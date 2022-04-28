package customskinloader.utils;

import org.apache.commons.lang3.StringUtils;

public class Version implements Comparable
{
    private final String version;
    private final int[] sub;
    
    public Version(String version) {
        if (version == null) {
            version = "";
        }
        this.version = version;
        final String[] split = version.split("\\.");
        this.sub = new int[split.length];
        for (int i = 0; i < split.length; ++i) {
            if (StringUtils.isNumeric((CharSequence)split[i])) {
                this.sub[i] = Integer.parseInt(split[i]);
            }
        }
    }
    
    public static Version of(final String version) {
        return new Version(version);
    }
    
    public static int compare(final String version, final String anotherVersion) {
        return of(version).compareTo(anotherVersion);
    }
    
    @Override
    public String toString() {
        return this.version;
    }
    
    @Override
    public int compareTo(Object o) {
        if (o instanceof String) {
            o = of((String)o);
        }
        if (!(o instanceof Version)) {
            throw new IllegalArgumentException(String.format("'%s' is not a Version.", o));
        }
        Version v;
        int i;
        for (v = (Version)o, i = 0; i < this.sub.length && i < v.sub.length && this.sub[i] == v.sub[i]; ++i) {}
        if (i < this.sub.length && i < v.sub.length) {
            return (this.sub[i] < v.sub[i]) ? -1 : ((this.sub[i] == v.sub[i]) ? 0 : 1);
        }
        return Integer.signum(this.sub.length - v.sub.length);
    }
}
