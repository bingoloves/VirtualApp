package com.bingoloves.plugin_core.http;

import android.content.Context;
import android.os.AsyncTask;

import com.bingoloves.plugin_core.utils.log.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import top.zibin.luban.Luban;

/**
 * Created by Administrator on 2020/9/28 0028.
 */

public class UploadUtils {

    /**
     * 上传图片
     * @param context
     * @param path
     */
    public static void uploadFiles(Context context, List<String> path, Observer observer){
        if (context == null || path == null || path.isEmpty()){
            return;
        }
//        Flowable.just(path)
//                .observeOn(Schedulers.io())
//                .map(list -> Luban.with(context).load(list).get())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(files -> {
//                    List<MultipartBody.Part> images = filesToMultipartBodyParts(files);
//                    RetrofitUtil.get().getApiService()
//                            .uploadImages(images)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(observer);
//                });
//        AppExecutors.getInstance().diskIO().execute(() ->
//                Flowable.just(path)
//                        .observeOn(Schedulers.io())
//                        .map(list -> Luban.with(context).load(list).get())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(files -> {
//                            List<MultipartBody.Part> images = filesToMultipartBodyParts(files);
//                            RetrofitUtil.get().getApiService()
//                                    .uploadImages(images)
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(observer);
//                        }));
        //异步任务的形式异步处理
        new AsyncTask<Integer, Integer, List<File>>() {
            @Override
            protected List<File> doInBackground(Integer... integers) {
                List<File> compressFiles = new ArrayList<>();
                try {
                    compressFiles = Luban.with(context).load(path).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return compressFiles;
            }

            @Override
            protected void onPostExecute(List<File> files) {
                super.onPostExecute(files);
                List<MultipartBody.Part> images = filesToMultipartBodyParts(files);
                RetrofitUtil.get().getApiService()
                        .uploadImages(images)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observer);
            }
        }.execute();
    }

    /**
     * 文件下载
     * @param url    文件地址
     * @param file   写入的目标文件
     * @param downloadListener 下载监听
     */
    public static void download(String url,File file,DownloadListener downloadListener){
        OkHttpClient.Builder builder = RetrofitUtil.get().getBuilder().addInterceptor(new DownloadInterceptor(downloadListener));
        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        retrofit.create(ApiService.class)
                .download(url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(ResponseBody::byteStream)
                .observeOn(Schedulers.computation())
                .doOnNext(inputStream -> writeFile(inputStream, file))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<InputStream>() {
                    @Override
                    public void onSuccess(InputStream data) {
                        downloadListener.onSuccess(file);
                    }

                    @Override
                    public void onFailure(String err) {
                        downloadListener.onFail(err);
                    }
                });

    }
    /**
     * @param files 多图片文件转表单
     * @return
     */
    public static List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files) {
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("form-data"), file);
//            RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            parts.add(part);
        }
        return parts;
    }

    /**
     * 将输入流写入文件
     * @param inputString
     * @param file
     */
    private static void writeFile(InputStream inputString, File file) {
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int len;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b,0,len);
            }
            inputString.close();
            fos.close();
        } catch (FileNotFoundException e) {
            //downloadListener.onFail("FileNotFoundException");
        } catch (IOException e) {
            //downloadListener.onFail("IOException");
        }

    }
    /**
     * 示例代码
     * @param context
     */
    public static void testUpload(Context context){
        List<String> list = new ArrayList<>();
        String path = "/storage/emulated/0/Pictures/res/mipmap-mdpi-v4/ysa_logo.png";
        LogUtils.e("原始文件大小 = "+ new File(path).length());
        list.add(path);
        UploadUtils.uploadFiles(context, list, new BaseObserver() {
            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onFailure(String err) {

            }
        });
    }

    /**
     * 测试下载示例
     * @param context
     */
    public static void testDownload(Context context){
        UploadUtils.download("https://down.qq.com/qqweb/QQ_1/android_apk/Androidqq_8.4.8.4810_537065343.apk", new File(context.getCacheDir(), "qq.apk"), new DownloadListener() {

            @Override
            public void onStartDownload() {
                LogUtils.e("开始下载");
            }

            @Override
            public void onProgress(int progress, long current, long total) {
                LogUtils.e("下载进度："+progress + "-------------current = "+current+"--------total = "+total);
            }

            @Override
            public void onFail(String errorInfo) {
                LogUtils.e("下载失败");
            }

            @Override
            public void onSuccess(File file) {
                LogUtils.e("下载成功");
            }
        });
    }
}
