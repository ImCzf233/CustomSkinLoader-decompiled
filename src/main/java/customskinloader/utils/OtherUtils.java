// Decompiled by ImCzf233, L wanghang

package customskinloader.utils;

import java.lang.reflect.Field;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.IReloadableResourceManager;
import customskinloader.font.CustomFontRender;
import net.minecraft.util.ResourceLocation;
import java.util.List;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.Minecraft;

public class OtherUtils
{
    public static void init() {
        final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        try {
            final Field field = SimpleReloadableResourceManager.class.getDeclaredField("reloadListeners");
            field.setAccessible(true);
            final List<IResourceManagerReloadListener> list = (List<IResourceManagerReloadListener>)field.get(Minecraft.getMinecraft().getResourceManager());
            list.remove(fontRenderer);
            Minecraft.getMinecraft().fontRendererObj = new CustomFontRender(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
            ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener((IResourceManagerReloadListener)Minecraft.getMinecraft().fontRendererObj);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
