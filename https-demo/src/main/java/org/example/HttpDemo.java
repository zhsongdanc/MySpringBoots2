package org.example;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/10/7 08:52
 */
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpDemo {

    private static final Logger logger = LoggerFactory.getLogger(HttpDemo.class.getName());

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public static void httpGet(String path) {
        StringBuffer tempStr = new StringBuffer();
        String responseContent = "";
        HttpURLConnection conn = null;
        try {
            // Create a trust manager that does not validate certificate chains
            trustAllHosts();
            URL url = new URL(path);
            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
            if (url.getProtocol().toLowerCase().equals("https")) {
                https.setHostnameVerifier(DO_NOT_VERIFY);
                conn = https;
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
            conn.connect();
            logger.info("地址:{}, success, result:{}", path, conn.getResponseCode() + " " + conn.getResponseMessage());
            // HttpURLConnection conn = (HttpURLConnection)
            // url.openConnection();

            // conn.setConnectTimeout(5000);
            // conn.setReadTimeout(5000);
            // conn.setDoOutput(true);
            //
            // InputStream in = conn.getInputStream();
            // conn.setReadTimeout(10*1000);
            // BufferedReader rd = new BufferedReader(new InputStreamReader(in,
            // "UTF-8"));
            // String tempLine;
            // while ((tempLine = rd.readLine()) != null) {
            // tempStr.append(tempLine);
            // }
            // responseContent = tempStr.toString();
            // System.out.println(responseContent);
            // rd.close();
            // in.close();
        } catch (Exception e) {
            logger.error("地址:{}, is error", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {

            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {

            }
        } };

        // Install the all-trusting trust manager
        // 忽略HTTPS请求的SSL证书，必须在openConnection之前调用
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            logger.error("trustAllHosts is error", e);
        }
    }

    public static void main(String[] args) {
        httpGet("https://xxx.com");
    }
}
