package customskinloader.utils;

import java.util.LinkedList;
import java.io.File;
import java.net.URLClassLoader;
import java.net.URL;

public class JavaUtil
{
    public static URL[] getClasspath() {
        if (JavaUtil.class.getClassLoader() instanceof URLClassLoader) {
            final URLClassLoader ucl = (URLClassLoader)JavaUtil.class.getClassLoader();
            return ucl.getURLs();
        }
        final String classpath = System.getProperty("java.class.path");
        final String[] elements = classpath.split(File.pathSeparator);
        if (elements.length == 0) {
            return new URL[0];
        }
        final LinkedList<URL> urls = new LinkedList<URL>();
        for (final String ele : elements) {
            try {
                urls.add(new File(ele).toURI().toURL());
            }
            catch (Exception ex) {}
        }
        return urls.toArray(new URL[0]);
    }
}
