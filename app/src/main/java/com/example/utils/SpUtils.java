package com.example.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.Touch.TouchPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * SharedPreferences 도구 클래스
 */
public class SpUtils {

    /**
     * 휴대폰에 저장된 파일 이름
     */
    private static final String FILE_NAME = "share_date";
    private static final String KEY_TOUCH_LIST = "touch_list";

    /**
     * 데이터를 저장하려면 저장된 데이터의 특정 유형을 가져온 다음 유형에 따라 다른 저장 방법을 호출해야합니다.
     * @param context
     * @param key
     * @param object
     */
    public static void setParam(Context context , String key, Object object){

        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if("String".equals(type)){
            editor.putString(key, (String)object);
        }
        else if("Integer".equals(type)){
            editor.putInt(key, (Integer)object);
        }
        else if("Boolean".equals(type)){
            editor.putBoolean(key, (Boolean)object);
        }
        else if("Float".equals(type)){
            editor.putFloat(key, (Float)object);
        }
        else if("Long".equals(type)){
            editor.putLong(key, (Long)object);
        }
        editor.apply();
    }


    /**
     * 데이터를 저장하는 방법을 가져오고, 기본값에 따라 저장된 데이터의 특정 유형을 가져온 다음 상대 방법을 호출하여 값을 가져옵니다.
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object getParam(Context context , String key, Object defaultObject){
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        if("String".equals(type)){
            return sp.getString(key, (String)defaultObject);
        }

        else if("Integer".equals(type)){
            return sp.getInt(key, (Integer)defaultObject);
        }

        else if("Boolean".equals(type)){
            return sp.getBoolean(key, (Boolean)defaultObject);
        }

        else if("Float".equals(type)){
            return sp.getFloat(key, (Float)defaultObject);
        }

        else if("Long".equals(type)){
            return sp.getLong(key, (Long)defaultObject);
        }

        return null;
    }

    /**
     * 모든 데이터 지우기
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().apply();
    }

    /**
     * 지정된 데이터 지우기
     * @param context
     */
    public static void clearAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("定义的键名");
        editor.apply();
    }

    public static void addTouchPoint(Context context, TouchPoint touchPoint) {
        List<TouchPoint> touchPoints = getTouchPoints(context);
        touchPoints.add(touchPoint);
    setTouchPoints(context, touchPoints);
}

    public static void setTouchPoints(Context context, List<TouchPoint> touchPoints) {
        String string = GsonUtils.beanToJson(touchPoints);
        setParam(context, KEY_TOUCH_LIST, string);
    }

    public static List<TouchPoint> getTouchPoints(Context context) {
        String string = (String) getParam(context, KEY_TOUCH_LIST, "");
        if (TextUtils.isEmpty(string)) {
            return new ArrayList<>();
        }
        return GsonUtils.jsonToList(string, TouchPoint.class);
    }
}
