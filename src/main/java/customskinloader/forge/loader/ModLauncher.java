package customskinloader.forge.loader;

import customskinloader.forge.transformer.FakeSkinManagerTransformer;
import customskinloader.forge.transformer.PlayerTabTransformer;
import customskinloader.forge.transformer.SkinManagerTransformer;
import java.util.Iterator;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ClassNode;
import customskinloader.forge.TransformerManager;

@Deprecated
public class ModLauncher
{
    private static final TransformerManager.IMethodTransformer[] TRANFORMERS;
    private TransformerManager transformerManager;
    private static volatile ModLauncher INSTANCE;
    
    public ModLauncher() {
        this.transformerManager = new TransformerManager(ModLauncher.TRANFORMERS);
    }
    
    public ClassNode transform(final ClassNode input) {
        for (final MethodNode mn : input.methods) {
            this.transformerManager.transform(input, mn, input.name, mn.name, mn.desc);
        }
        return input;
    }
    
    public static ModLauncher instance() {
        if (ModLauncher.INSTANCE == null) {
            synchronized (ModLauncher.class) {
                if (ModLauncher.INSTANCE == null) {
                    ModLauncher.INSTANCE = new ModLauncher();
                }
            }
        }
        return ModLauncher.INSTANCE;
    }
    
    static {
        TRANFORMERS = new TransformerManager.IMethodTransformer[] { new SkinManagerTransformer.InitTransformer(), new SkinManagerTransformer.LoadSkinTransformer(), new SkinManagerTransformer.LoadProfileTexturesTransformer(), new SkinManagerTransformer.LoadSkinFromCacheTransformer(), new PlayerTabTransformer.ScoreObjectiveTransformer(), new FakeSkinManagerTransformer.InitTransformer() };
        ModLauncher.INSTANCE = null;
    }
}
