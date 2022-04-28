package customskinloader.utils;

import java.util.regex.Matcher;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.IOUtils;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import customskinloader.CustomSkinLoader;
import java.util.regex.Pattern;
import java.io.File;

public class HttpRequestUtil
{
    public static final File CACHE_DIR;
    private static final Pattern MAX_AGE_PATTERN;
    
    public static HttpResponce makeHttpRequest(final HttpRequest request) {
        return makeHttpRequest(request, 0);
    }
    
    public static HttpResponce makeHttpRequest(final HttpRequest request, final int redirectTime) {
        try {
            if (request.url.contains("{ABORT}")) {
                CustomSkinLoader.logger.info("ABORT tag found in url, request has been aborted.");
                return new HttpResponce();
            }
            CustomSkinLoader.logger.debug("Try to request '" + request.url + ((request.userAgent == null) ? "'." : ("' with user agent '" + request.userAgent + "'.")));
            if (StringUtils.isNotEmpty((CharSequence)request.payload) || CustomSkinLoader.config.forceDisableCache) {
                request.cacheTime = -1;
            }
            File cacheInfoFile = null;
            CacheInfo cacheInfo = new CacheInfo();
            if (request.cacheFile == null && request.cacheTime >= 0) {
                final String hash = DigestUtils.sha1Hex(request.url);
                request.cacheFile = new File(HttpRequestUtil.CACHE_DIR, hash);
                cacheInfoFile = new File(HttpRequestUtil.CACHE_DIR, hash + ".json");
            }
            if (request.cacheTime == 0 && request.cacheFile.isFile()) {
                return loadFromCache(request, new HttpResponce());
            }
            if (cacheInfoFile != null && cacheInfoFile.isFile()) {
                final String json = FileUtils.readFileToString(cacheInfoFile, "UTF-8");
                if (StringUtils.isNotEmpty((CharSequence)json)) {
                    cacheInfo = (CacheInfo)CustomSkinLoader.GSON.fromJson(json, (Class)CacheInfo.class);
                }
                if (cacheInfo == null) {
                    cacheInfo = new CacheInfo();
                }
                if (cacheInfo.expire >= TimeUtil.getCurrentUnixTimestamp()) {
                    return loadFromCache(request, new HttpResponce(), cacheInfo.expire);
                }
            }
            final URL rawUrl = new URL(request.url);
            final URI uri = new URI(rawUrl.getProtocol(), rawUrl.getUserInfo(), rawUrl.getHost(), rawUrl.getPort(), rawUrl.getPath(), rawUrl.getQuery(), rawUrl.getRef());
            final String url = uri.toASCIIString();
            if (!url.equalsIgnoreCase(request.url)) {
                CustomSkinLoader.logger.debug("Encoded URL: " + url);
            }
            final HttpURLConnection c = (HttpURLConnection)new URL(url).openConnection();
            c.setReadTimeout(10000);
            c.setConnectTimeout(10000);
            c.setDoInput(true);
            c.setUseCaches(false);
            c.setInstanceFollowRedirects(true);
            if (cacheInfo.lastModified >= 0L) {
                c.setIfModifiedSince(cacheInfo.lastModified);
            }
            if (cacheInfo.etag != null) {
                c.setRequestProperty("If-None-Match", cacheInfo.etag);
            }
            c.setRequestProperty("Accept-Encoding", "gzip");
            if (request.userAgent != null) {
                c.setRequestProperty("User-Agent", request.userAgent);
            }
            if (StringUtils.isNotEmpty((CharSequence)request.payload)) {
                CustomSkinLoader.logger.info("Payload: " + request.payload);
                c.setRequestProperty("Content-Type", "application/json");
                c.setDoOutput(true);
                final OutputStream os = c.getOutputStream();
                IOUtils.write(request.payload, os, "UTF-8");
                IOUtils.closeQuietly(os);
            }
            c.connect();
            final HttpResponce responce = new HttpResponce();
            responce.responceCode = c.getResponseCode();
            final int res = c.getResponseCode();
            if (res / 100 == 4 || res / 100 == 5) {
                CustomSkinLoader.logger.debug("Failed to request (Response Code: " + res + ")");
                return responce;
            }
            if (res == 301 || res == 302) {
                if (redirectTime >= 4) {
                    CustomSkinLoader.logger.debug("Failed to request (Too many redirection)");
                    return responce;
                }
                request.url = c.getHeaderField("Location");
                if (request.url == null) {
                    CustomSkinLoader.logger.debug("Failed to request (Redirecting location not found)");
                    return responce;
                }
                CustomSkinLoader.logger.debug("Redirect to: " + request.url);
                return makeHttpRequest(request, redirectTime + 1);
            }
            else {
                responce.success = true;
                CustomSkinLoader.logger.debug("Successfully request (Response Code: " + res + " , Content Length: " + c.getContentLength() + ")");
                if (responce.responceCode == 304) {
                    return loadFromCache(request, responce);
                }
                if (responce.responceCode == 204) {
                    request.cacheTime = 3600;
                }
                final InputStream is = "gzip".equals(c.getContentEncoding()) ? new GZIPInputStream(c.getInputStream()) : c.getInputStream();
                final byte[] bytes = IOUtils.toByteArray(is);
                if (request.checkPNG && (bytes.length <= 4 || bytes[1] != 80 || bytes[2] != 78 || bytes[3] != 71)) {
                    CustomSkinLoader.logger.debug("Failed to request (Not Standard PNG)");
                    responce.success = false;
                    return responce;
                }
                if (request.cacheFile != null) {
                    FileUtils.writeByteArrayToFile(request.cacheFile, bytes);
                    if (cacheInfoFile != null) {
                        cacheInfo.url = request.url;
                        cacheInfo.etag = c.getHeaderField("ETag");
                        cacheInfo.lastModified = c.getLastModified();
                        cacheInfo.expire = getExpire(c, request.cacheTime);
                        FileUtils.write(cacheInfoFile, (CharSequence)CustomSkinLoader.GSON.toJson((Object)cacheInfo), "UTF-8");
                    }
                    CustomSkinLoader.logger.debug("Saved to cache (Length: " + request.cacheFile.length() + " , Path: '" + request.cacheFile.getAbsolutePath() + "' , Expire: " + cacheInfo.expire + ")");
                }
                if (!request.loadContent) {
                    return responce;
                }
                responce.content = new String(bytes, StandardCharsets.UTF_8);
                CustomSkinLoader.logger.debug("Content: " + responce.content);
                return responce;
            }
        }
        catch (Exception e) {
            CustomSkinLoader.logger.debug("Failed to request " + request.url + " (Exception: " + e.toString() + ")");
            return loadFromCache(request, new HttpResponce());
        }
    }
    
