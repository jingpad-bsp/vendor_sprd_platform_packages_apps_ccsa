package com.unisoc.ccsa.permission.util;

import com.unisoc.ccsa.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class SystemPropertiesProxy {
    private static final String TAG = "SystemPropertiesProxy";

    private SystemPropertiesProxy() {
    }

    public static boolean getBoolean(String key, boolean def) throws IllegalArgumentException {
        try {
            Class SystemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method getBooleanMethod =
                    SystemPropertiesClass.getDeclaredMethod(
                            "getBoolean", String.class, boolean.class);
            getBooleanMethod.setAccessible(true);
            return (boolean) getBooleanMethod.invoke(SystemPropertiesClass, key, def);
        } catch (InvocationTargetException
                | IllegalAccessException
                | NoSuchMethodException
                | ClassNotFoundException e) {
            Log.e(TAG, "Failed to invoke SystemProperties.getBoolean()", e);
        }
        return def;
    }

    public static int getInt(String key, int def) throws IllegalArgumentException {
        try {
            Class SystemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method getIntMethod =
                    SystemPropertiesClass.getDeclaredMethod("getInt", String.class, int.class);
            getIntMethod.setAccessible(true);
            return (int) getIntMethod.invoke(SystemPropertiesClass, key, def);
        } catch (InvocationTargetException
                | IllegalAccessException
                | NoSuchMethodException
                | ClassNotFoundException e) {
            Log.e(TAG, "Failed to invoke SystemProperties.getInt()", e);
        }
        return def;
    }

    public static String get(String key) throws IllegalArgumentException {
        return get(key, "");
    }

    public static String get(String key, String def) throws IllegalArgumentException {
        try {
            Class SystemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method getIntMethod =
                    SystemPropertiesClass.getDeclaredMethod("get", String.class, String.class);
            getIntMethod.setAccessible(true);
            return (String) getIntMethod.invoke(SystemPropertiesClass, key, def);
        } catch (InvocationTargetException
                | IllegalAccessException
                | NoSuchMethodException
                | ClassNotFoundException e) {
            Log.e(TAG, "Failed to invoke SystemProperties.get()", e);
        }
        return def;
    }

    public static void set(String key, String value) throws IllegalArgumentException {
        try {
            Class SystemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method getIntMethod =
                    SystemPropertiesClass.getDeclaredMethod("set", String.class, String.class);
            getIntMethod.setAccessible(true);
            getIntMethod.invoke(SystemPropertiesClass, key, value);
        } catch (InvocationTargetException
                | IllegalAccessException
                | NoSuchMethodException
                | ClassNotFoundException e) {
            Log.e(TAG, "Failed to invoke SystemProperties.set()", e);
        }
    }
}