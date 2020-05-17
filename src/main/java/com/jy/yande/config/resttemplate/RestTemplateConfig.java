package com.jy.yande.config.resttemplate;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * @Author: jy
 * @ProjectName: qingyuwx
 * @Package: com.jy.qingyuwx.config.resttemplate
 * @ClassName: RestTemplateConfig
 * @CreateDate: 2020/4/20 10:46
 * @Description:
 */
@Component
public class RestTemplateConfig {

    private static final int HTTP_CLIENT_CONNECTION_TIMEOUT_MS =60000;
    private static final int HTTP_CLIENT_READ_TIMEOUT_MS = 60000;

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        RestTemplate restTemplate = new RestTemplate(factory);
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        HttpMessageConverter<?> targetConverter = null;
        for(HttpMessageConverter<?> converter:converterList){
            if(StringHttpMessageConverter.class==converter.getClass()){
                targetConverter = converter;
                break;
            }
        }
        if(null!=targetConverter){
            converterList.remove(targetConverter);
        }
        converterList.add(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        converterList.add(new FastJsonHttpMessageConverter());
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        JSkipSslVerificationHttpRequestFactory factory = new JSkipSslVerificationHttpRequestFactory();
        factory.setConnectTimeout(HTTP_CLIENT_CONNECTION_TIMEOUT_MS);
        factory.setReadTimeout(HTTP_CLIENT_READ_TIMEOUT_MS);
        factory.setProxy(new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1080)));
        return factory;
    }

    class JSkipSslVerificationHttpRequestFactory extends SimpleClientHttpRequestFactory{
        JSkipSslVerificationHttpRequestFactory() {
        }

        protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
            if (connection instanceof HttpsURLConnection) {
                this.prepareHttpsConnection((HttpsURLConnection)connection);
            }
            super.prepareConnection(connection, httpMethod);
        }

        private void prepareHttpsConnection(HttpsURLConnection connection) {
            connection.setHostnameVerifier(new JSkipSslVerificationHttpRequestFactory.SkipHostnameVerifier());

            try {
                connection.setSSLSocketFactory(this.createSslSocketFactory());
            } catch (Exception var3) {
            }

        }

        private SSLSocketFactory createSslSocketFactory() throws Exception {
            SSLContext context = SSLContext.getInstance("TLSv1.3");
            context.init((KeyManager[])null, new TrustManager[]{new JSkipSslVerificationHttpRequestFactory.SkipX509TrustManager()}, new SecureRandom());
            return context.getSocketFactory();
        }

        private class SkipX509TrustManager implements X509TrustManager {
            private SkipX509TrustManager() {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }

        private class SkipHostnameVerifier implements HostnameVerifier {
            private SkipHostnameVerifier() {
            }

            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        }
    }

}
