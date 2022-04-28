package customskinloader;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import com.google.gson.GsonBuilder;
import customskinloader.loader.ProfileLoader;
import customskinloader.profile.UserProfile;
import customskinloader.profile.ModelManager0;
import com.google.common.collect.Maps;
import customskinloader.utils.MinecraftUtil;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.Map;
import com.mojang.authlib.GameProfile;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import customskinloader.profile.DynamicSkullManager;
import customskinloader.profile.ProfileCache;
import customskinloader.config.Config;
import com.google.gson.Gson;
import customskinloader.config.SkinSiteProfile;
import java.io.File;

public class CustomSkinLoader
{
    public static final String CustomSkinLoader_VERSION = "1.0.0";
    public static final String CustomSkinLoader_FULL_VERSION = "1.0.0";
    public static final File DATA_DIR;
    public static final File LOG_FILE;
    public static final SkinSiteProfile[] DEFAULT_LOAD_LIST;
    public static final Gson GSON;
    public static final Logger logger;
    public static final Config config;
    public static final ProfileCache profileCache;
    private static final DynamicSkullManager dynamicSkullManager;
    private static final ThreadFactory defaultFactory;
    private static final ThreadFactory customFactory;
    private static final ThreadPoolExecutor threadPool;
    
    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> loadProfile(final GameProfile gameProfile) {
        final String username = gameProfile.getName();
        final String credential = MinecraftUtil.getCredential(gameProfile);
        if (username == null) {
            CustomSkinLoader.logger.warning("Could not load profile: username is null.");
            return Maps.newHashMap();
        }
        final String tempName = Thread.currentThread().getName();
        Thread.currentThread().setName(username);
        UserProfile profile;
        if (CustomSkinLoader.profileCache.isReady(credential)) {
            CustomSkinLoader.logger.info("Cached profile will be used.");
            profile = CustomSkinLoader.profileCache.getProfile(credential);
            if (profile == null) {
                CustomSkinLoader.logger.warning("(!Cached Profile is empty!) Expiry:" + CustomSkinLoader.profileCache.getExpiry(credential));
                if (CustomSkinLoader.profileCache.isExpired(credential)) {
                    profile = loadProfile0(gameProfile);
                }
            }
            else {
                CustomSkinLoader.logger.info(profile.toString(CustomSkinLoader.profileCache.getExpiry(credential)));
            }
        }
        else {
            CustomSkinLoader.profileCache.setLoading(credential, true);
            profile = loadProfile0(gameProfile);
        }
        Thread.currentThread().setName(tempName);
        return ModelManager0.fromUserProfile(profile);
    }
    
