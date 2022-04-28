package customskinloader.utils;

public class TimeUtil
{
    public static long getCurrentUnixTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }
    
    public static long getUnixTimestamp(final long offset) {
        return getCurrentUnixTimestamp() + offset;
    }
    
    public static long getUnixTimestampRandomDelay(final long offset) {
        return getCurrentUnixTimestamp() + offset + random(0, 5);
    }
    
    private static int random(final int min, final int max) {
        return (int)(Math.random() * (max - min + 1)) + min;
    }
}
