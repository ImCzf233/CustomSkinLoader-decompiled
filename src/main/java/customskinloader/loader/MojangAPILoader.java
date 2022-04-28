package customskinloader.loader;

import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import com.google.common.collect.Iterables;
import com.mojang.authlib.properties.Property;
import com.google.common.collect.Maps;
import com.mojang.authlib.yggdrasil.response.MinecraftProfilePropertiesResponse;
import com.mojang.authlib.properties.PropertyMap;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import java.util.Collections;
import customskinloader.utils.HttpRequestUtil;
import java.lang.reflect.Type;
import com.mojang.util.UUIDTypeAdapter;
import java.util.UUID;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.Map;
import com.google.common.collect.Multimap;
import customskinloader.profile.ModelManager0;
import customskinloader.CustomSkinLoader;
import customskinloader.profile.UserProfile;
import com.mojang.authlib.GameProfile;
import customskinloader.config.SkinSiteProfile;

public class MojangAPILoader implements ProfileLoader.IProfileLoader
{
    private static final String MOJANG_API_ROOT = "https://api{DO_NOT_MODIFY}.mojang.com/";
    
    @Override
    public UserProfile loadProfile(final SkinSiteProfile ssp, final GameProfile gameProfile) throws Exception {
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = getTextures(gameProfile);
        if (!map.isEmpty()) {
            CustomSkinLoader.logger.info("Default profile will be used.");
            return ModelManager0.toUserProfile(map);
        }
        final String username = gameProfile.getName();
        GameProfile newGameProfile = loadGameProfile(ssp.apiRoot, username);
        if (newGameProfile == null) {
            CustomSkinLoader.logger.info("Profile not found.(" + username + "'s profile not found.)");
            return null;
        }
        newGameProfile = fillGameProfile(ssp.sessionRoot, newGameProfile);
        map = getTextures(newGameProfile);
        if (!map.isEmpty()) {
            gameProfile.getProperties().putAll((Multimap)newGameProfile.getProperties());
            return ModelManager0.toUserProfile(map);
        }
        CustomSkinLoader.logger.info("Profile not found.(" + username + " doesn't have skin/cape.)");
        return null;
    }
    
    public static GameProfile loadGameProfile(final String apiRoot, final String username) {
        final Gson gson = new GsonBuilder().registerTypeAdapter((Type)UUID.class, (Object)new UUIDTypeAdapter()).create();
        final HttpRequestUtil.HttpResponce responce = HttpRequestUtil.makeHttpRequest(new HttpRequestUtil.HttpRequest(apiRoot + "profiles/minecraft").setCacheTime(600).setPayload(gson.toJson((Object)Collections.singletonList(username))));
        if (StringUtils.isEmpty((CharSequence)responce.content)) {
            return null;
        }
        final GameProfile[] profiles = (GameProfile[])gson.fromJson(responce.content, (Class)GameProfile[].class);
        if (profiles.length == 0) {
            return null;
        }
        final GameProfile gameProfile = profiles[0];
        if (gameProfile.getId() == null) {
            return null;
        }
        return new GameProfile(gameProfile.getId(), gameProfile.getName());
    }
    
    public static GameProfile fillGameProfile(final String sessionRoot, final GameProfile profile) {
        final HttpRequestUtil.HttpResponce responce = HttpRequestUtil.makeHttpRequest(new HttpRequestUtil.HttpRequest(sessionRoot + "session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(profile.getId())).setCacheTime(90));
        if (StringUtils.isEmpty((CharSequence)responce.content)) {
            return profile;
        }
        final Gson gson = new GsonBuilder().registerTypeAdapter((Type)UUID.class, (Object)new UUIDTypeAdapter()).registerTypeAdapter((Type)PropertyMap.class, (Object)new PropertyMap.Serializer()).create();
        final MinecraftProfilePropertiesResponse propertiesResponce = (MinecraftProfilePropertiesResponse)gson.fromJson(responce.content, (Class)MinecraftProfilePropertiesResponse.class);
        final GameProfile newGameProfile = new GameProfile(propertiesResponce.getId(), propertiesResponce.getName());
        newGameProfile.getProperties().putAll((Multimap)propertiesResponce.getProperties());
        return newGameProfile;
    }
    
    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(final GameProfile gameProfile) {
        if (gameProfile == null) {
            return Maps.newHashMap();
        }
        final Property textureProperty = (Property)Iterables.getFirst((Iterable)gameProfile.getProperties().get("textures"), (Object)null);
        if (textureProperty == null) {
            return Maps.newHashMap();
        }
        final String value = textureProperty.getValue();
        if (StringUtils.isBlank((CharSequence)value)) {
            return Maps.newHashMap();
        }
        final String json = new String(Base64.decodeBase64(value), Charsets.UTF_8);
        final Gson gson = new GsonBuilder().registerTypeAdapter((Type)UUID.class, (Object)new UUIDTypeAdapter()).create();
        final MinecraftTexturesPayload result = (MinecraftTexturesPayload)gson.fromJson(json, (Class)MinecraftTexturesPayload.class);
        if (result == null || result.getTextures() == null) {
            return Maps.newHashMap();
        }
        return (Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>)result.getTextures();
    }
    
    @Override
    public boolean compare(final SkinSiteProfile ssp0, final SkinSiteProfile ssp1) {
        return true;
    }
    
    @Override
    public String getName() {
        return "MojangAPI";
    }
    
    @Override
    public void init(final SkinSiteProfile ssp) {
        if (ssp.apiRoot == null) {
            ssp.apiRoot = "https://api.mojang.com/";
        }
        if (ssp.sessionRoot == null) {
            ssp.sessionRoot = "https://sessionserver.mojang.com/";
        }
    }
    
    public static String getMojangApiRoot() {
        return "https://api{DO_NOT_MODIFY}.mojang.com/".replace("{DO_NOT_MODIFY}", "");
    }
}
