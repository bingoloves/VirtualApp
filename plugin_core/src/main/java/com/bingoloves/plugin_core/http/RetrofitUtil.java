package com.bingoloves.plugin_core.http;

import com.bingoloves.plugin_core.BuildConfig;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * retrofit 工具封装
 */
public class RetrofitUtil {
    private static RetrofitUtil retrofitUtil;
    private Retrofit retrofit;
    private OkHttpClient.Builder builder;
    private RetrofitUtil(){
        builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new TokenInterceptor())
                ;
        if (BuildConfig.HTTP_LOG){
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        OkHttpClient client = builder.build();
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static RetrofitUtil get() {
        if (retrofitUtil == null) {
            synchronized (RetrofitUtil.class) {
                if (retrofitUtil == null) {
                    retrofitUtil = new RetrofitUtil();
                }
            }
        }
        return retrofitUtil;
    }

    public OkHttpClient.Builder getBuilder() {
        return builder;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }


}
