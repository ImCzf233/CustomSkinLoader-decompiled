package customskinloader.forge.transformer;

import java.util.ListIterator;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ClassNode;
import customskinloader.forge.TransformerManager;

public class SpectatorMenuTransformer
{
    @TransformerManager.TransformTarget(className = "net.minecraft.client.gui.spectator.PlayerMenuObject", methodNames = { "<init>" }, desc = "(Lcom/mojang/authlib/GameProfile;)V")
    public static class PlayerMenuObjectTransformer implements TransformerManager.IMethodTransformer
    {
        @Override
        public void transform(final ClassNode cn, final MethodNode mn) {
            final InsnList il = mn.instructions;
            final ListIterator<AbstractInsnNode> li = (ListIterator<AbstractInsnNode>)il.iterator();
            boolean flag = false;
            while (li.hasNext()) {
                final AbstractInsnNode ain = li.next();
                if (ain.getOpcode() != 184) {
                    continue;
                }
                if (flag) {
                    il.set(ain, (AbstractInsnNode)new MethodInsnNode(184, "customskinloader/fake/FakeClientPlayer", "getDownloadImageSkin", "(Lnet/minecraft/util/ResourceLocation;Ljava/lang/String;)Lnet/minecraft/client/renderer/ThreadDownloadImageData;", false));
                    break;
                }
                il.set(ain, (AbstractInsnNode)new MethodInsnNode(184, "customskinloader/fake/FakeClientPlayer", "getLocationSkin", "(Ljava/lang/String;)Lnet/minecraft/util/ResourceLocation;", false));
                flag = true;
            }
        }
    }
}
