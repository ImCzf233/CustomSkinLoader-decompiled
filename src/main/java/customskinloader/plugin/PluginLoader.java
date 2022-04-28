package customskinloader.plugin;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import org.apache.commons.io.FileUtils;
import java.net.URL;
import java.util.ArrayList;
import java.io.File;
import customskinloader.CustomSkinLoader;
import customskinloader.loader.ProfileLoader;
import java.util.HashMap;

public class PluginLoader
{
    public static HashMap<String, ProfileLoader.IProfileLoader> loadPlugins() {
        final File pluginsDir = new File(CustomSkinLoader.DATA_DIR, "Plugins");
        final ArrayList<URL> urls = new ArrayList<URL>();
        if (!pluginsDir.isDirectory()) {
            pluginsDir.mkdirs();
        }
        else {
            for (final File plugin : FileUtils.listFiles(pluginsDir, new String[] { "jar", "zip" }, false)) {
                try {
                    urls.add(plugin.toURI().toURL());
                    CustomSkinLoader.logger.info("Found a jar or zip file: " + plugin.getName());
                }
                catch (MalformedURLException ex) {}
            }
        }
        final ServiceLoader<ICustomSkinLoaderPlugin> sl = ServiceLoader.load(ICustomSkinLoaderPlugin.class, new URLClassLoader(urls.toArray(new URL[0]), PluginLoader.class.getClassLoader()));
        final HashMap<String, ProfileLoader.IProfileLoader> profileLoaders = new HashMap<String, ProfileLoader.IProfileLoader>();
        for (final ICustomSkinLoaderPlugin cslPlugin : sl) {
            final ProfileLoader.IProfileLoader profileLoader = cslPlugin.getProfileLoader();
            profileLoaders.put(profileLoader.getName().toLowerCase(), profileLoader);
            CustomSkinLoader.logger.info("Add profile loader: " + profileLoader.getName());
        }
        return profileLoaders;
    }
}
