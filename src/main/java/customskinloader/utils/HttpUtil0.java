package customskinloader.utils;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.apache.commons.lang3.StringUtils;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;

public class HttpUtil0
{
    public static boolean isLocal(final String url) {
        return url != null && !url.startsWith("http");
    }
    
    public static void ignoreHttpsCertificate() {
        final HostnameVerifier doNotVerify = new HostnameVerifier() {
            @Override
            public boolean verify(final String hostname, final SSLSession session) {
                return true;
            }
        };
        final TrustManager[] trustAllCerts = { new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
                
                @Override
                public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
                }
                
                @Override
                public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
                }
            } };
        try {
            final SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(new SSLSocketFactoryFacade());
            HttpsURLConnection.setDefaultHostnameVerifier(doNotVerify);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String parseAddress(final String address) {
        if (StringUtils.isEmpty((CharSequence)address)) {
            return null;
        }
        final String[] addresses = address.split(":");
        InetAddress add;
        try {
            add = InetAddress.getByName(addresses[0]);
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
        return add.getHostAddress() + ((addresses.length == 2) ? addresses[1] : "25565");
    }
    
    public static boolean isLanServer(final String standardAddress) {
        if (StringUtils.isEmpty((CharSequence)standardAddress)) {
            return true;
        }
        final String[] addresses = standardAddress.split(":");
        final int numIp = getNumIp(addresses[0]);
        return numIp == 0 || numIp == getNumIp("127.0.0.1") || (numIp >= getNumIp("192.168.0.0") && numIp <= getNumIp("192.168.255.255")) || (numIp >= getNumIp("10.0.0.0") && numIp <= getNumIp("10.255.255.255")) || (numIp >= getNumIp("172.16.0.0") && numIp <= getNumIp("172.31.255.255"));
    }
    
    public static int getNumIp(final String ip) {
        int num = 0;
        final String[] ips = ip.split("\\.");
        if (ips.length != 4) {
            return 0;
        }
        for (int i = 0; i < 4; ++i) {
            num += Integer.parseInt(ips[i]) * (0x100 ^ 3 - i);
        }
        return num;
    }
    
    public static class SSLSocketFactoryFacade extends SSLSocketFactory
    {
        SSLSocketFactory sslsf;
        
        public SSLSocketFactoryFacade() {
            this.sslsf = (SSLSocketFactory)SSLSocketFactory.getDefault();
        }
        
        @Override
        public String[] getDefaultCipherSuites() {
            return this.sslsf.getDefaultCipherSuites();
        }
        
        @Override
        public String[] getSupportedCipherSuites() {
            return this.sslsf.getSupportedCipherSuites();
        }
        
        @Override
        public Socket createSocket(final Socket socket, final String s, final int i, final boolean b) throws IOException {
            return this.sslsf.createSocket(socket, s, i, b);
        }
        
        @Override
        public Socket createSocket(final String s, final int i) throws IOException, UnknownHostException {
            return this.sslsf.createSocket(s, i);
        }
        
        @Override
        public Socket createSocket(final String s, final int i, final InetAddress inetAddress, final int i1) throws IOException, UnknownHostException {
            return this.sslsf.createSocket(s, i, inetAddress, i1);
        }
        
        @Override
        public Socket createSocket(final InetAddress inetAddress, final int i) throws IOException {
            return this.createSocket(inetAddress, i);
        }
        
        @Override
        public Socket createSocket(final InetAddress inetAddress, final int i, final InetAddress inetAddress1, final int i1) throws IOException {
            return this.createSocket(inetAddress, i, inetAddress1, i1);
        }
    }
}
