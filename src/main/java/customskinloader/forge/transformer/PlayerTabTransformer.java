// Decompiled by ImCzf233, L wanghang

package customskinloader.forge.transformer;

import java.util.ListIterator;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ClassNode;
import customskinloader.forge.TransformerManager;

public class PlayerTabTransformer
{
    @TransformerManager.TransformTarget(className = "net.minecraft.client.gui.GuiPlayerTabOverlay", methodNames = { "renderPlayerlist", "renderPlayerlist" }, desc = "(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V")
    public static class ScoreObjectiveTransformer implements TransformerManager.IMethodTransformer
    {
        @Override
        public void transform(final ClassNode cn, final MethodNode mn) {
            for (final AbstractInsnNode node : mn.instructions.toArray()) {
                if (node instanceof VarInsnNode) {
                    final VarInsnNode varNode = (VarInsnNode)node;
                    if (varNode.getOpcode() != 54 || varNode.var != 11) {
                        continue;
                    }
                    mn.instructions.set(varNode.getPrevious(), (AbstractInsnNode)new InsnNode(4));
                }
            }
        }
    }
}
