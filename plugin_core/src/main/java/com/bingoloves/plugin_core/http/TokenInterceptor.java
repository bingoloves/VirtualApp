package com.bingoloves.plugin_core.http;

import android.text.TextUtils;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String token = MMKVHelper.decodeString(ApiService.TOKEN);
        if (!TextUtils.isEmpty(token)){
            //LogUtils.e(token);
            Request updateRequest = request.newBuilder().header(ApiService.TOKEN, token).build();
            return chain.proceed(updateRequest);
        }
        return chain.proceed(request);
    }
}
