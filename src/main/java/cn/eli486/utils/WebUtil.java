package cn.eli486.utils;

import org.apache.commons.collections4.MapUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author eli
 * HttpClient工具类
 */
public class WebUtil {
    private static CloseableHttpClient httpsClient;
    private static CloseableHttpClient httpClient;
    public static Header[] headers;

    static {
//        httpClient = getHttpClient();
//        httpsClient = getHttpsClient();
    }

    public static CloseableHttpClient getHttpClient () {
        try {
            httpClient = HttpClients.custom ()
                    .setConnectionManager (PoolManager.getHttpPoolInstance ())
                    .setConnectionManagerShared (true)
                    .setDefaultRequestConfig (requestConfig ())
                    .setRetryHandler (retryHandler ())
                    .build ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return httpClient;
    }

    public static CloseableHttpClient getHttpsClient () {
        try {
            //Secure Protocol implementation.
            SSLContext ctx = SSLContext.getInstance ("SSL");
            //Implementation of a trust manager for X509 certificates
            TrustManager x509TrustManager = new X509TrustManager () {
                @Override
                public void checkClientTrusted (X509Certificate[] xcs,
                                                String string) {
                }

                @Override
                public void checkServerTrusted (X509Certificate[] xcs,
                                                String string) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers () {
                    return null;
                }
            };
            ctx.init (null, new TrustManager[]{x509TrustManager}, null);
            ConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory (ctx, hostnameVerifier);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create ()
                    .register ("http", PlainConnectionSocketFactory.INSTANCE)
                    .register ("https", connectionSocketFactory).build ();
            // 设置连接池
            httpsClient = HttpClients.custom ()
                    .setConnectionManager (PoolsManager.getHttpsPoolInstance (socketFactoryRegistry))
                    .setConnectionManagerShared (true)
                    .setDefaultRequestConfig (requestConfig ())
                    .setRetryHandler (retryHandler ())
                    .build ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return httpsClient;
    }


    // 配置请求的超时设置
    private static RequestConfig requestConfig () {
        return RequestConfig.custom ()
                .setCookieSpec (CookieSpecs.STANDARD_STRICT)
                .setConnectionRequestTimeout (20000)
                .setConnectTimeout (20000)
                .setSocketTimeout (2000000)
                .build ();
    }

    //创建HostnameVerifier
    //用于解决javax.net.ssl.SSLException: hostname in certificate didn't match: <123.125.97.66> != <123.125.97.241>
    private static HostnameVerifier hostnameVerifier = new NoopHostnameVerifier () {
        @Override
        public boolean verify (String s, SSLSession sslSession) {
            return super.verify (s, sslSession);
        }
    };


    private static HttpRequestRetryHandler retryHandler () {
        //请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler;
        httpRequestRetryHandler = (exception, executionCount, context) -> {
            if (executionCount >= 3) {// 如果已经重试了3次，就放弃
                return false;
            }
            if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                return true;
            }
            if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                return false;
            }
            if (exception instanceof InterruptedIOException) {// 超时
                return false;
            }
            if (exception instanceof UnknownHostException) {// 目标服务器不可达
                return false;
            }
            if (exception instanceof SSLException) {// ssl握手异常
                return false;
            }

            HttpClientContext clientContext = HttpClientContext.adapt (context);
            HttpRequest request = clientContext.getRequest ();
            // 如果请求是幂等的，就再次尝试
            return !(request instanceof HttpEntityEnclosingRequest);
        };
        return httpRequestRetryHandler;
    }

    public static class PoolManager {
        static PoolingHttpClientConnectionManager clientConnectionManager = null;

        private PoolManager () {
            int maxTotal = 200;
            clientConnectionManager.setMaxTotal (maxTotal);
            int defaultMaxPerRoute = 100;
            clientConnectionManager.setDefaultMaxPerRoute (defaultMaxPerRoute);
        }

        private static class PoolManagerHolder {
            static PoolManager instance = new PoolManager ();
        }

        static PoolManager getInstance () {
            if (null == clientConnectionManager) {
                clientConnectionManager = new PoolingHttpClientConnectionManager ();
            }
            return PoolManagerHolder.instance;
        }

        static PoolingHttpClientConnectionManager getHttpPoolInstance () {
            PoolManager.getInstance ();
            return PoolManager.clientConnectionManager;
        }


    }


    public static class PoolsManager {
        static PoolingHttpClientConnectionManager clientConnectionManager = null;

        private PoolsManager () {
            int maxTotal = 200;
            clientConnectionManager.setMaxTotal (maxTotal);
            int defaultMaxPerRoute = 100;
            clientConnectionManager.setDefaultMaxPerRoute (defaultMaxPerRoute);
        }

        private static class PoolsManagerHolder {
            static PoolsManager instance = new PoolsManager ();
        }

        static PoolsManager getInstance (Registry<ConnectionSocketFactory> socketFactoryRegistry) {
            if (null == clientConnectionManager) {
                clientConnectionManager = new PoolingHttpClientConnectionManager (socketFactoryRegistry);
            }
            return PoolsManagerHolder.instance;
        }

        static PoolingHttpClientConnectionManager getHttpsPoolInstance (Registry<ConnectionSocketFactory> socketFactoryRegistry) {
            PoolsManager.getInstance (socketFactoryRegistry);
            return PoolsManager.clientConnectionManager;
        }
    }

