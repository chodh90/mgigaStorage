package com.kt.gigastorage.mobile.webservice.impl;

import android.content.Context;
import android.content.res.Resources;

import com.kt.gigastorage.mobile.activity.MainActivity;
import com.kt.gigastorage.mobile.activity.R;
import com.kt.gigastorage.mobile.interceptor.TokenAddInterceptor;
import com.kt.gigastorage.mobile.utils.SharedPreferenceUtil;
import com.kt.gigastorage.mobile.webservice.RestService;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by a-raise on 2016-09-08.
 */
public class RestServiceImpl {
    public static final int CONNECT_TIMEOUT = 15;
    public static final int WRITE_TIMEOUT = 15;
    public static final int READ_TIMEOUT = 15;
    private static OkHttpClient client;
    private static RestService restService;
    private static Context context = MainActivity.context;

    public synchronized static RestService getInstance(String baseUrl) {
        restService = null;

        if (baseUrl == null || baseUrl.equals("")) {
            Resources res = context.getResources();
            baseUrl = String.format(res.getString(R.string.serverUrl), SharedPreferenceUtil.getSharedPreference(context,"hostIp"));
        }
        if(restService == null){

            TokenAddInterceptor token = new TokenAddInterceptor();
            client = configureClient(new OkHttpClient.Builder())
                    .addNetworkInterceptor(token)
                    .build();

            restService = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create()) //Json Parser 추가
                    .build().create(RestService.class); //인터페이스 연결

        }
        return restService;
    }

    public static OkHttpClient.Builder configureClient(final OkHttpClient.Builder builder){
        final TrustManager[] certs = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};

        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, certs, new SecureRandom());
        } catch (final java.security.GeneralSecurityException ex){
            ex.printStackTrace();
        }

        try {
            final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            builder.sslSocketFactory(ctx.getSocketFactory()).hostnameVerifier(hostnameVerifier);
        } catch (final Exception e){
            e.printStackTrace();
        }

        return builder;
    }

}