    public static UserProfile loadProfile0(final GameProfile gameProfile) {
        final String username = gameProfile.getName();
        final String credential = MinecraftUtil.getCredential(gameProfile);
        CustomSkinLoader.profileCache.setLoading(credential, true);
        CustomSkinLoader.logger.info("Loading " + username + "'s profile.");
        if (CustomSkinLoader.config.loadlist.isEmpty()) {
            CustomSkinLoader.logger.info("LoadList is Empty.");
            return null;
        }
        UserProfile profile0 = new UserProfile();
        for (int i = 0; i < CustomSkinLoader.config.loadlist.size(); ++i) {
            final SkinSiteProfile ssp = CustomSkinLoader.config.loadlist.get(i);
            CustomSkinLoader.logger.info(i + 1 + "/" + CustomSkinLoader.config.loadlist.size() + " Try to load profile from '" + ssp.name + "'.");
            final ProfileLoader.IProfileLoader loader = ProfileLoader.LOADERS.get(ssp.type.toLowerCase());
            if (loader == null) {
                CustomSkinLoader.logger.info("Type '" + ssp.type + "' is not defined.");
            }
            else {
                UserProfile profile2 = null;
                try {
                    profile2 = loader.loadProfile(ssp, gameProfile);
                }
                catch (Exception e) {
                    CustomSkinLoader.logger.warning("Exception occurs while loading.");
                    CustomSkinLoader.logger.warning(e);
                    if (e.getCause() != null) {
                        CustomSkinLoader.logger.warning("Caused By:");
                        CustomSkinLoader.logger.warning(e.getCause());
                    }
                }
                if (profile2 != null) {
                    if (!CustomSkinLoader.config.forceLoadAllTextures) {
                        profile0 = profile2;
                        break;
                    }
                    profile0.mix(profile2);
                    if (profile0.isFull()) {
                        break;
                    }
                }
            }
        }
        if (!profile0.isEmpty()) {
            CustomSkinLoader.logger.info(username + "'s profile loaded.");
            if (!CustomSkinLoader.config.enableCape) {
                profile0.capeUrl = null;
            }
            CustomSkinLoader.profileCache.updateCache(credential, profile0);
            CustomSkinLoader.profileCache.setLoading(credential, false);
            CustomSkinLoader.logger.info(profile0.toString(CustomSkinLoader.profileCache.getExpiry(credential)));
            return profile0;
        }
        CustomSkinLoader.logger.info(username + "'s profile not found in load list.");
        if (CustomSkinLoader.config.enableLocalProfileCache) {
            final UserProfile profile3 = CustomSkinLoader.profileCache.getLocalProfile(credential);
            if (profile3 != null) {
                CustomSkinLoader.profileCache.updateCache(credential, profile3, false);
                CustomSkinLoader.profileCache.setLoading(credential, false);
                CustomSkinLoader.logger.info(username + "'s LocalProfile will be used.");
                CustomSkinLoader.logger.info(profile3.toString(CustomSkinLoader.profileCache.getExpiry(credential)));
                return profile3;
            }
            CustomSkinLoader.logger.info(username + "'s LocalProfile not found.");
        }
        CustomSkinLoader.profileCache.setLoading(credential, false);
        return null;
    }
    
    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> loadProfileFromCache(final GameProfile gameProfile) {
        final String username = gameProfile.getName();
        final String credential = MinecraftUtil.getCredential(gameProfile);
        if (username == null || credential == null) {
            return CustomSkinLoader.dynamicSkullManager.getTexture(gameProfile);
        }
        Label_0071: {
            if (CustomSkinLoader.config.forceUpdateSkull) {
                if (!CustomSkinLoader.profileCache.isReady(credential)) {
                    break Label_0071;
                }
            }
            else if (!CustomSkinLoader.profileCache.isExist(credential)) {
                break Label_0071;
            }
            final UserProfile profile = CustomSkinLoader.profileCache.getProfile(credential);
            return ModelManager0.fromUserProfile(profile);
        }
        if (!CustomSkinLoader.profileCache.isLoading(credential)) {
            CustomSkinLoader.profileCache.setLoading(credential, true);
            final String[] tempName = new String[1];
            final String str = "";
            final Runnable loadThread = () -> {
                tempName[0] = Thread.currentThread().getName();
                Thread.currentThread().setName(str + "'s skull");
                loadProfile0(gameProfile);
                Thread.currentThread().setName(tempName[0]);
                return;
            };
            if (CustomSkinLoader.config.forceUpdateSkull) {
                new Thread(loadThread).start();
            }
            else {
                CustomSkinLoader.threadPool.execute(loadThread);
            }
        }
        return Maps.newHashMap();
    }
    
    private static Logger initLogger() {
        final Logger logger = new Logger(CustomSkinLoader.LOG_FILE);
        logger.info("CustomSkinLoader 1.0.0");
        logger.info("DataDir: " + CustomSkinLoader.DATA_DIR.getAbsolutePath());
        logger.info("Operating System: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
        logger.info("Java Version: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        logger.info("Java VM Version: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        logger.info("Minecraft: " + MinecraftUtil.getMinecraftMainVersion() + "(" + MinecraftUtil.getMinecraftVersionText() + ")");
        return logger;
    }
    
    static {
        DATA_DIR = new File(MinecraftUtil.getMinecraftDataDir(), "CustomSkinLoader");
        LOG_FILE = new File(CustomSkinLoader.DATA_DIR, "CustomSkinLoader.log");
        DEFAULT_LOAD_LIST = new SkinSiteProfile[] { SkinSiteProfile.createUniSkinAPI("DoMCer", "http://mwskin.domcer.com:25566/") };
        GSON = new GsonBuilder().setPrettyPrinting().create();
        logger = initLogger();
        config = Config.loadConfig0();
        profileCache = new ProfileCache();
        dynamicSkullManager = new DynamicSkullManager();
        defaultFactory = Executors.defaultThreadFactory();
        final Thread[] t = new Thread[1];
        customFactory = (r -> {
            t[0] = CustomSkinLoader.defaultFactory.newThread(r);
            if (r instanceof Thread) {
                t[0].setName(((Thread) r).getName());
            }
            return t[0];
        });
        threadPool = new ThreadPoolExecutor(CustomSkinLoader.config.threadPoolSize, CustomSkinLoader.config.threadPoolSize, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(333), CustomSkinLoader.customFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
    }
}
