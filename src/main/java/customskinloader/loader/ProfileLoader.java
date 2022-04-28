package customskinloader.loader;

import customskinloader.profile.UserProfile;
import com.mojang.authlib.GameProfile;
import customskinloader.config.SkinSiteProfile;
import java.util.Map;
import customskinloader.plugin.PluginLoader;
import java.util.HashMap;

public class ProfileLoader
{
    private static final IProfileLoader[] DEFAULT_LOADERS;
    public static final HashMap<String, IProfileLoader> LOADERS;
    
    private static HashMap<String, IProfileLoader> initLoaders() {
        final HashMap<String, IProfileLoader> loaders = new HashMap<String, IProfileLoader>();
        for (final IProfileLoader loader : ProfileLoader.DEFAULT_LOADERS) {
            loaders.put(loader.getName().toLowerCase(), loader);
        }
        loaders.putAll(PluginLoader.loadPlugins());
        return loaders;
    }
    
    static {
        DEFAULT_LOADERS = new IProfileLoader[] { new MojangAPILoader(), new JsonAPILoader(JsonAPILoader.Type.CustomSkinAPI), new JsonAPILoader(JsonAPILoader.Type.CustomSkinAPIPlus), new JsonAPILoader(JsonAPILoader.Type.UniSkinAPI), new LegacyLoader() };
        LOADERS = initLoaders();
    }
    
    public interface IProfileLoader
    {
        UserProfile loadProfile(final SkinSiteProfile p0, final GameProfile p1) throws Exception;
        
        boolean compare(final SkinSiteProfile p0, final SkinSiteProfile p1);
        
        String getName();
        
        void init(final SkinSiteProfile p0);
    }
}