    public static File getCacheFile(final String hash) {
        return new File(HttpRequestUtil.CACHE_DIR, hash);
    }
    
    private static HttpResponce loadFromCache(final HttpRequest request, final HttpResponce responce) {
        return loadFromCache(request, responce, 0L);
    }
    
    private static HttpResponce loadFromCache(final HttpRequest request, final HttpResponce responce, final long expireTime) {
        if (request.cacheFile == null || !request.cacheFile.isFile()) {
            return responce;
        }
        CustomSkinLoader.logger.debug("Cache file found (Length: " + request.cacheFile.length() + " , Path: '" + request.cacheFile.getAbsolutePath() + "' , Expire: " + expireTime + ")");
        responce.fromCache = true;
        responce.success = true;
        if (!request.loadContent) {
            return responce;
        }
        CustomSkinLoader.logger.info("Try to load from cache '" + request.cacheFile.getAbsolutePath() + "'.");
        try {
            responce.content = FileUtils.readFileToString(request.cacheFile, "UTF-8");
            CustomSkinLoader.logger.debug("Successfully load from cache");
            CustomSkinLoader.logger.debug("Content: " + responce.content);
        }
        catch (IOException e) {
            CustomSkinLoader.logger.debug("Failed to load from cache (Exception: " + e.toString() + ")");
            responce.success = false;
        }
        return responce;
    }
    
    private static long getExpire(final HttpURLConnection connection, final int cacheTime) {
        final String cacheControl = connection.getHeaderField("Cache-Control");
        if (StringUtils.isNotEmpty((CharSequence)cacheControl)) {
            final Matcher m = HttpRequestUtil.MAX_AGE_PATTERN.matcher(cacheControl);
            if (m.matches()) {
                return TimeUtil.getUnixTimestamp(Long.parseLong(m.group(m.groupCount())));
            }
        }
        final long expires = connection.getExpiration();
        if (expires > 0L) {
            return expires / 1000L;
        }
        return TimeUtil.getUnixTimestampRandomDelay((cacheTime == 0) ? 2592000L : ((long)cacheTime));
    }
    
    static {
        CACHE_DIR = new File(CustomSkinLoader.DATA_DIR, "caches");
        MAX_AGE_PATTERN = Pattern.compile(".*?max-age=(\\d+).*?");
    }
    
    public static class HttpRequest
    {
        public String url;
        public String userAgent;
        public String payload;
        public boolean loadContent;
        public boolean checkPNG;
        public int cacheTime;
        public File cacheFile;
        
        public HttpRequest(final String url) {
            this.userAgent = null;
            this.payload = null;
            this.loadContent = true;
            this.checkPNG = false;
            this.cacheTime = 600;
            this.cacheFile = null;
            this.url = url;
        }
        
        public HttpRequest setUserAgent(final String userAgent) {
            this.userAgent = userAgent;
            return this;
        }
        
        public HttpRequest setPayload(final String payload) {
            this.payload = payload;
            return this;
        }
        
        public HttpRequest setLoadContent(final boolean loadContent) {
            this.loadContent = loadContent;
            return this;
        }
        
        public HttpRequest setCheckPNG(final boolean checkPNG) {
            this.checkPNG = checkPNG;
            return this;
        }
        
        public HttpRequest setCacheTime(final int cacheTime) {
            this.cacheTime = cacheTime;
            return this;
        }
        
        public HttpRequest setCacheFile(final File cacheFile) {
            this.cacheFile = cacheFile;
            return this;
        }
    }
    
    public static class HttpResponce
    {
        public String content;
        public int responceCode;
        public boolean success;
        public boolean fromCache;
        
        public HttpResponce() {
            this.content = null;
            this.responceCode = -1;
            this.success = false;
            this.fromCache = false;
        }
    }
    
    public static class CacheInfo
    {
        public String url;
        public String etag;
        public long lastModified;
        public long expire;
        
        public CacheInfo() {
            this.etag = null;
            this.lastModified = -1L;
            this.expire = -1L;
        }
    }
}
