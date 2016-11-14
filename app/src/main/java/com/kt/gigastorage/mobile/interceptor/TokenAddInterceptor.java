package com.kt.gigastorage.mobile.interceptor;

import android.content.Context;

import com.kt.gigastorage.mobile.activity.FileAttrViewActivity;
import com.kt.gigastorage.mobile.activity.MainActivity;
import com.kt.gigastorage.mobile.activity.R;
import com.kt.gigastorage.mobile.utils.SharedPreferenceUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by a-raise on 2016-09-23.
 */
public class TokenAddInterceptor extends SharedPreferenceUtil implements Interceptor  {

    private static Context context = MainActivity.context;

    //Header에 토큰 값 전송
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = SharedPreferenceUtil.getSharedPreference(context,context.getString(R.string.xAuthToken));
        String cookie = SharedPreferenceUtil.getSharedPreference(context,context.getString(R.string.cookie));
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader(context.getString(R.string.cookie),cookie);
        builder.addHeader(context.getString(R.string.xAuthToken), token);
        return chain.proceed(builder.build());
    }
}
