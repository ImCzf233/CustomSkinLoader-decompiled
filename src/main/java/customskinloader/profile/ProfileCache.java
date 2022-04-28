package customskinloader.profile;

import org.apache.commons.io.FileUtils;
import customskinloader.CustomSkinLoader;
import customskinloader.utils.TimeUtil;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.io.File;

public class ProfileCache
{
    public static File PROFILE_CACHE_DIR;
    private Map<String, CachedProfile> cachedProfiles;
    private Map<String, UserProfile> localProfiles;
    
    public ProfileCache() {
        this.cachedProfiles = new ConcurrentHashMap<String, CachedProfile>();
        this.localProfiles = new ConcurrentHashMap<String, UserProfile>();
        if (!ProfileCache.PROFILE_CACHE_DIR.exists()) {
            ProfileCache.PROFILE_CACHE_DIR.mkdir();
        }
    }
    
    public void remove(final String username) {
        this.cachedProfiles.remove(username.toLowerCase());
    }
    
    public boolean isExist(final String username) {
        return this.cachedProfiles.containsKey(username.toLowerCase());
    }
    
    public boolean isLoading(final String username) {
        final CachedProfile cp = this.cachedProfiles.get(username.toLowerCase());
        return cp != null && cp.loading;
    }
    
    public boolean isReady(final String username) {
        final CachedProfile cp = this.cachedProfiles.get(username.toLowerCase());
        return cp != null && (cp.loading || cp.expiryTime > TimeUtil.getCurrentUnixTimestamp());
    }
    
    public boolean isExpired(final String username) {
        final CachedProfile cp = this.cachedProfiles.get(username.toLowerCase());
        return cp == null || cp.expiryTime <= TimeUtil.getCurrentUnixTimestamp();
    }
    
    public UserProfile getProfile(final String username) {
        return this.getCachedProfile(username).profile;
    }
    
    public long getExpiry(final String username) {
        return this.getCachedProfile(username).expiryTime;
    }
    
    public UserProfile getLocalProfile(final String username) {
        if (this.localProfiles.containsKey(username.toLowerCase())) {
            return this.localProfiles.get(username.toLowerCase());
        }
        return this.loadLocalProfile(username);
    }
    
    public void setLoading(final String username, final boolean loading) {
        this.getCachedProfile(username).loading = loading;
    }
    
    public void updateCache(final String username, final UserProfile profile) {
        this.updateCache(username, profile, CustomSkinLoader.config.enableLocalProfileCache);
    }
    
    public void updateCache(final String username, final UserProfile profile, final boolean saveLocalProfile) {
        final CachedProfile cp = this.getCachedProfile(username);
        cp.profile = profile;
        cp.expiryTime = TimeUtil.getUnixTimestamp(CustomSkinLoader.config.cacheExpiry);
        if (!saveLocalProfile) {
            return;
        }
        this.saveLocalProfile(username, profile);
    }
    
    private CachedProfile getCachedProfile(final String username) {
        CachedProfile cp = this.cachedProfiles.get(username.toLowerCase());
        if (cp != null) {
            return cp;
        }
        cp = new CachedProfile();
        this.cachedProfiles.put(username.toLowerCase(), cp);
        return cp;
    }
    
    private UserProfile loadLocalProfile(final String username) {
        final File localProfile = new File(ProfileCache.PROFILE_CACHE_DIR, username.toLowerCase() + ".json");
        if (!localProfile.exists()) {
            this.localProfiles.put(username.toLowerCase(), null);
        }
        try {
            final String json = FileUtils.readFileToString(localProfile, "UTF-8");
            final UserProfile profile = (UserProfile)CustomSkinLoader.GSON.fromJson(json, (Class)UserProfile.class);
            this.localProfiles.put(username.toLowerCase(), profile);
            CustomSkinLoader.logger.info("Successfully load LocalProfile.");
            return profile;
        }
        catch (Exception e) {
            CustomSkinLoader.logger.info("Failed to load LocalProfile.(" + e.toString() + ")");
            this.localProfiles.put(username.toLowerCase(), null);
            return null;
        }
    }
    
    private void saveLocalProfile(final String username, final UserProfile profile) {
        final String json = CustomSkinLoader.GSON.toJson((Object)profile);
        final File localProfile = new File(ProfileCache.PROFILE_CACHE_DIR, username + ".json");
        if (localProfile.exists()) {
            localProfile.delete();
        }
        try {
            FileUtils.write(localProfile, (CharSequence)json, "UTF-8");
            CustomSkinLoader.logger.info("Successfully save LocalProfile.");
        }
        catch (Exception e) {
            CustomSkinLoader.logger.info("Failed to save LocalProfile.(" + e.toString() + ")");
        }
    }
    
    static {
        ProfileCache.PROFILE_CACHE_DIR = new File(CustomSkinLoader.DATA_DIR, "ProfileCache");
    }
}
