// Decompiled by ImCzf233, L wanghang

package customskinloader.forge.transformer;

import java.util.Iterator;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ClassNode;
import customskinloader.forge.TransformerManager;

public class SkinManagerTransformer
{
    @TransformerManager.TransformTarget(className = "net.minecraft.client.resources.SkinManager", methodNames = { "<init>" }, desc = "(Lnet/minecraft/client/renderer/texture/TextureManager;Ljava/io/File;Lcom/mojang/authlib/minecraft/MinecraftSessionService;)V")
    public static class InitTransformer implements TransformerManager.IMethodTransformer
    {
        @Override
        public void transform(final ClassNode cn, final MethodNode mn) {
            boolean hasField = false;
            for (final FieldNode fn : cn.fields) {
                if (fn.name.equals("fakeManager")) {
                    hasField = true;
                    break;
                }
            }
            if (!hasField) {
                cn.fields.add(new FieldNode(2, "fakeManager", "Lcustomskinloader/fake/FakeSkinManager;", (String)null, (Object)null));
            }
            mn.instructions.clear();
            mn.instructions.add((AbstractInsnNode)new VarInsnNode(25, 0));
            mn.instructions.add((AbstractInsnNode)new MethodInsnNode(183, "java/lang/Object", "<init>", "()V", false));
            mn.instructions.add((AbstractInsnNode)new VarInsnNode(25, 0));
            mn.instructions.add((AbstractInsnNode)new TypeInsnNode(187, "customskinloader/fake/FakeSkinManager"));
            mn.instructions.add((AbstractInsnNode)new InsnNode(89));
            mn.instructions.add((AbstractInsnNode)new VarInsnNode(25, 1));
            mn.instructions.add((AbstractInsnNode)new VarInsnNode(25, 2));
            mn.instructions.add((AbstractInsnNode)new VarInsnNode(25, 3));
            mn.instructions.add((AbstractInsnNode)new MethodInsnNode(183, "customskinloader/fake/FakeSkinManager", "<init>", "(Lnet/minecraft/client/renderer/texture/TextureManager;Ljava/io/File;Lcom/mojang/authlib/minecraft/MinecraftSessionService;)V", false));
            mn.instructions.add((AbstractInsnNode)new FieldInsnNode(181, "net/minecraft/client/resources/SkinManager", "fakeManager", "Lcustomskinloader/fake/FakeSkinManager;"));
            mn.instructions.add((AbstractInsnNode)new InsnNode(177));
        }
    }
    
    @TransformerManager.TransformTarget(className = "net.minecraft.client.resources.SkinManager", methodNames = { "loadSkin", "loadSkin" }, desc = "(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;)Lnet/minecraft/util/ResourceLocation;")
    public static class LoadSkinTransformer implements TransformerManager.IMethodTransformer
    {
        @Override
        public void transform(final ClassNode cn, final MethodNode mn) {
            mn.instructions.clear();
            mn.instructions.add((AbstractInsnNode)new VarInsnNode(25, 0));
            mn.instructions.add((AbstractInsnNode)new FieldInsnNode(180, "net/minecraft/client/resources/SkinManager", "fakeManager", "Lcustomskinloader/fake/FakeSkinManager;"));
            mn.instructions.add((AbstractInsnNode)new VarInsnNode(25, 1));
            mn.instructions.add((AbstractInsnNode)new VarInsnNode(25, 2));
            mn.instructions.add((AbstractInsnNode)new VarInsnNode(25, 3));
            mn.instructions.add((AbstractInsnNode)new MethodInsnNode(182, "customskinloader/fake/FakeSkinManager", "loadSkin", "(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;)Lnet/minecraft/util/ResourceLocation;", false));
            mn.instructions.add((AbstractInsnNode)new InsnNode(176));
        }
    }
    
    @TransformerManager.TransformTarget(className = "net.minecraft.client.resources.SkinManager", methodNames = { "loadProfileTextures", "loadProfileTextures" }, desc = "(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;Z)V")
    public static class LoadProfileTexturesTransformer implements TransformerManager.IMethodTransformer
    {
        @Override
        public void transform(final ClassNode cn, final MethodNode mn) {
            mn.instructions.clear();
            mn.instructions.add((AbstractInsnNode)new VarInsnNode(25, 0));
            mn.instructions.add((AbstractInsnNode)new FieldInsnNode(180, "net/minecraft/client/resources/SkinManager", "fakeManager", "Lcustomskinloader/fake/FakeSkinManager;"));
            mn.instructions.add((AbstractInsnNode)new VarInsnNode(25, 1));
            mn.instructions.add((AbstractInsnNode)new VarInsnNode(25, 2));
            mn.instructions.add((AbstractInsnNode)new VarInsnNode(21, 3));
            mn.instructions.add((AbstractInsnNode)new MethodInsnNode(182, "customskinloader/fake/FakeSkinManager", "loadProfileTextures", "(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;Z)V", false));
            mn.instructions.add((AbstractInsnNode)new InsnNode(177));
        }
    }
    
    @TransformerManager.TransformTarget(className = "net.minecraft.client.resources.SkinManager", methodNames = { "loadSkinFromCache", "loadSkinFromCache" }, desc = "(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;")
    public static class LoadSkinFromCacheTransformer implements TransformerManager.IMethodTransformer
    {
        @Override
        public void transform(final ClassNode cn, final MethodNode mn) {
            mn.instructions.clear();
            mn.instructions.add((AbstractInsnNode)new VarInsnNode(25, 0));
            mn.instructions.add((AbstractInsnNode)new FieldInsnNode(180, "net/minecraft/client/resources/SkinManager", "fakeManager", "Lcustomskinloader/fake/FakeSkinManager;"));
            mn.instructions.add((AbstractInsnNode)new VarInsnNode(25, 1));
            mn.instructions.add((AbstractInsnNode)new MethodInsnNode(182, "customskinloader/fake/FakeSkinManager", "loadSkinFromCache", "(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;", false));
            mn.instructions.add((AbstractInsnNode)new InsnNode(176));
        }
    }
}
