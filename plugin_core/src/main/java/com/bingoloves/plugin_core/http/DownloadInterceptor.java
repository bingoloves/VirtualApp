package com.bingoloves.plugin_core.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by bingo on 2020/9/28.
 */

public class DownloadInterceptor implements Interceptor {
    private DownloadListener downloadListener;
    public DownloadInterceptor(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder().body(new DownloadResponseBody(response.body(), downloadListener)).build();
    }
}
