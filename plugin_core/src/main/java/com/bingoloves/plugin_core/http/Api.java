package com.bingoloves.plugin_core.http;

/**
 * Created by bingo on 2020/10/16.
 */

public interface Api {
    /**
     * 登录接口
     */
    String LOGIN = "/api/index/login";
    /**
     * 登出
     */
    String LOGOUT = "/api/index/logout";
    /**
     * 预警消息
     */
    String WARN_TIPS = "/api/index/warnTips";
    /**
     * 预警统计
     */
    String ALARM_NUM = "/api/index/alarmNum";
    /**
     * 通行及卡口统计
     */
    String STATISTIC_BARRIER = "/api/index/statisticBarrier";
    /**
     * 预警列表
     */
    String WARN_LIST = "/api/search/index";
    /**
     * 车辆查询
     */
    String SEARCH_CAR = "/api/search/info";
    /**
     * 轨迹信息
     */
    String TRACE_INFO = "/api/search/trace";

    /**
     * 车辆布控列表
     */
    String DEPLOY_LIST = "/api/deploy/index";
    /**
     * 车辆布控详情
     */
    String DEPLOY_DETAIL = "/api/deploy/detail";
    /**
     * 车辆布控字典
     */
    String DEPLOY_DIC = "/api/deploy/getdic";
    /**
     * 车辆查询字典
     */
    String SEARCH_DIC = "/api/search/getdic";
    /**
     * 添加车辆布控
     */
    String DEPLOY_STORE = "/api/deploy/store";

    /*电子证据*/
    /**
     * 入网检测
     */
    String NET_CHECK = "/api/certificate/netcheck";
    /**
     * 抓拍图片
     */
    String SUSPECT = "/api/certificate/suspect";
    /**
     * 查看轨迹
     */
    String TRACE = "/api/certificate/trace";
    /**
     * 上传轨迹
     */
    String UPLOAD_TRACE = "/api/uploads/trace";
    /**
     * 查看电子证据
     */
    String CERTIFICATE_INFO = "/api/certificate/show";
    /**
     * 生成电子证据
     */
    String MAKE_CERTIFICATE = "/api/certificate/make";
}
