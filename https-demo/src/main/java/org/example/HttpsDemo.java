package org.example;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/10/7 08:49
 */
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpsDemo {

    private static final Logger logger = LoggerFactory.getLogger(HttpsDemo.class.getName());

    public static void sendHttps(String path, String outputStr) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        HttpsURLConnection httpUrlConn = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(path);
            httpUrlConn = (HttpsURLConnection) url.openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.connect();

            // 当有数据需要提交时
            if (null != outputStr) {
                outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 将返回的输入流转换成字符串
            inputStream = httpUrlConn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            logger.info("地址:{}, success, result:{}", path, buffer.toString());
        } catch (Exception e) {
            logger.error("地址:{}, error, exception:{}", path, e);
        } finally {
            if (bufferedReader != null) {
                IOUtils.closeQuietly(bufferedReader);
            }
            if (inputStreamReader != null) {
                IOUtils.closeQuietly(inputStreamReader);
            }
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
            if (httpUrlConn != null) {
                httpUrlConn.disconnect();
            }
        }
    }

    public static void sendHttp(String path) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(path);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setUseCaches(false);
            inputStream = urlConnection.getInputStream();
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int n = 0;
            while (-1 != (n = inputStream.read(buffer))) {
                outputStream.write(buffer, 0, n);
            }
            logger.info("地址:{}, success, result:{}", path, outputStream.toString());
        } catch (Exception e) {
            logger.error("地址:{}, error, exception:{}", path, e);
        } finally {
            if (outputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
            if (outputStream != null) {
                IOUtils.closeQuietly(outputStream);
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        sendHttps("https://xxx.com", null);
    }
}
