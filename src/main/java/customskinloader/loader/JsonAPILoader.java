package customskinloader.loader;

import customskinloader.loader.jsonapi.UniSkinAPI;
import customskinloader.loader.jsonapi.CustomSkinAPIPlus;
import customskinloader.loader.jsonapi.CustomSkinAPI;
import customskinloader.utils.HttpRequestUtil;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.io.File;
import customskinloader.utils.HttpUtil0;
import customskinloader.CustomSkinLoader;
import org.apache.commons.lang3.StringUtils;
import customskinloader.profile.UserProfile;
import com.mojang.authlib.GameProfile;
import customskinloader.config.SkinSiteProfile;

public class JsonAPILoader implements ProfileLoader.IProfileLoader
{
    private Type type;
    
    public JsonAPILoader(final Type type) {
        this.type = type;
    }
    
    @Override
    public UserProfile loadProfile(final SkinSiteProfile ssp, final GameProfile gameProfile) throws Exception {
        final String username = gameProfile.getName();
        if (StringUtils.isEmpty((CharSequence)ssp.root)) {
            CustomSkinLoader.logger.info("Root not defined.");
            return null;
        }
        final boolean local = HttpUtil0.isLocal(ssp.root);
        final String jsonUrl = this.type.jsonAPI.toJsonUrl(ssp.root, username);
        String json;
        if (local) {
            final File jsonFile = new File(CustomSkinLoader.DATA_DIR, jsonUrl);
            if (!jsonFile.exists()) {
                CustomSkinLoader.logger.info("Profile File not found.");
                return null;
            }
            json = IOUtils.toString((InputStream)new FileInputStream(jsonFile), StandardCharsets.UTF_8);
        }
        else {
            final HttpRequestUtil.HttpResponce responce = HttpRequestUtil.makeHttpRequest(new HttpRequestUtil.HttpRequest(jsonUrl).setCacheTime(90).setUserAgent(ssp.userAgent).setPayload(this.type.jsonAPI.getPayload(ssp)));
            json = responce.content;
        }
        if (json == null || json.equals("")) {
            CustomSkinLoader.logger.info("Profile not found.");
            return null;
        }
        final ErrorProfile profile = (ErrorProfile)CustomSkinLoader.GSON.fromJson(json, (Class)ErrorProfile.class);
        if (profile.errno != 0) {
            CustomSkinLoader.logger.info("Error " + profile.errno + ": " + profile.msg);
            return null;
        }
        final UserProfile p = this.type.jsonAPI.toUserProfile(ssp.root, json, local);
        if (p == null || p.isEmpty()) {
            CustomSkinLoader.logger.info("Both skin and cape not found.");
            return null;
        }
        return p;
    }
    
    @Override
    public boolean compare(final SkinSiteProfile ssp0, final SkinSiteProfile ssp1) {
        return !StringUtils.isNoneEmpty(new CharSequence[] { ssp0.root }) || ssp0.root.equalsIgnoreCase(ssp1.root);
    }
    
    @Override
    public String getName() {
        return this.type.jsonAPI.getName();
    }
    
    @Override
    public void init(final SkinSiteProfile ssp) {
        if (HttpUtil0.isLocal(ssp.root)) {
            final File f = new File(ssp.root);
            if (!f.exists()) {
                f.mkdirs();
            }
        }
    }
    
    public static class ErrorProfile
    {
        public int errno;
        public String msg;
    }
    
    public enum Type
    {
        CustomSkinAPI((IJsonAPI)new CustomSkinAPI()), 
        CustomSkinAPIPlus((IJsonAPI)new CustomSkinAPIPlus()), 
        UniSkinAPI((IJsonAPI)new UniSkinAPI());
        
        public IJsonAPI jsonAPI;
        
        private Type(final IJsonAPI jsonAPI) {
            this.jsonAPI = jsonAPI;
        }
    }
    
    public interface IJsonAPI
    {
        String toJsonUrl(final String p0, final String p1);
        
        String getPayload(final SkinSiteProfile p0);
        
        UserProfile toUserProfile(final String p0, final String p1, final boolean p2);
        
        String getName();
    }
}
