package customskinloader.config;

import customskinloader.utils.HttpUtil0;
import java.util.Collections;
import java.util.List;

public class Config
{
    public String version;
    public final List<SkinSiteProfile> loadlist;
    public boolean enableDynamicSkull;
    public boolean enableTransparentSkin;
    public boolean forceIgnoreHttpsCertificate;
    public boolean forceLoadAllTextures;
    public boolean enableCape;
    public int threadPoolSize;
    public int retryTime;
    public int cacheExpiry;
    public boolean forceUpdateSkull;
    public boolean enableLocalProfileCache;
    public boolean enableCacheAutoClean;
    public boolean forceDisableCache;
    
    public Config() {
        this.loadlist = Collections.singletonList(SkinSiteProfile.createUniSkinAPI("DoMCer", "http://mwskin.domcer.com:25566/"));
        this.enableDynamicSkull = true;
        this.enableTransparentSkin = true;
        this.forceIgnoreHttpsCertificate = false;
        this.forceLoadAllTextures = false;
        this.enableCape = true;
        this.threadPoolSize = 3;
        this.retryTime = 1;
        this.cacheExpiry = 1;
        this.forceUpdateSkull = false;
        this.enableLocalProfileCache = false;
        this.enableCacheAutoClean = false;
        this.forceDisableCache = false;
        this.version = "1.0.0";
    }
    
    public static Config loadConfig0() {
        final Config config = new Config();
        if (config.forceIgnoreHttpsCertificate) {
            HttpUtil0.ignoreHttpsCertificate();
        }
        return config;
    }
}
