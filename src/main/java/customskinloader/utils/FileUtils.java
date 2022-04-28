package customskinloader.utils;

import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.io.InputStream;
import java.io.IOException;
import java.net.UnknownServiceException;
import java.io.File;
import java.util.Iterator;
import java.net.URL;
import net.minecraft.launchwrapper.LaunchClassLoader;
import java.util.HashSet;
import java.util.Set;

public class FileUtils
{
    public static Set<String> checkFile() {
        final Set<String> fileHash = new HashSet<String>();
        final LaunchClassLoader lwClassloader = (LaunchClassLoader)FileUtils.class.getClassLoader();
        for (final URL source : lwClassloader.getSources()) {
            final String hash = getFileHash(source);
            if (hash != null) {
                fileHash.add(hash);
            }
        }
        return fileHash;
    }
    
    private static String getFileHash(final URL url) {
        final String fileName = new File(url.getFile()).getName();
        try (final InputStream in = url.openStream()) {
            return calcHash(in) + "\u0000" + fileName;
        }
        catch (UnknownServiceException e2) {
            return null;
        }
        catch (IOException e) {
            System.out.println(e.toString());
            return "0000000000000000000000000000000000000000\u0000" + (fileName.isEmpty() ? "unknown" : fileName);
        }
    }
    
    private static String calcHash(final InputStream in) throws IOException {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA1");
            final byte[] buffer = new byte[4096];
            for (int read = in.read(buffer, 0, 4096); read > -1; read = in.read(buffer, 0, 4096)) {
                md.update(buffer, 0, read);
            }
            final byte[] digest = md.digest();
            return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest)).toUpperCase();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
