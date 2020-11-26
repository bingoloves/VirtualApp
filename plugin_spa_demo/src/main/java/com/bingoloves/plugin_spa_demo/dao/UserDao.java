package com.bingoloves.plugin_spa_demo.dao;

import android.util.Log;
import android.view.View;

import com.bingoloves.plugin_spa_demo.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by bingo on 2020/11/26.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/26
 */

public class UserDao {
    /**
     * 统一回调处理
     */
    public interface Callback{
        void onSucceed(User user);
        void onError(BmobException e);
    }
    public interface JsonCallback{
        void onSucceed(String json);
        void onError(BmobException e);
    }
    public interface QueryUsersCallback{
        void onSucceed(List<User> users);
        void onError(BmobException e);
    }
    /**
     * 账号密码注册
     */
    public static void register(String userName,String password,Callback callback) {
        final User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    if (callback != null) callback.onSucceed(user);
                } else {
                    if (callback != null) callback.onError(e);
                }
            }
        });
    }

    /**
     * 账号密码登录
     */
    public static void login(String userName,String password,Callback callback) {
        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        user.login(new SaveListener<User>() {
            @Override
            public void done(User bmobUser, BmobException e) {
                if (e == null) {
                    if (callback != null) callback.onSucceed(user);
                } else {
                    if (callback != null) callback.onError(e);
                }
            }
        });
    }

    /**
     * 退出登录
     */
    public static void logOut(){
        BmobUser.logOut();
    }
    /**
     * 账号密码登录
     */
    public static void loginByAccount(String userName,String password,Callback callback) {
        BmobUser.loginByAccount(userName, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    if (callback != null) callback.onSucceed(user);
                } else {
                    if (callback != null) callback.onError(e);
                }
            }
        });
    }

    /**
     * 获取当前登录用户
     * @return
     */
    public static User getCurrentUser(){
        return BmobUser.getCurrentUser(User.class);
    }
    /**
     * 更新用户操作并同步更新本地的用户信息
     */
    public static void updateUser(Callback callback) {
        final User user = BmobUser.getCurrentUser(User.class);
        user.setAge(20);
        user.setAvatar("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1606374538307&di=6a1a074836bf078f22b4b74880d12eae&imgtype=0&src=http%3A%2F%2Fcdn.duitang.com%2Fuploads%2Fitem%2F201408%2F24%2F20140824154253_45Hay.png");
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    if (callback != null) callback.onSucceed(user);
                } else {
                    if (callback != null) callback.onError(e);
                }
            }
        });
    }

    /**
     * 修改当前用户密码
     */
    public static void resetPassword(String oldPassword,String newPassword,Callback callback) {
        BmobUser.updateCurrentUserPassword(oldPassword, newPassword,new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    if (callback != null) callback.onSucceed(null);
                } else {
                    if (callback != null) callback.onError(e);
                }
            }
        });
    }

    /**
     * 同步控制台数据到缓存中
     */
    public static void fetchUserInfo(Callback callback) {
        BmobUser.fetchUserInfo(new FetchUserInfoListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e == null) {
                    if (callback != null) callback.onSucceed(BmobUser.getCurrentUser(User.class));
                } else {
                    if (callback != null) callback.onError(e);
                }
            }
        });
    }
    /**
     * 查询用户表
     */
    public static void queryUser(QueryUsersCallback callback) {
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                if (e == null) {
                    if (callback != null) callback.onSucceed(object);
                } else {
                    if (callback != null) callback.onError(e);
                }
            }
        });
    }
    /**
     * 获取控制台最新数据
     * 获取所有用户的数据 需要登录状态下请求
     */
    public static void fetchUserJsonInfo(JsonCallback jsonCallback){
        BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
            @Override
            public void done(String json, BmobException e) {
                if (e == null) {
                    if (jsonCallback != null)jsonCallback.onSucceed(json);
                } else {
                    if (jsonCallback != null)jsonCallback.onError(e);
                }
            }
        });
    }
}
