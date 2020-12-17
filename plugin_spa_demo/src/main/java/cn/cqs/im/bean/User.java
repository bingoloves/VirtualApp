package cn.cqs.im.bean;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created on 2018/11/22 18:01
 *
 * @author zhangchaozhou
 */
public class User extends BmobUser {
    /**
     * 账户名
     */
    //private String userName;
    /**
     * 密码
     */
    //private String password;
    /**
     * 昵称
     */
    private String nickname;

    /**
     * 国家
     */

    private String country;

    /**
     * 年龄
     */
    private Integer age;


    /**
     * 性别
     */
    private Integer gender;


    /**
     * 用户当前位置
     */
    private BmobGeoPoint address;


    /**
     * 头像
     */
    private String avatar;
//    private BmobFile avatar;


    /**
     * 别名
     */
    private List<String> alias;

//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public void setPassword(String password) {
//        this.password = password;
//    }

    public String getNickname() {
        return nickname;
    }

    public User setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public User setCountry(String country) {
        this.country = country;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public User setAge(Integer age) {
        this.age = age;
        return this;
    }

    public Integer getGender() {
        return gender;
    }

    public User setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public BmobGeoPoint getAddress() {
        return address;
    }

    public User setAddress(BmobGeoPoint address) {
        this.address = address;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public User setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public List<String> getAlias() {
        return alias;
    }

    public User setAlias(List<String> alias) {
        this.alias = alias;
        return this;
    }
}
