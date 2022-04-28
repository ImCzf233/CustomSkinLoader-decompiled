// Decompiled by ImCzf233, L wanghang

package customskinloader.forge.transformer;

import java.util.ListIterator;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ClassNode;
import customskinloader.forge.TransformerManager;

@Deprecated
public class FakeSkinManagerTransformer
{
    private static final String TARGET_CLASS = "net/minecraft/client/renderer/ThreadDownloadImageData";
    private static final String NEW_TARGET_CLASS = "net/minecraft/client/renderer/texture/ThreadDownloadImageData";
    private static final String CALLBACK_CLASS = "net/minecraft/client/resources/SkinManager$SkinAvailableCallback";
    
    @TransformerManager.TransformTarget(className = "customskinloader.fake.FakeSkinManager", methodNames = { "<init>" }, desc = "(Lnet/minecraft/client/renderer/texture/TextureManager;Ljava/io/File;Lcom/mojang/authlib/minecraft/MinecraftSessionService;)V")
    public static class InitTransformer implements TransformerManager.IMethodTransformer
    {
        @Override
        public void transform(final ClassNode cn, final MethodNode mn) {
            TransformerManager.logger.info("1.13 detected, FakeSkinManager will be transformed.");
            final InsnList il = mn.instructions;
            for (final AbstractInsnNode ain : il.toArray()) {
                if (ain instanceof MethodInsnNode) {
                    final MethodInsnNode min = (MethodInsnNode)ain;
                    if (min.owner.equals("net/minecraft/client/renderer/ThreadDownloadImageData")) {
                        min.owner = "net/minecraft/client/renderer/texture/ThreadDownloadImageData";
                    }
                    if (!min.owner.equals("net/minecraft/client/resources/SkinManager$SkinAvailableCallback") || !min.name.equals("skinAvailable")) {
                        continue;
                    }
                    min.name = "onSkinTextureAvailable";
                }
                else {
                    if (!(ain instanceof TypeInsnNode)) {
                        continue;
                    }
                    final TypeInsnNode tin = (TypeInsnNode)ain;
                    if (!tin.desc.equals("net/minecraft/client/renderer/ThreadDownloadImageData")) {
                        continue;
                    }
                    tin.desc = "net/minecraft/client/renderer/texture/ThreadDownloadImageData";
                }
            }
        }
    }
}
