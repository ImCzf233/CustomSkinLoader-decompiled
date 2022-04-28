package customskinloader.forge.loader;

import customskinloader.forge.transformer.TileEntitySkullTransformer;
import customskinloader.forge.transformer.SpectatorMenuTransformer;
import customskinloader.forge.transformer.PlayerTabTransformer;
import customskinloader.forge.transformer.SkinManagerTransformer;
import customskinloader.forge.transformer.FakeInterfacesTransformer;
import customskinloader.forge.TransformerManager;
import java.io.File;
import net.minecraft.launchwrapper.IClassTransformer;

public class LaunchWrapper implements IClassTransformer
{
    private static final File MWSKIN_FILE;
    private static final TransformerManager.IClassTransformer[] CLASS_TRANSFORMERS;
    private static final TransformerManager.IMethodTransformer[] TRANFORMERS;
    private TransformerManager transformerManager;
    
    public LaunchWrapper() {
        this.transformerManager = new TransformerManager(LaunchWrapper.CLASS_TRANSFORMERS, LaunchWrapper.TRANFORMERS);
    }
    
    public byte[] transform(final String obfClassName, final String className, final byte[] bytes) {
        return bytes;
    }
    
    static {
        MWSKIN_FILE = new File("./mwskin");
        CLASS_TRANSFORMERS = new TransformerManager.IClassTransformer[] { new FakeInterfacesTransformer.MinecraftTransformer(), new FakeInterfacesTransformer.AbstractTextureTransfomer(), new FakeInterfacesTransformer.TextureTransformer(), new FakeInterfacesTransformer.TextureManagerTransformer() };
        TRANFORMERS = new TransformerManager.IMethodTransformer[] { new SkinManagerTransformer.InitTransformer(), new SkinManagerTransformer.LoadSkinTransformer(), new SkinManagerTransformer.LoadProfileTexturesTransformer(), new SkinManagerTransformer.LoadSkinFromCacheTransformer(), new PlayerTabTransformer.ScoreObjectiveTransformer(), new SpectatorMenuTransformer.PlayerMenuObjectTransformer(), new TileEntitySkullTransformer.UpdateGameProfileTransformer() };
    }
}
