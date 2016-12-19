package com.s16.app;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by SMM on 11/17/2016.
 */

public class BuildConfigWrapper {

    public static boolean DEBUG;
    public static String APPLICATION_ID;
    public static String BUILD_TYPE;
    public static String FLAVOR;
    public static int VERSION_CODE;
    public static String VERSION_NAME;

    private static String PACKAGE_NAME = "com.s16.dhammadroid"; // BuildConfigWrapper.class.getPackage().getName();
    private static String BUILD_CONFIG_CLASS_NAME = PACKAGE_NAME  + ".BuildConfig";
    static {
        DEBUG = getStaticFieldValue("DEBUG", false);
        APPLICATION_ID = getStaticFieldValue("APPLICATION_ID", PACKAGE_NAME);
        BUILD_TYPE = getStaticFieldValue("BUILD_TYPE", "");
        FLAVOR = getStaticFieldValue("FLAVOR", "");
        VERSION_CODE = getStaticFieldValue("VERSION_CODE", 0);
        VERSION_NAME = getStaticFieldValue("VERSION_NAME", "");
    }

    private static <T> T getStaticFieldValue(String fieldName, T defValue) {
        if (TextUtils.isEmpty(fieldName)) return null;
        Class<?> targetClass = null;
        try {
            targetClass = Class.forName(BUILD_CONFIG_CLASS_NAME);
        } catch (ClassNotFoundException e) {
        }

        if (targetClass != null) {
            try {
                Field field = targetClass.getDeclaredField(fieldName);
                if (field != null && Modifier.isStatic(field.getModifiers())) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    Object retValue = field.get(null);
                    if (retValue != null) {
                        return (T) retValue;
                    }
                }
            } catch (SecurityException e) {
                // ignore
            } catch (NoSuchFieldException e) {
                // ignore
            } catch (IllegalAccessException e) {
                // ignore
            }
        }
        return defValue;
    }
}
