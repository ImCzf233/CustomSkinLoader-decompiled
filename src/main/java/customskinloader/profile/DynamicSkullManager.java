package customskinloader.profile;

import com.google.gson.Gson;
import org.apache.commons.io.FilenameUtils;
import customskinloader.utils.HttpRequestUtil;
import customskinloader.utils.HttpTextureUtil;
import customskinloader.utils.HttpUtil0;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import java.io.File;
import customskinloader.CustomSkinLoader;
import java.lang.reflect.Type;
import com.mojang.util.UUIDTypeAdapter;
import java.util.UUID;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Iterables;
import com.mojang.authlib.properties.Property;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.GameProfile;
import java.util.Map;

public class DynamicSkullManager
{
    private Map<GameProfile, SkullTexture> dynamicTextures;
    private Map<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> staticTextures;
    private List<GameProfile> loadingList;
    
    public DynamicSkullManager() {
        this.dynamicTextures = new ConcurrentHashMap<GameProfile, SkullTexture>();
        this.staticTextures = new ConcurrentHashMap<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>();
        this.loadingList = new ArrayList<GameProfile>();
    }
    
    private void parseGameProfile(final GameProfile profile) {
        final Property textureProperty = (Property)Iterables.getFirst(profile.getProperties().get("textures"), (Object)null);
        if (textureProperty == null) {
            this.staticTextures.put(profile, Maps.newHashMap());
            return;
        }
        final String value = textureProperty.getValue();
        if (StringUtils.isBlank((CharSequence)value)) {
            this.staticTextures.put(profile, Maps.newHashMap());
            return;
        }
        final String json = new String(Base64.decodeBase64(value), Charsets.UTF_8);
        final Gson gson = new GsonBuilder().registerTypeAdapter((Type)UUID.class, (Object)new UUIDTypeAdapter()).create();
        final SkullTexture result = (SkullTexture)gson.fromJson(json, (Class)SkullTexture.class);
        if (result == null) {
            this.staticTextures.put(profile, Maps.newHashMap());
            return;
        }
        this.staticTextures.put(profile, (result.textures == null || !result.textures.containsKey(MinecraftProfileTexture.Type.SKIN)) ? Maps.newHashMap() : this.parseTextures(result.textures));
        if (StringUtils.isNotEmpty((CharSequence)result.index)) {
            final File indexFile = new File(CustomSkinLoader.DATA_DIR, result.index);
            try {
                final String index = FileUtils.readFileToString(indexFile, "UTF-8");
                if (StringUtils.isNotEmpty((CharSequence)index)) {
                    final String[] skins = (String[])CustomSkinLoader.GSON.fromJson(index, (Class)String[].class);
                    if (skins != null && skins.length != 0) {
                        result.skins = Lists.newArrayList(skins);
                    }
                }
            }
            catch (Exception e) {
                CustomSkinLoader.logger.warning("Exception occurs while parsing index file: " + e.toString());
            }
        }
        if (!CustomSkinLoader.config.enableDynamicSkull || result.skins == null || result.skins.isEmpty()) {
            return;
        }
        CustomSkinLoader.logger.info("Try to load Dynamic Skull: " + json);
        for (int i = 0; i < result.skins.size(); ++i) {
            final String skin = result.skins.get(i);
            if (HttpUtil0.isLocal(skin)) {
                final File skinFile = new File(CustomSkinLoader.DATA_DIR, skin);
                if (skinFile.isFile() && skinFile.length() > 0L) {
                    final String fakeUrl = HttpTextureUtil.getLocalFakeUrl(skin);
                    result.skins.set(i, fakeUrl);
                }
                else {
                    result.skins.remove(i--);
                }
            }
            else {
                final HttpRequestUtil.HttpResponce responce = HttpRequestUtil.makeHttpRequest(new HttpRequestUtil.HttpRequest(skin).setCacheFile(HttpTextureUtil.getCacheFile(FilenameUtils.getBaseName(skin))).setCacheTime(0).setLoadContent(false));
                if (!responce.success) {
                    result.skins.remove(i--);
                }
            }
        }
        if (result.skins.isEmpty()) {
            CustomSkinLoader.logger.info("Failed: Nothing loaded.");
            return;
        }
        result.interval = Math.max(result.interval, 50);
        if (result.fromZero) {
            result.startTime = System.currentTimeMillis();
        }
        result.period = result.interval * result.skins.size();
        CustomSkinLoader.logger.info("Successfully loaded Dynamic Skull: " + new Gson().toJson((Object)result));
        this.dynamicTextures.put(profile, result);
        this.staticTextures.remove(profile);
    }
    
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> parseTextures(final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures) {
        final MinecraftProfileTexture skin = textures.get(MinecraftProfileTexture.Type.SKIN);
        final String skinUrl = skin.getUrl();
        if (!HttpUtil0.isLocal(skinUrl)) {
            return textures;
        }
        final File skinFile = new File(CustomSkinLoader.DATA_DIR, skinUrl);
        if (!skinFile.isFile()) {
            return Maps.newHashMap();
        }
        textures.put(MinecraftProfileTexture.Type.SKIN, ModelManager0.getProfileTexture(HttpTextureUtil.getLocalFakeUrl(skinUrl), null));
        return textures;
    }
    
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTexture(final GameProfile profile) {
        if (this.staticTextures.get(profile) != null) {
            return this.staticTextures.get(profile);
        }
        if (this.loadingList.contains(profile)) {
            return Maps.newHashMap();
        }
        if (this.dynamicTextures.containsKey(profile)) {
            final SkullTexture texture = this.dynamicTextures.get(profile);
            final long time = System.currentTimeMillis() - texture.startTime;
            final int index = (int)Math.floor((double)(time % texture.period / texture.interval));
            final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Maps.newHashMap();
            map.put(MinecraftProfileTexture.Type.SKIN, ModelManager0.getProfileTexture(texture.skins.get(index), null));
            return map;
        }
        this.loadingList.add(profile);
        final Thread loadThread = new Thread() {
            @Override
            public void run() {
                DynamicSkullManager.this.parseGameProfile(profile);
                DynamicSkullManager.this.loadingList.remove(profile);
            }
        };
        loadThread.setName("Skull " + profile.hashCode());
        loadThread.start();
        return Maps.newHashMap();
    }
    
    public static class SkullTexture
    {
        public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures;
        public String index;
        public ArrayList<String> skins;
        public int interval;
        public boolean fromZero;
        public long startTime;
        public int period;
    }
}
