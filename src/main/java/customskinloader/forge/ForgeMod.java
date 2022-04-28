// Decompiled by ImCzf233, L wanghang

package customskinloader.forge;

import customskinloader.utils.OtherUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import customskinloader.loader.jsonapi.CustomSkinAPIPlus;
import net.minecraft.command.ICommand;
import customskinloader.command.CommandMWSkin;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import java.util.Iterator;
import customskinloader.CustomSkinLoader;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = "customskinloader", name = "CustomSkinLoader", version = "1.0.0", clientSideOnly = true, acceptedMinecraftVersions = "[1.8,)", acceptableRemoteVersions = "*", certificateFingerprint = "52885f395e68f42e9b3b629ba56ecf606f7d4269")
public class ForgeMod
{
    public static FMLEventChannel channel;
    
    @Mod.EventHandler
    public void fingerprintError(final FMLFingerprintViolationEvent event) {
        if (!event.isDirectory) {
            return;
        }
        CustomSkinLoader.logger.warning("!!!Fingerprint ERROR!!!");
        CustomSkinLoader.logger.warning("Failed to check fingerprint in file '" + event.source.getAbsolutePath() + "'.");
        CustomSkinLoader.logger.warning("Excepted Fingerprint: " + event.expectedFingerprint);
        if (event.fingerprints.isEmpty()) {
            CustomSkinLoader.logger.warning("No Fingerprint Founded.");
        }
        else {
            CustomSkinLoader.logger.warning("Founded Fingerprint: ");
            for (final String s : event.fingerprints) {
                CustomSkinLoader.logger.warning(s);
            }
        }
        throw new RuntimeException("Fingerprint ERROR, please **DO NOT MODIFY** any mod.");
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent e) {
        ClientCommandHandler.instance.registerCommand((ICommand)new CommandMWSkin());
        final CustomSkinAPIPlus api = new CustomSkinAPIPlus();
        (ForgeMod.channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("CustomSkinLoader")).register((Object)api);
        MinecraftForge.EVENT_BUS.register((Object)api);
        OtherUtils.init();
        MinecraftForge.EVENT_BUS.register((Object)this);
    }
}