    public static String httpGet (CloseableHttpClient client, String url) throws IOException {
        String result = "";
        HttpGet request = new HttpGet (url);
        HttpEntity entity = null;
        try (CloseableHttpResponse response = client.execute (request)) {
            if (HttpStatus.SC_INTERNAL_SERVER_ERROR == response.getStatusLine ().getStatusCode ()) {
                throw new Exception ("服务器发生内部错误");
            }
            if (HttpStatus.SC_OK != response.getStatusLine ().getStatusCode ()) {
                return "";
            }
            entity = response.getEntity ();
            if (entity != null) {
                result = EntityUtils.toString (entity, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            EntityUtils.consume (entity);
            request.releaseConnection ();
        }

        return result;
    }


    public static String httpGet (CloseableHttpClient client, String url,String headerName,String headerValue) throws IOException {
        String result = "";
        HttpGet request = new HttpGet (url);
        HttpEntity entity = null;
        request.setHeader (headerName,headerValue);
        try (CloseableHttpResponse response = client.execute (request)) {
            if (HttpStatus.SC_INTERNAL_SERVER_ERROR == response.getStatusLine ().getStatusCode ()) {
                throw new Exception ("服务器发生内部错误");
            }
            if (HttpStatus.SC_OK != response.getStatusLine ().getStatusCode ()) {
                return "";
            }
            entity = response.getEntity ();
            if (entity != null) {
                result = EntityUtils.toString (entity, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            EntityUtils.consume (entity);
            request.releaseConnection ();
        }

        return result;
    }

    public static void getFile (CloseableHttpClient client, String url,
                                String fileName) {
        HttpGet get = new HttpGet (url);
        try {
            CloseableHttpResponse response = client.execute (get);
            downFile (response,fileName);
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            get.releaseConnection ();
        }
    }

    public static String httpPost (CloseableHttpClient client, Map<String, String> params, String httpUrl) {
        String result;
        HttpPost httpPost = null;
        try {
            if (MapUtils.isEmpty (params)) {
                throw new Exception ("请求参数不能为空");
            }
            httpPost = new HttpPost (httpUrl);
            ArrayList<NameValuePair> nvps = parsePostParams (params);
            httpPost.setEntity (new UrlEncodedFormEntity (nvps, StandardCharsets.UTF_8));
            CloseableHttpResponse response = client.execute (httpPost);
            headers = response.getHeaders ("set-Cookie");
            result = EntityUtils.toString (response.getEntity (), "utf-8");
            EntityUtils.consume (response.getEntity ());
        } catch (Exception e) {
            throw new RuntimeException (e);
        } finally {
            assert httpPost != null;
            httpPost.releaseConnection ();
        }
        return result;
    }

    public static String httpPostJson (CloseableHttpClient client, String jsonParam, String httpUrl,String headerName,String headerValue) {
        String result;
        StringEntity entity;
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost (httpUrl);
            entity = new StringEntity (jsonParam, "UTF-8");
            entity.setContentType ("text/json");
            httpPost.setHeader ("accept", "application/json, text/plain, */*");
            httpPost.setHeader ("Content-Type", "application/json");
            httpPost.setHeader (headerName,headerValue);
            httpPost.setEntity (entity);
            CloseableHttpResponse response = client.execute (httpPost);
            result = EntityUtils.toString (response.getEntity (), "utf-8");
            EntityUtils.consume (response.getEntity ());
        } catch (Exception e) {
            throw new RuntimeException (e);
        } finally {
            assert httpPost != null;
            httpPost.releaseConnection ();
        }
        return result;
    }

    public static String httpPostJson (CloseableHttpClient client, String jsonParam, String httpUrl) {
        String result;
        StringEntity entity;
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost (httpUrl);
            entity = new StringEntity (jsonParam, "UTF-8");
            entity.setContentType ("text/json");
            httpPost.setHeader ("accept", "application/json, text/javascript, */*");
            httpPost.setHeader ("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpPost.setEntity (entity);
            CloseableHttpResponse response = client.execute (httpPost);
            result = EntityUtils.toString (response.getEntity (), "utf-8");
            EntityUtils.consume (response.getEntity ());
        } catch (Exception e) {
            throw new RuntimeException (e);
        } finally {
            assert httpPost != null;
            httpPost.releaseConnection ();
        }
        return result;
    }

    public static void postFile (CloseableHttpClient client, String url, Map<String, String> params,
                                 String fileName) {
        HttpPost post = new HttpPost (url);
        try {
            ArrayList<NameValuePair> nvps = parsePostParams (params);
            post.setEntity (new UrlEncodedFormEntity (nvps, StandardCharsets.UTF_8));
            CloseableHttpResponse response = client.execute (post);
            downFile (response,fileName);
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            post.releaseConnection ();
        }
    }



    private static ArrayList<NameValuePair> parsePostParams(Map<String, String> params){
        ArrayList<NameValuePair> nvps = new ArrayList<> ();
        for (Map.Entry<String, String> entry : params.entrySet ()) {
            nvps.add (new BasicNameValuePair (entry.getKey (), entry.getValue ()));
        }
        return nvps;
    }
    private static void downFile(CloseableHttpResponse response,String fileName)throws Exception{
        if (HttpStatus.SC_OK != response.getStatusLine ().getStatusCode ()) {
            return;
        }
        HttpEntity entity = response.getEntity ();
        if (entity == null) {
            return;
        }
        File storeFile = new File (fileName);
        FileOutputStream output = new FileOutputStream (storeFile);
        entity.writeTo (output);
        output.close ();
        EntityUtils.consume (entity);
    }
}
