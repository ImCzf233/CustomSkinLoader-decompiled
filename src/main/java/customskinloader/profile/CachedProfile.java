package customskinloader.profile;

class CachedProfile
{
    public UserProfile profile;
    public long expiryTime;
    public boolean loading;
    
    CachedProfile() {
        this.expiryTime = 0L;
        this.loading = false;
    }
}
