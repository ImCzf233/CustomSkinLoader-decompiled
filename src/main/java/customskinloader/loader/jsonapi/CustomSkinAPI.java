package customskinloader.loader.jsonapi;

import customskinloader.config.SkinSiteProfile;
import java.util.Iterator;
import customskinloader.profile.ModelManager0;
import java.util.Map;
import java.util.LinkedHashMap;
import customskinloader.utils.HttpTextureUtil;
import org.apache.commons.lang3.StringUtils;
import customskinloader.CustomSkinLoader;
import customskinloader.profile.UserProfile;
import customskinloader.loader.JsonAPILoader;

public class CustomSkinAPI implements JsonAPILoader.IJsonAPI
{
    private static final String TEXTURES = "textures/";
    private static final String SUFFIX = ".json";
    
    @Override
    public String toJsonUrl(final String root, final String username) {
        return root + username + ".json";
    }
    
    @Override
    public UserProfile toUserProfile(final String root, final String json, final boolean local) {
        final CustomSkinAPIProfile profile = (CustomSkinAPIProfile)CustomSkinLoader.GSON.fromJson(json, (Class)CustomSkinAPIProfile.class);
        final UserProfile p = new UserProfile();
        if (StringUtils.isNotBlank((CharSequence)profile.skin)) {
            p.skinUrl = root + "textures/" + profile.skin;
            if (local) {
                p.skinUrl = HttpTextureUtil.getLocalFakeUrl(p.skinUrl);
            }
        }
        if (StringUtils.isNotBlank((CharSequence)profile.cape)) {
            p.capeUrl = root + "textures/" + profile.cape;
            if (local) {
                p.capeUrl = HttpTextureUtil.getLocalFakeUrl(p.capeUrl);
            }
        }
        if (StringUtils.isNotBlank((CharSequence)profile.elytra)) {
            p.elytraUrl = root + "textures/" + profile.elytra;
            if (local) {
                p.elytraUrl = HttpTextureUtil.getLocalFakeUrl(p.elytraUrl);
            }
        }
        final Map<String, String> textures = new LinkedHashMap<String, String>();
        if (profile.skins != null) {
            textures.putAll(profile.skins);
        }
        if (profile.textures != null) {
            textures.putAll(profile.textures);
        }
        if (textures.isEmpty()) {
            return p;
        }
        boolean hasSkin = false;
        for (final String model : textures.keySet()) {
            final ModelManager0.Model enumModel = ModelManager0.getEnumModel(model);
            if (enumModel != null) {
                if (StringUtils.isEmpty((CharSequence)textures.get(model))) {
                    continue;
                }
                if (ModelManager0.isSkin(enumModel)) {
                    if (hasSkin) {
                        continue;
                    }
                    hasSkin = true;
                }
                String url = root + "textures/" + textures.get(model);
                if (local) {
                    url = HttpTextureUtil.getLocalFakeUrl(url);
                }
                p.put(enumModel, url);
            }
        }
        return p;
    }
    
    @Override
    public String getPayload(final SkinSiteProfile ssp) {
        return null;
    }
    
    @Override
    public String getName() {
        return "CustomSkinAPI";
    }
    
    private class CustomSkinAPIProfile
    {
        public String username;
        public LinkedHashMap<String, String> textures;
        public LinkedHashMap<String, String> skins;
        public String skin;
        public String cape;
        public String elytra;
    }
}
