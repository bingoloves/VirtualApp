package com.bingoloves.plugin_core.http;
import java.io.File;

/**
 * Created by Administrator on 2020/9/28 0028.
 */

public interface DownloadListener {
    void onStartDownload();
    void onProgress(int progress, long current, long total);
    void onFail(String errorInfo);
    void onSuccess(File file);
}
