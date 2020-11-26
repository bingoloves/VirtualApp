package com.bingoloves.plugin_core.http;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bingo on 2020/8/26 0026.
 * 响应体数组基类
 */

public class BaseListResponse<T> implements Serializable{

    /**
     * code : 0
     * data : []
     * msg : string
     */

    private int code;
    private List<T> data;
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

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
