package customskinloader.forge;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.io.File;
import org.objectweb.asm.tree.MethodNode;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.tree.ClassNode;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import customskinloader.Logger;

public class TransformerManager
{
    public static Logger logger;
    public Map<String, IClassTransformer> classMap;
    public Map<String, Map<String, IMethodTransformer>> map;
    
    public TransformerManager(final IMethodTransformer... methodTransformers) {
        this(new IClassTransformer[0], methodTransformers);
    }
    
    public TransformerManager(final IClassTransformer[] classTransformers, final IMethodTransformer[] methodTransformers) {
        this.classMap = new HashMap<String, IClassTransformer>();
        this.map = new HashMap<String, Map<String, IMethodTransformer>>();
        for (final IClassTransformer t : classTransformers) {
            final TransformTarget tt = this.getTransformTarget(t.getClass());
            if (tt != null) {
                this.addClassTransformer(tt.className(), t);
            }
        }
        for (final IMethodTransformer t2 : methodTransformers) {
            final TransformTarget tt = this.getTransformTarget(t2.getClass());
            if (tt != null) {
                this.addMethodTransformer(tt, tt.className(), t2);
            }
        }
    }
    
    public TransformTarget getTransformTarget(final Class<?> cl) {
        TransformerManager.logger.info("[CSL DEBUG] REGISTERING TRANSFORMER %s", cl.getName());
        if (!cl.isAnnotationPresent(TransformTarget.class)) {
            TransformerManager.logger.info("[CSL DEBUG] ERROR occurs while parsing Annotation.");
            return null;
        }
        return cl.getAnnotation(TransformTarget.class);
    }
    
    private void addClassTransformer(final String className, final IClassTransformer transformer) {
        if (!this.classMap.containsKey(className)) {
            this.classMap.put(className, transformer);
            TransformerManager.logger.info("[CSL DEBUG] REGISTERING CLASS %s", className);
        }
    }
    
    public void addMethodTransformer(final TransformTarget target, final String className, final IMethodTransformer transformer) {
        if (!this.map.containsKey(className)) {
            this.map.put(className, new HashMap<String, IMethodTransformer>());
        }
        for (final String methodName : target.methodNames()) {
            this.map.get(className).put(methodName + target.desc(), transformer);
            TransformerManager.logger.info("[CSL DEBUG] REGISTERING METHOD %s(%s)", className, methodName + target.desc());
        }
    }
    
    public ClassNode transform(final ClassNode classNode) {
        final IClassTransformer transformer = this.classMap.get(FMLDeobfuscatingRemapper.INSTANCE.map(classNode.name).replace("/", "."));
        if (transformer != null) {
            try {
                transformer.transform(classNode);
                TransformerManager.logger.info("[CSL DEBUG] Successfully transformed class %s", classNode.name);
            }
            catch (Exception e) {
                TransformerManager.logger.warning("[CSL DEBUG] An error happened when transforming class %s.", classNode.name);
                TransformerManager.logger.warning(e);
            }
        }
        return classNode;
    }
    
    public MethodNode transform(final ClassNode classNode, final MethodNode methodNode, final String className, final String methodName, final String methodDesc) {
        final Map<String, IMethodTransformer> transMap = this.map.get(className);
        final String methodTarget = methodName + methodDesc;
        if (transMap != null && transMap.containsKey(methodTarget)) {
            try {
                transMap.get(methodTarget).transform(classNode, methodNode);
                TransformerManager.logger.info("[CSL DEBUG] Successfully transformed method %s in class %s", methodName, className);
            }
            catch (Exception e) {
                TransformerManager.logger.warning("[CSL DEBUG] An error happened when transforming method %s in class %s.", methodTarget, className);
                TransformerManager.logger.warning(e);
            }
        }
        return methodNode;
    }
    
    static {
        TransformerManager.logger = new Logger(new File("./CustomSkinLoader/ForgePlugin.log"));
    }
    
    public interface IMethodTransformer
    {
        void transform(final ClassNode p0, final MethodNode p1);
    }
    
    public interface IClassTransformer
    {
        void transform(final ClassNode p0);
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface TransformTarget {
        String className();
        
        String[] methodNames() default {};
        
        String desc() default "";
    }
}
