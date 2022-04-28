package customskinloader.loader;

import javax.annotation.Nonnull;
import com.mojang.util.UUIDTypeAdapter;
import customskinloader.profile.ModelManager0;
import customskinloader.utils.HttpRequestUtil;
import customskinloader.utils.HttpTextureUtil;
import java.io.File;
import customskinloader.CustomSkinLoader;
import customskinloader.utils.HttpUtil0;
import org.apache.commons.lang3.StringUtils;
import customskinloader.profile.UserProfile;
import com.mojang.authlib.GameProfile;
import customskinloader.config.SkinSiteProfile;

public class LegacyLoader implements ProfileLoader.IProfileLoader
{
    public static final String USERNAME_PLACEHOLDER = "{USERNAME}";
    public static final String UUID_PLACEHOLDER = "{UUID}";
    
    @Override
    public UserProfile loadProfile(final SkinSiteProfile ssp, final GameProfile gameProfile) throws Exception {
        final String username = gameProfile.getName();
        final UserProfile profile = new UserProfile();
        if (StringUtils.isNoneEmpty(new CharSequence[] { ssp.skin })) {
            final String skin = this.expandURL(ssp.skin, username);
            if (HttpUtil0.isLocal(ssp.skin)) {
                final File skinFile = new File(CustomSkinLoader.DATA_DIR, skin);
                if (skinFile.exists() && skinFile.isFile()) {
                    profile.skinUrl = HttpTextureUtil.getLocalLegacyFakeUrl(skin, HttpTextureUtil.getHash(skin, skinFile.length(), skinFile.lastModified()));
                }
            }
            else {
                final HttpRequestUtil.HttpResponce responce = HttpRequestUtil.makeHttpRequest(new HttpRequestUtil.HttpRequest(skin).setUserAgent(ssp.userAgent).setCheckPNG(ssp.checkPNG != null && ssp.checkPNG).setLoadContent(false).setCacheTime(90));
                if (responce.success) {
                    profile.skinUrl = HttpTextureUtil.getLegacyFakeUrl(skin);
                }
            }
            profile.model = (profile.hasSkinUrl() ? ssp.model : null);
        }
        if (StringUtils.isNoneEmpty(new CharSequence[] { ssp.cape })) {
            final String cape = this.expandURL(ssp.cape, username);
            if (HttpUtil0.isLocal(ssp.cape)) {
                final File capeFile = new File(CustomSkinLoader.DATA_DIR, cape);
                if (capeFile.exists() && capeFile.isFile()) {
                    profile.capeUrl = HttpTextureUtil.getLocalLegacyFakeUrl(cape, HttpTextureUtil.getHash(cape, capeFile.length(), capeFile.lastModified()));
                }
            }
            else {
                final HttpRequestUtil.HttpResponce responce = HttpRequestUtil.makeHttpRequest(new HttpRequestUtil.HttpRequest(cape).setUserAgent(ssp.userAgent).setCheckPNG(ssp.checkPNG != null && ssp.checkPNG).setLoadContent(false).setCacheTime(90));
                if (responce.success) {
                    profile.capeUrl = HttpTextureUtil.getLegacyFakeUrl(cape);
                }
            }
        }
        if (ModelManager0.isElytraSupported() && StringUtils.isNoneEmpty(new CharSequence[] { ssp.elytra })) {
            final String elytra = this.expandURL(ssp.elytra, username);
            if (HttpUtil0.isLocal(ssp.elytra)) {
                final File elytraFile = new File(CustomSkinLoader.DATA_DIR, elytra);
                if (elytraFile.exists() && elytraFile.isFile()) {
                    profile.elytraUrl = HttpTextureUtil.getLocalLegacyFakeUrl(elytra, HttpTextureUtil.getHash(elytra, elytraFile.length(), elytraFile.lastModified()));
                }
            }
            else {
                final HttpRequestUtil.HttpResponce responce = HttpRequestUtil.makeHttpRequest(new HttpRequestUtil.HttpRequest(elytra).setUserAgent(ssp.userAgent).setCheckPNG(ssp.checkPNG != null && ssp.checkPNG).setLoadContent(false).setCacheTime(90));
                if (responce.success) {
                    profile.elytraUrl = HttpTextureUtil.getLegacyFakeUrl(elytra);
                }
            }
        }
        if (profile.isEmpty()) {
            CustomSkinLoader.logger.info("Both skin and cape not found.");
            return null;
        }
        return profile;
    }
    
    @Override
    public boolean compare(final SkinSiteProfile ssp0, final SkinSiteProfile ssp1) {
        return !StringUtils.isNoneEmpty(new CharSequence[] { ssp0.skin }) || ssp0.skin.equalsIgnoreCase(ssp1.skin) || !StringUtils.isNoneEmpty(new CharSequence[] { ssp0.cape }) || ssp0.cape.equalsIgnoreCase(ssp1.cape);
    }
    
    @Override
    public String getName() {
        return "Legacy";
    }
    
    @Override
    public void init(final SkinSiteProfile ssp) {
        if (HttpUtil0.isLocal(ssp.skin)) {
            this.initFolder(ssp.skin);
        }
        if (HttpUtil0.isLocal(ssp.cape)) {
            this.initFolder(ssp.cape);
        }
        if (HttpUtil0.isLocal(ssp.elytra)) {
            this.initFolder(ssp.elytra);
        }
    }
    
    private void initFolder(final String target) {
        final String file = target.replace("{USERNAME}", "init");
        final File folder = new File(CustomSkinLoader.DATA_DIR, file).getParentFile();
        if (folder != null && !folder.exists()) {
            folder.mkdirs();
        }
    }
    
    private String expandURL(final String url, final String username) {
        String t = url.replace("{USERNAME}", username);
        if (t.contains("{UUID}")) {
            t = t.replace("{UUID}", this.getMojangUUID(username));
        }
        return t;
    }
    
    @Nonnull
    private String getMojangUUID(final String username) {
        final GameProfile profile = MojangAPILoader.loadGameProfile(MojangAPILoader.getMojangApiRoot(), username);
        if (profile == null) {
            CustomSkinLoader.logger.info("UUID for %s not found.", username);
            return "{ABORT}";
        }
        return UUIDTypeAdapter.fromUUID(profile.getId());
    }
}
