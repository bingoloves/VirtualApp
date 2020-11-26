package com.bingoloves.plugin_core.http;

import java.io.Serializable;

/**
 * Created by bingo on 2020/8/26 0026.
 * 响应体基类
 */

public class BaseResponse<T> implements Serializable{

    /**
     * code : 0
     * data : {}
     * msg : string
     */

    private int code;
    private T data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
