package customskinloader.profile;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.HashMap;

public class ModelManager0
{
    private static HashMap<String, Model> models;
    private static MinecraftProfileTexture.Type typeElytra;
    
    public static Model getEnumModel(final String model) {
        return ModelManager0.models.get(model);
    }
    
    public static boolean isSkin(final Model model) {
        return model == Model.SKIN_DEFAULT || model == Model.SKIN_SLIM;
    }
    
    public static boolean isElytraSupported() {
        return ModelManager0.typeElytra != null;
    }
    
    public static UserProfile toUserProfile(final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> profile) {
        final UserProfile userProfile = new UserProfile();
        if (profile == null) {
            return userProfile;
        }
        final MinecraftProfileTexture skin = profile.get(MinecraftProfileTexture.Type.SKIN);
        userProfile.skinUrl = ((skin == null) ? null : skin.getUrl());
        userProfile.model = ((skin == null) ? null : skin.getMetadata("model"));
        if (StringUtils.isEmpty((CharSequence)userProfile.model)) {
            userProfile.model = "default";
        }
        final MinecraftProfileTexture cape = profile.get(MinecraftProfileTexture.Type.CAPE);
        userProfile.capeUrl = ((cape == null) ? null : cape.getUrl());
        return userProfile;
    }
    
    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> fromUserProfile(final UserProfile profile) {
        final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Maps.newHashMap();
        if (profile == null) {
            return map;
        }
        if (profile.skinUrl != null) {
            Map<String, String> metadata = null;
            if ("slim".equals(profile.model) || "auto".equals(profile.model)) {
                metadata = Maps.newHashMap();
                metadata.put("model", profile.model);
            }
            map.put(MinecraftProfileTexture.Type.SKIN, getProfileTexture(profile.skinUrl, metadata));
        }
        if (profile.capeUrl != null) {
            map.put(MinecraftProfileTexture.Type.CAPE, getProfileTexture(profile.capeUrl, null));
        }
        if (ModelManager0.typeElytra != null && profile.elytraUrl != null) {
            map.put(ModelManager0.typeElytra, getProfileTexture(profile.elytraUrl, null));
        }
        return map;
    }
    
    public static MinecraftProfileTexture getProfileTexture(final String url, final Map<String, String> metadata) {
        return new MinecraftProfileTexture(url, (Map)metadata);
    }
    
    static {
        ModelManager0.models = new HashMap<String, Model>();
        ModelManager0.typeElytra = null;
        for (final MinecraftProfileTexture.Type type : MinecraftProfileTexture.Type.values()) {
            if (type.ordinal() == 2) {
                ModelManager0.typeElytra = type;
            }
        }
        ModelManager0.models.put("default", Model.SKIN_DEFAULT);
        ModelManager0.models.put("slim", Model.SKIN_SLIM);
        ModelManager0.models.put("cape", Model.CAPE);
        if (ModelManager0.typeElytra != null) {
            ModelManager0.models.put("elytra", Model.ELYTRA);
            ModelManager0.models.put("elytron", Model.ELYTRA);
        }
    }
    
    public enum Model
    {
        SKIN_DEFAULT, 
        SKIN_SLIM, 
        CAPE, 
        ELYTRA;
    }
}
