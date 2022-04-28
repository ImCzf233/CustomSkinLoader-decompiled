package customskinloader.config;

public class SkinSiteProfile
{
    public String name;
    public String type;
    public String userAgent;
    public String apiRoot;
    public String sessionRoot;
    public String root;
    public Boolean checkPNG;
    public String skin;
    public String model;
    public String cape;
    public String elytra;
    
    public static SkinSiteProfile createUniSkinAPI(final String name, final String root) {
        final SkinSiteProfile ssp = new SkinSiteProfile();
        ssp.name = name;
        ssp.type = "UniSkinAPI";
        ssp.root = root;
        return ssp;
    }
}
