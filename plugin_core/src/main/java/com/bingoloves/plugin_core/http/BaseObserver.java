package com.bingoloves.plugin_core.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.bingoloves.plugin_core.utils.Utils;

import org.json.JSONException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * 针对普通对象
 * @param <T>
 */
public abstract class BaseObserver<T> implements Observer<T> {

    private Disposable d;
    private ProgressDialog loading;
    private Handler handler;
    //持续时间最小2000ms保证体验效果
    private static final int MIN_TIME = 2000;
    private long startTime = 0;
    //是否显示加载框
    private boolean showLoading = false;
    //是否等待加载
    private boolean isWaitLoading = false;

    public BaseObserver() {}

    public BaseObserver(boolean showLoading) {
        this.showLoading = showLoading;
    }

    public BaseObserver(boolean showLoading,boolean isWaitLoading) {
        this.showLoading = showLoading;
        this.isWaitLoading = isWaitLoading;
    }
    @Override
    public void onSubscribe(Disposable d) {
        this.d = d;
        Context context = Utils.getApp();
        if (loading == null){
            loading = new ProgressDialog(context);
        }
        if (isWaitLoading){
            loading.setCanceledOnTouchOutside(false);
        }
        if (showLoading)loading.show();
        handler = new Handler(Looper.getMainLooper());
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onNext(final T data) {
        if (data instanceof BaseResponse){
            BaseResponse response =  (BaseResponse) data;
            Object object = response.getData();
            if (object != null){
                int code = response.getCode();
                String msg = response.getMsg();
                handleResponse(code,data,msg);
            } else {
              onFailure("数据异常");
            }
        } else if (data instanceof BaseListResponse){
            BaseListResponse response =  (BaseListResponse) data;
            List responseData = response.getData();
            if (responseData != null){
                int code = response.getCode();
                String msg = response.getMsg();
                handleResponse(code,data,msg);
            } else {
                onFailure("数据异常");
            }
        }
    }

    /**
     * 统一数据处理
     * @param code
     * @param data
     * @param msg
     */
    private void handleResponse(int code, T data, String msg){
        if (code == 1){
            hideDialog();
            onSuccess(data);
        } else if (code == 0){
            hideDialog();
            onFailure(msg);
        } else {
//            Activity topActivity = ActivityStackManager.getStackManager().currentActivity();
//            if (topActivity != null && !"cn.cncqs.zc.activity.LoginActivity".equals(topActivity.getClass().getCanonicalName())){
//                Intent intent = new Intent(topActivity,LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                topActivity.startActivity(intent);
//            }
        }
    }
    @Override
    public void onError(final Throwable e) {
        onFailure(exceptionHandler(e));
        hideDialog();
        onComplete();
    }

    @Override
    public void onComplete() {
        if (d.isDisposed()) {
            d.dispose();
        }
        handleDialogEvent(this::hideDialog);
    }

    public abstract void onSuccess(T data);

    public abstract void onFailure(String err);

    /**
     * 本地网络不可用时异常,会回调这个方法
     */
    public void onNetworkError(){
         hideDialog();
     }

    /**
     * 处理dialog 显示效果
     */
    private void handleDialogEvent(Runnable runnable){
         long endTime = System.currentTimeMillis();
         long timeDiff = endTime - startTime;
         if (timeDiff > MIN_TIME){
             handler.post(runnable);
         } else {
             handler.postDelayed(runnable,MIN_TIME - timeDiff);
         }
    }

    /**
     * 隐藏dialog
     */
    private void hideDialog(){
        if (loading != null && loading.isShowing()){
            loading.dismiss();
        }
    }
    /**
     * 异常处理
     * @param e
     * @return 返回错误信息
     */
    private String exceptionHandler(Throwable e){
        String errorMsg = "未知错误";
        if (e instanceof UnknownHostException) {
            errorMsg = "网络不可用";
        } else if (e instanceof SocketTimeoutException) {
            errorMsg = "请求网络超时";
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            errorMsg = convertStatusCode(httpException);
        } else if (e instanceof ParseException || e instanceof JSONException) {
            errorMsg =  "数据解析错误";
        }
        return errorMsg;
    }

    private String convertStatusCode(HttpException httpException) {
        String msg;
        if (httpException.code() >= 500 && httpException.code() < 600) {
            msg =  "服务器处理请求出错";
        } else if (httpException.code() >= 400 && httpException.code() < 500) {
            msg =  "服务器无法处理请求";
        } else if (httpException.code() >= 300 && httpException.code() < 400) {
            msg =  "请求被重定向到其他页面";
        } else {
            msg = httpException.message();
        }
        return msg;
    }
}
