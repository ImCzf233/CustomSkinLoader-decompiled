// Decompiled by ImCzf233, L wanghang

package customskinloader.forge.transformer;

import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ClassNode;
import customskinloader.forge.TransformerManager;

public class TileEntitySkullTransformer
{
    @TransformerManager.TransformTarget(className = "net.minecraft.tileentity.TileEntitySkull", methodNames = { "updateGameProfile", "updateGameprofile" }, desc = "(Lcom/mojang/authlib/GameProfile;)Lcom/mojang/authlib/GameProfile;")
    public static class UpdateGameProfileTransformer implements TransformerManager.IMethodTransformer
    {
        @Override
        public void transform(final ClassNode cn, final MethodNode mn) {
            final InsnList il = new InsnList();
            il.add((AbstractInsnNode)new VarInsnNode(25, 0));
            il.add((AbstractInsnNode)new InsnNode(176));
            mn.instructions.insert(il);
        }
    }
}
