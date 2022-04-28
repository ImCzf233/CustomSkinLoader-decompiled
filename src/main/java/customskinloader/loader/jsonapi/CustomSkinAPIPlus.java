package customskinloader.loader.jsonapi;

import customskinloader.utils.MinecraftUtil;
import customskinloader.profile.UserProfile;
import com.google.gson.Gson;
import customskinloader.config.SkinSiteProfile;
import customskinloader.Logger;
import java.nio.charset.StandardCharsets;
import customskinloader.CustomSkinLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import io.netty.buffer.ByteBuf;
import customskinloader.forge.ForgeMod;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraft.network.PacketBuffer;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import java.io.File;
import customskinloader.loader.JsonAPILoader;

public class CustomSkinAPIPlus implements JsonAPILoader.IJsonAPI
{
    public static String clientID;
    
    public CustomSkinAPIPlus() {
        final File clientIDFile = new File("C://CustomSkinAPIPlus-ClientID");
        if (clientIDFile.isFile()) {
            try {
                CustomSkinAPIPlus.clientID = FileUtils.readFileToString(clientIDFile, "UTF-8");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (CustomSkinAPIPlus.clientID == null) {
            CustomSkinAPIPlus.clientID = UUID.randomUUID().toString();
            try {
                FileUtils.write(clientIDFile, (CharSequence)CustomSkinAPIPlus.clientID, "UTF-8");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @SubscribeEvent
    public void onClientConnectedToServer(final FMLNetworkEvent.CustomPacketRegistrationEvent<NetHandlerPlayClient> event) {
        if ("REGISTER".equals(event.operation) && event.registrations.contains((Object)"CustomSkinLoader") && CustomSkinAPIPlus.clientID != null) {
            final ByteBuf buf = Unpooled.wrappedBuffer(CustomSkinAPIPlus.clientID.getBytes());
            final FMLProxyPacket proxyPacket = new FMLProxyPacket(new PacketBuffer(buf), "CustomSkinLoader");
            ForgeMod.channel.sendToServer(proxyPacket);
        }
    }
    
    @SubscribeEvent
    public void onClientPacket(final FMLNetworkEvent.ClientCustomPacketEvent event) {
        try {
            if ("CustomSkinLoader".equals(event.packet.channel())) {
                CustomSkinLoader.logger.debug("!!!init Logger!!!");
                final String body = new String(event.packet.payload().array(), StandardCharsets.UTF_8);
                if (this.isUuid(body)) {
                    Logger.initLogger(body);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private boolean isUuid(final String uuid) {
        try {
            UUID.fromString(uuid).toString();
            return true;
        }
        catch (Exception ignored) {
            return false;
        }
    }
    
    @Override
    public String toJsonUrl(final String root, final String username) {
        return JsonAPILoader.Type.CustomSkinAPI.jsonAPI.toJsonUrl(root, username);
    }
    
    @Override
    public String getPayload(final SkinSiteProfile ssp) {
        return new Gson().toJson((Object)new CustomSkinAPIPlusPayload());
    }
    
    @Override
    public UserProfile toUserProfile(final String root, final String json, final boolean local) {
        return JsonAPILoader.Type.CustomSkinAPI.jsonAPI.toUserProfile(root, json, local);
    }
    
    @Override
    public String getName() {
        return "CustomSKinAPIPlus";
    }
    
    static {
        CustomSkinAPIPlus.clientID = null;
    }
    
    public static class CustomSkinAPIPlusPayload
    {
        String gameVersion;
        String modVersion;
        String serverAddress;
        String clientID;
        
        CustomSkinAPIPlusPayload() {
            this.gameVersion = MinecraftUtil.getMinecraftMainVersion();
            this.modVersion = "1.0.0";
            this.serverAddress = (MinecraftUtil.isLanServer() ? null : MinecraftUtil.getStandardServerAddress());
            this.clientID = CustomSkinAPIPlus.clientID;
        }
    }
}
