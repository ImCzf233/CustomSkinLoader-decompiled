package customskinloader.loader.jsonapi;

import java.util.Map;
import java.util.List;
import customskinloader.config.SkinSiteProfile;
import java.util.Iterator;
import customskinloader.profile.ModelManager0;
import customskinloader.utils.HttpTextureUtil;
import org.apache.commons.lang3.StringUtils;
import customskinloader.CustomSkinLoader;
import customskinloader.profile.UserProfile;
import customskinloader.loader.JsonAPILoader;

public class UniSkinAPI implements JsonAPILoader.IJsonAPI
{
    private static final String TEXTURES = "textures/";
    private static final String SUFFIX = ".json";
    
    @Override
    public String toJsonUrl(final String root, final String username) {
        return root + username + ".json";
    }
    
    @Override
    public UserProfile toUserProfile(final String root, final String json, final boolean local) {
        final UniSkinAPIProfile profile = (UniSkinAPIProfile)CustomSkinLoader.GSON.fromJson(json, (Class)UniSkinAPIProfile.class);
        final UserProfile p = new UserProfile();
        if (StringUtils.isNotBlank((CharSequence)profile.cape)) {
            p.capeUrl = root + "textures/" + profile.cape;
            if (local) {
                p.capeUrl = HttpTextureUtil.getLocalFakeUrl(p.capeUrl);
            }
        }
        if (profile.skins == null || profile.skins.isEmpty()) {
            return p;
        }
        if (profile.model_preference == null || profile.model_preference.isEmpty()) {
            return p;
        }
        boolean hasSkin = false;
        for (final String model : profile.model_preference) {
            final ModelManager0.Model enumModel = ModelManager0.getEnumModel(model);
            if (enumModel != null) {
                if (StringUtils.isEmpty((CharSequence)profile.skins.get(model))) {
                    continue;
                }
                if (ModelManager0.isSkin(enumModel)) {
                    if (hasSkin) {
                        continue;
                    }
                    hasSkin = true;
                }
                String url = root + "textures/" + profile.skins.get(model);
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
        return "UniSkinAPI";
    }
    
    private class UniSkinAPIProfile
    {
        public String player_name;
        public long last_update;
        public List<String> model_preference;
        public Map<String, String> skins;
        public String cape;
    }
}
