package com.bingoloves.plugin_core.http;

import android.app.Application;
import android.os.Parcelable;
import android.text.TextUtils;

import com.tencent.mmkv.MMKV;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by bingo on 2020/9/25.
 * MMKV 缓存工具封装 代替SharedPreferences
 */

public class MMKVHelper {

    private static MMKVHelper mInstance;
    private static MMKV mv;
    private MMKVHelper() {
        mv = MMKV.defaultMMKV();
    }

    public static MMKVHelper init(Application application) {
        if (mInstance == null) {
            synchronized (MMKVHelper.class) {
                if (mInstance == null) {
                    MMKV.initialize(application);
                    mInstance = new MMKVHelper();
                }
            }
        }
        return mInstance;
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public static void encode(String key, Object object) {
        if (object instanceof String) {
            mv.encode(key, (String) object);
        } else if (object instanceof Integer) {
            mv.encode(key, (Integer) object);
        } else if (object instanceof Boolean) {
            mv.encode(key, (Boolean) object);
        } else if (object instanceof Float) {
            mv.encode(key, (Float) object);
        } else if (object instanceof Long) {
            mv.encode(key, (Long) object);
        } else if (object instanceof Double) {
            mv.encode(key, (Double) object);
        } else if (object instanceof byte[] ) {
            mv.encode(key, (byte[]) object);
        } else if (object instanceof Serializable) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = null;
            try {
                os = new ObjectOutputStream(bos);
                os.writeObject(object);
                byte[] bytes = bos.toByteArray();
                if (bytes != null){
                    mv.encode(key,bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mv.encode(key, object.toString());
        }
    }

    public static void encodeSet(String key,Set<String> sets) {
        mv.encode(key, sets);
    }

    public static void encodeParcelable(String key,Parcelable obj) {
        mv.encode(key, obj);
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    public static Integer decodeInt(String key) {
        return mv.decodeInt(key, 0);
    }
    public static Double decodeDouble(String key) {
        return mv.decodeDouble(key, 0.00);
    }
    public static Long decodeLong(String key) {
        return mv.decodeLong(key, 0L);
    }
    public static Boolean decodeBoolean(String key) {
        return mv.decodeBool(key, false);
    }
    public static Float decodeFloat(String key) {
        return mv.decodeFloat(key, 0F);
    }
    public static byte[] decodeBytes(String key) {
        return mv.decodeBytes(key);
    }
    public static String decodeString(String key) {
        return mv.decodeString(key,"");
    }
    public static Set<String> decodeStringSet(String key) {
        return mv.decodeStringSet(key, Collections.<String>emptySet());
    }
    public static Parcelable decodeParcelable(String key) {
        return mv.decodeParcelable(key, null);
    }

    public static Object decodeSerializable(String key) {
        byte[] bytes = mv.decodeBytes(key);
        Object data = null;
        if (bytes != null){
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bis);
                data = ois.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    ois.close();
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public static MMKV getMv() {
        return mv;
    }

    /**
     * 移除某个key对
     *
     * @param key
     */
    public static void removeKey(String key) {
        mv.removeValueForKey(key);
    }

    /**
     * 获取全部
     * @return
     */
    public static Map<String, ?> getAll(){
        return mv.getAll();
    }
    public static String[] getAllKeys(){
        return mv.allKeys();
    }
    public static String getAllKeysRecord(){
        return TextUtils.join(",",mv.allKeys());
    }
    public static boolean contains(String key){
        return mv.contains(key);
    }
    public static boolean containsKey(String key){
        return mv.containsKey(key);
    }

    /**
     * 清除所有key
     */
    public static void clearAll() {
        mv.clearAll();
    }
}
