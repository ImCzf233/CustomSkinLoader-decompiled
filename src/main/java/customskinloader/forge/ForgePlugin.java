package customskinloader.forge;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("CustomSkinLoader")
public class ForgePlugin implements IFMLLoadingPlugin
{
    public String[] getASMTransformerClass() {
        return new String[] { "customskinloader.forge.loader.LaunchWrapper" };
    }
    
    public String getModContainerClass() {
        return null;
    }
    
    public String getSetupClass() {
        return null;
    }
    
    public void injectData(final Map<String, Object> data) {
    }
    
    public String getAccessTransformerClass() {
        return null;
    }
}
