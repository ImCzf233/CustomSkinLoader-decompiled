package customskinloader.utils;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.UUID;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.IOException;
import java.util.Iterator;
import java.net.HttpURLConnection;
import java.io.DataOutputStream;
import java.util.Map;

public class HttpUtils
{
    private static final String BOUNDARY_PREFIX = "--";
    private static final String LINE_END = "\r\n";
    
    public static HttpResponse postFormData(final String urlStr, final Map<String, byte[]> filePathMap, final Map<String, Object> keyValues, final Map<String, Object> headers) throws IOException {
        final HttpURLConnection conn = getHttpURLConnection(urlStr, headers);
        final String boundary = "MyBoundary" + System.currentTimeMillis();
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        try (final DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            if (keyValues != null && !keyValues.isEmpty()) {
                for (final Map.Entry<String, Object> entry : keyValues.entrySet()) {
                    writeSimpleFormField(boundary, out, entry);
                }
            }
            if (filePathMap != null && !filePathMap.isEmpty()) {
                for (final Map.Entry<String, byte[]> filePath : filePathMap.entrySet()) {
                    writeFile(filePath.getKey(), filePath.getValue(), boundary, out);
                }
            }
            final String endStr = "--" + boundary + "--" + "\r\n";
            out.write(endStr.getBytes());
        }
        catch (Exception e) {
            final HttpResponse response = new HttpResponse(500, e.getMessage());
            return response;
        }
        return getHttpResponse(conn);
    }
    
    private static HttpURLConnection getHttpURLConnection(final String urlStr, final Map<String, Object> headers) throws IOException {
        final URL url = new URL(urlStr);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setConnectTimeout(50000);
        conn.setReadTimeout(50000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("connection", "keep-alive");
        if (headers != null && !headers.isEmpty()) {
            for (final Map.Entry<String, Object> header : headers.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue().toString());
            }
        }
        return conn;
    }
    
    private static HttpResponse getHttpResponse(final HttpURLConnection conn) {
        HttpResponse response;
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            final int responseCode = conn.getResponseCode();
            final StringBuilder responseContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            response = new HttpResponse(responseCode, responseContent.toString());
        }
        catch (Exception e) {
            response = new HttpResponse(500, e.getMessage());
        }
        return response;
    }
    
    private static void writeFile(final String paramName, final byte[] bytes, final String boundary, final DataOutputStream out) {
        try {
            final String boundaryStr = "--" + boundary + "\r\n";
            out.write(boundaryStr.getBytes());
            final String fileName = UUID.randomUUID().toString() + ".jpg";
            final String contentDispositionStr = String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", paramName, fileName) + "\r\n";
            out.write(contentDispositionStr.getBytes());
            final String contentType = "Content-Type: application/octet-stream\r\n\r\n";
            out.write(contentType.getBytes());
            out.write(bytes);
            out.write("\r\n".getBytes());
        }
        catch (Exception ex) {}
    }
    
    private static void writeSimpleFormField(final String boundary, final DataOutputStream out, final Map.Entry<String, Object> entry) throws IOException {
        final String boundaryStr = "--" + boundary + "\r\n";
        out.write(boundaryStr.getBytes());
        final String contentDispositionStr = String.format("Content-Disposition: form-data; name=\"%s\"", entry.getKey()) + "\r\n" + "\r\n";
        out.write(contentDispositionStr.getBytes());
        final String valueStr = entry.getValue().toString() + "\r\n";
        out.write(valueStr.getBytes());
    }
    
    public static HttpResponse postText(final String urlStr, final String filePath) throws IOException {
        final URL url = new URL(urlStr);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
             final BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                writer.write(line);
            }
        }
        catch (Exception e) {
            final HttpResponse response = new HttpResponse(500, e.getMessage());
            return response;
        }
        return getHttpResponse(conn);
    }
    
    public static class HttpResponse
    {
        private int code;
        private String content;
        
        public HttpResponse(final int status, final String content) {
            this.code = status;
            this.content = content;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public void setCode(final int code) {
            this.code = code;
        }
        
        public String getContent() {
            return this.content;
        }
        
        public void setContent(final String content) {
            this.content = content;
        }
        
        @Override
        public String toString() {
            return "[ code = " + this.code + " , content = " + this.content + " ]";
        }
    }
}
