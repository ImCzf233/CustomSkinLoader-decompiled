package customskinloader.profile;

import org.apache.commons.lang3.StringUtils;

public class UserProfile
{
    public String skinUrl;
    public String model;
    public String capeUrl;
    public String elytraUrl;
    
    public UserProfile() {
        this.skinUrl = null;
        this.model = null;
        this.capeUrl = null;
        this.elytraUrl = null;
    }
    
    public void put(final ModelManager0.Model model, final String url) {
        if (model == null || StringUtils.isEmpty((CharSequence)url)) {
            return;
        }
        switch (model) {
            case SKIN_DEFAULT: {
                this.skinUrl = url;
                this.model = "default";
            }
            case SKIN_SLIM: {
                this.skinUrl = url;
                this.model = "slim";
            }
            case CAPE: {
                this.capeUrl = url;
            }
            case ELYTRA: {
                this.capeUrl = url;
                break;
            }
        }
    }
    
    @Override
    public String toString() {
        return this.toString(0L);
    }
    
    public String toString(final long expiry) {
        return "(SkinUrl: " + this.skinUrl + " , Model: " + this.model + " , CapeUrl: " + this.capeUrl + (StringUtils.isBlank((CharSequence)this.elytraUrl) ? " " : (" , ElytraUrl: " + this.elytraUrl)) + ((expiry == 0L) ? "" : (" , Expiry: " + expiry)) + ")";
    }
    
    public boolean isEmpty() {
        return StringUtils.isEmpty((CharSequence)this.skinUrl) && StringUtils.isEmpty((CharSequence)this.capeUrl) && StringUtils.isEmpty((CharSequence)this.elytraUrl);
    }
    
    public boolean isFull() {
        return StringUtils.isNoneBlank(new CharSequence[] { this.skinUrl, this.capeUrl });
    }
    
    public boolean hasSkinUrl() {
        return StringUtils.isNotEmpty((CharSequence)this.skinUrl);
    }
    
    public void mix(final UserProfile profile) {
        if (profile == null) {
            return;
        }
        if (StringUtils.isEmpty((CharSequence)this.skinUrl)) {
            this.skinUrl = profile.skinUrl;
            this.model = profile.model;
        }
        if (StringUtils.isEmpty((CharSequence)this.capeUrl)) {
            this.capeUrl = profile.capeUrl;
        }
        if (StringUtils.isEmpty((CharSequence)this.elytraUrl)) {
            this.elytraUrl = profile.elytraUrl;
        }
    }
}
