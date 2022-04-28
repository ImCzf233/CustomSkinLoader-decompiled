package customskinloader.utils;

import org.apache.commons.codec.digest.DigestUtils;
import customskinloader.CustomSkinLoader;
import org.apache.commons.io.FilenameUtils;
import java.io.File;

public class HttpTextureUtil
{
    private static final String LEGACY_MARK = "(LEGACY)";
    private static final String LOCAL_MARK = "(LOCAL)";
    private static final String LOCAL_LEGACY_MARK = "(LOCAL_LEGACY)";
    public static File defaultCacheDir;
    
    public static void cleanCacheDir() {
        if (HttpTextureUtil.defaultCacheDir != null) {
            HttpTextureUtil.defaultCacheDir.delete();
            HttpTextureUtil.defaultCacheDir.mkdirs();
        }
    }
    
    public static File getCacheDir() {
        return (HttpTextureUtil.defaultCacheDir == null) ? new File(MinecraftUtil.getMinecraftDataDir(), "assets/skins") : HttpTextureUtil.defaultCacheDir;
    }
    
    public static HttpTextureInfo toHttpTextureInfo(String fakeUrl) {
        final HttpTextureInfo info = new HttpTextureInfo();
        if (fakeUrl.startsWith("http")) {
            info.url = fakeUrl;
            info.hash = FilenameUtils.getBaseName(fakeUrl);
            info.cacheFile = getCacheFile(info.hash);
            return info;
        }
        if (fakeUrl.startsWith("(LOCAL_LEGACY)")) {
            fakeUrl = fakeUrl.replace("(LOCAL_LEGACY)", "");
            final String[] t = fakeUrl.split(",", 2);
            if (t.length != 2) {
                return info;
            }
            info.cacheFile = new File(CustomSkinLoader.DATA_DIR, t[1]);
            info.hash = t[0];
            return info;
        }
        else {
            if (fakeUrl.startsWith("(LOCAL)")) {
                fakeUrl = fakeUrl.replace("(LOCAL)", "");
                info.cacheFile = new File(CustomSkinLoader.DATA_DIR, fakeUrl);
                info.hash = FilenameUtils.getBaseName(fakeUrl);
                return info;
            }
            if (fakeUrl.startsWith("(LEGACY)")) {
                fakeUrl = fakeUrl.replace("(LEGACY)", "");
                info.url = fakeUrl;
                info.hash = DigestUtils.sha1Hex(info.url);
                info.cacheFile = HttpRequestUtil.getCacheFile(info.hash);
                return info;
            }
            return info;
        }
    }
    
    public static String getLegacyFakeUrl(final String url) {
        return "(LEGACY)" + url;
    }
    
    public static String getLocalFakeUrl(final String path) {
        return "(LOCAL)" + path;
    }
    
    public static String getLocalLegacyFakeUrl(final String path, final String hash) {
        return "(LOCAL_LEGACY)" + hash + "," + path;
    }
    
    public static String getHash(final String url, final long size, final long lastModified) {
        return DigestUtils.sha1Hex(size + url + lastModified);
    }
    
    public static File getCacheFile(final String hash) {
        return getCacheFile(HttpTextureUtil.defaultCacheDir, hash);
    }
    
    public static File getCacheFile(final File cacheDir, final String hash) {
        return new File(new File(cacheDir, (hash.length() > 2) ? hash.substring(0, 2) : "xx"), hash);
    }
    
    public static class HttpTextureInfo
    {
        public String url;
        public File cacheFile;
        public String hash;
        
        public HttpTextureInfo() {
            this.url = "";
        }
    }
}
