// Decompiled by ImCzf233, L wanghang

package customskinloader.fake;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import customskinloader.CustomSkinLoader;
import com.google.common.collect.Maps;
import net.minecraft.util.StringUtils;
import java.util.UUID;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import com.mojang.authlib.GameProfile;
import customskinloader.utils.MinecraftUtil;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import java.util.Map;

public class FakeClientPlayer
{
    public static Map<ResourceLocation, ITextureObject> textureCache;
    
    public static ThreadDownloadImageData getDownloadImageSkin(final ResourceLocation resourceLocationIn, final String username) {
        final TextureManager textman = MinecraftUtil.getTextureManager();
        final ITextureObject ito = textman.getTexture(resourceLocationIn);
        if (ito == null || !(ito instanceof ThreadDownloadImageData)) {
            final SkinManager skinman = MinecraftUtil.getSkinManager();
            final UUID offlineUUID = getOfflineUUID(username);
            final GameProfile offlineProfile = new GameProfile(offlineUUID, username);
            final ResourceLocation defaultSkin = DefaultPlayerSkin.getDefaultSkin(offlineUUID);
            final ITextureObject defaultSkinObj = (ITextureObject)new SimpleTexture(defaultSkin);
            textman.loadTexture(resourceLocationIn, defaultSkinObj);
            skinman.loadProfileTextures(offlineProfile, (SkinManager.SkinAvailableCallback)new LegacyBuffer(resourceLocationIn), false);
        }
        if (ito instanceof ThreadDownloadImageData) {
            return (ThreadDownloadImageData)ito;
        }
        return null;
    }
    
    public static ResourceLocation getLocationSkin(final String username) {
        return new ResourceLocation("skins/legacy-" + StringUtils.stripControlCodes(username));
    }
    
    public static UUID getOfflineUUID(final String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes());
    }
    
    static {
        FakeClientPlayer.textureCache = Maps.newHashMap();
    }
    
    public static class LegacyBuffer implements SkinManager.SkinAvailableCallback
    {
        ResourceLocation resourceLocationIn;
        boolean loaded;
        
        public LegacyBuffer(final ResourceLocation resourceLocationIn) {
            this.loaded = false;
            CustomSkinLoader.logger.debug("Loading Legacy Texture (" + resourceLocationIn + ")");
            this.resourceLocationIn = resourceLocationIn;
        }
        
        public void skinAvailable(final MinecraftProfileTexture.Type typeIn, final ResourceLocation location, final MinecraftProfileTexture profileTexture) {
            if (typeIn != MinecraftProfileTexture.Type.SKIN || this.loaded) {
                return;
            }
            final TextureManager textman = MinecraftUtil.getTextureManager();
            ITextureObject ito = textman.getTexture(location);
            if (ito == null) {
                ito = FakeClientPlayer.textureCache.get(location);
            }
            if (ito == null) {
                return;
            }
            this.loaded = true;
            textman.loadTexture(this.resourceLocationIn, ito);
            CustomSkinLoader.logger.debug("Legacy Texture (" + this.resourceLocationIn + ") Loaded as " + ito + " (" + location + ")");
        }
    }
}
