// Decompiled by ImCzf233, L wanghang

package customskinloader.utils;

import com.mojang.authlib.GameProfile;
import java.net.URL;
import java.net.URLDecoder;
import net.minecraft.client.multiplayer.ServerData;
import java.util.regex.Matcher;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.Minecraft;
import java.io.File;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class MinecraftUtil
{
    private static ArrayList<String> minecraftVersion;
    private static String minecraftMainVersion;
    private static final Pattern MINECRAFT_VERSION_PATTERN;
    private static final Pattern MINECRAFT_CORE_FILE_PATTERN;
    private static final Pattern LIBRARY_FILE_PATTERN;
    
    public static File getMinecraftDataDir() {
        return Minecraft.getMinecraft().mcDataDir;
    }
    
    public static TextureManager getTextureManager() {
        return Minecraft.getMinecraft().getTextureManager();
    }
    
    public static SkinManager getSkinManager() {
        return Minecraft.getMinecraft().getSkinManager();
    }
    
    public static ArrayList<String> getMinecraftVersions() {
        if (MinecraftUtil.minecraftVersion != null && !MinecraftUtil.minecraftVersion.isEmpty()) {
            return MinecraftUtil.minecraftVersion;
        }
        testProbe();
        return MinecraftUtil.minecraftVersion;
    }
    
    public static String getMinecraftVersionText() {
        final StringBuilder sb = new StringBuilder();
        for (final String version : getMinecraftVersions()) {
            sb.append(version).append(" ");
        }
        return StringUtils.trim(sb.toString());
    }
    
    public static String getMinecraftMainVersion() {
        if (MinecraftUtil.minecraftMainVersion != null) {
            return MinecraftUtil.minecraftMainVersion;
        }
        for (final String version : getMinecraftVersions()) {
            Matcher m = null;
            try {
                m = MinecraftUtil.MINECRAFT_VERSION_PATTERN.matcher(version);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (m != null) {
                if (!m.matches()) {
                    continue;
                }
                MinecraftUtil.minecraftMainVersion = m.group(m.groupCount());
                break;
            }
        }
        return MinecraftUtil.minecraftMainVersion;
    }
    
    public static String getServerAddress() {
        final ServerData data = Minecraft.getMinecraft().getCurrentServerData();
        if (data == null) {
            return null;
        }
        return data.serverIP;
    }
    
    public static String getStandardServerAddress() {
        return HttpUtil0.parseAddress(getServerAddress());
    }
    
    public static boolean isLanServer() {
        return HttpUtil0.isLanServer(getStandardServerAddress());
    }
    
    public static String getCurrentUsername() {
        return Minecraft.getMinecraft().getSession().getProfile().getName();
    }
    
    private static void testProbe() {
        MinecraftUtil.minecraftVersion.clear();
        final URL[] classpath;
        final URL[] urls = classpath = JavaUtil.getClasspath();
        for (final URL url : classpath) {
            Matcher m = null;
            try {
                m = MinecraftUtil.MINECRAFT_CORE_FILE_PATTERN.matcher(URLDecoder.decode(url.getPath(), "UTF-8"));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (m != null) {
                if (m.matches()) {
                    MinecraftUtil.minecraftVersion.add(m.group(2));
                }
            }
        }
    }
    
    public static boolean isCoreFile(final URL url) {
        return regexMatch(url, MinecraftUtil.MINECRAFT_CORE_FILE_PATTERN);
    }
    
    public static boolean isLibraryFile(final URL url) {
        return regexMatch(url, MinecraftUtil.LIBRARY_FILE_PATTERN);
    }
    
    private static boolean regexMatch(final URL url, final Pattern p) {
        Matcher m;
        try {
            m = p.matcher(URLDecoder.decode(url.getPath(), "UTF-8"));
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return m.matches();
    }
    
    public static String getCredential(final GameProfile profile) {
        return (profile == null || profile.hashCode() == 0) ? null : ((profile.getId() == null) ? profile.getName() : String.format("%s-%s", profile.getName(), profile.getId()));
    }
    
    static {
        MinecraftUtil.minecraftVersion = new ArrayList<String>();
        MinecraftUtil.minecraftMainVersion = null;
        MINECRAFT_VERSION_PATTERN = Pattern.compile(".*?(\\d+\\.\\d+[\\.]?\\d*).*?");
        MINECRAFT_CORE_FILE_PATTERN = Pattern.compile("^(.*?)/versions/([^\\/\\\\]*?)/([^\\/\\\\]*?).jar$");
        LIBRARY_FILE_PATTERN = Pattern.compile("^(.*?)/libraries/(.*?)/([^\\/\\\\]*?).jar$");
    }
}
