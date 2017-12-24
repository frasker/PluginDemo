package com.momo.tv.plugindemo.plugin;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by 默默 on 2017/12/22.
 */

public class Plugin {
    public static final String TAG = "Plugin";
    public static final String PLUGIN_ACTIVITY ="plugin_activity";
    public static final String PLUGIN_COMPONENT ="plugin_component";

    public static boolean init(Context context) {
//        Environment.getDataDirectory() = /data
//        Environment.getDownloadCacheDirectory() = /cache
//        Environment.getExternalStorageDirectory() = /mnt/sdcard
//        Environment.getExternalStoragePublicDirectory(“test”) = /mnt/sdcard/test
//        Environment.getRootDirectory() = /system
//        getPackageCodePath() = /data/app/com.my.app-1.apk
//        getPackageResourcePath() = /data/app/com.my.app-1.apk
//        getCacheDir() = /data/data/com.my.app/cache
//        getDatabasePath(“test”) = /data/data/com.my.app/databases/test
//        getDir(“test”, Context.MODE_PRIVATE) = /data/data/com.my.app/app_test
//        getExternalCacheDir() = /mnt/sdcard/Android/data/com.my.app/cache
//        getExternalFilesDir(“test”) = /mnt/sdcard/Android/data/com.my.app/files/test
//        getExternalFilesDir(null) = /mnt/sdcard/Android/data/com.my.app/files
//        getFilesDir() = /data/data/com.my.app/files
        boolean success = true;

        try {
            handleDexElements(context);
            proxyActivityManager(context);
            reflectHandleActivityThread();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "plugin init: "+e.toString());
            success = false;
        }
        return success;
    }

    private static void reflectHandleActivityThread() throws Exception{
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod=activityThreadClass.getDeclaredMethod("currentActivityThread");
        currentActivityThreadMethod.setAccessible(true);
        Object currentActivityThread=currentActivityThreadMethod.invoke(null);

        Field mHField=activityThreadClass.getDeclaredField("mH");
        mHField.setAccessible(true);
        Object mH=mHField.get(currentActivityThread);

        Class<?> handlerClass = Class.forName("android.os.Handler");
        Field mCallbackField=handlerClass.getDeclaredField("mCallback");
        mCallbackField.setAccessible(true);
        mCallbackField.set(mH,new PluginAcitivityThreadHandlerCallback());
    }

    private static void proxyActivityManager(Context context) throws Exception{
        //gDefault 是一个 ActivityManagerNative的静态常量，是一个Singleton类
        Object gDefault = getFieldObject(null,Class.forName("android.app.ActivityManagerNative"),"gDefault");
        Field mInstanceField = getField(Class.forName("android.util.Singleton"),"mInstance");
        //拿到
        Object activityManager = mInstanceField.get(gDefault);

        Class<?> IActivityManager=Class.forName("android.app.IActivityManager");
        IActivityManagerHandler handler= new IActivityManagerHandler(context,activityManager);
        Object proxy= Proxy.newProxyInstance(context.getClassLoader(),new Class[]{IActivityManager},handler);
        mInstanceField.set(gDefault,proxy);
    }

    private static void handleDexElements(Context context) throws Exception {
        //dex 加载路径，也就是apk加载路径
        String dexPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pluginDemo.apk";
        //dex优化目录
        String optimizedDirectory = context.getCacheDir().getAbsolutePath();
        //创建DexClassLoader
        DexClassLoader pluginClassLoader = new DexClassLoader(dexPath, optimizedDirectory, optimizedDirectory, context.getClassLoader());
        //获取宿主的ClassLoader
        PathClassLoader hostClassLoader = (PathClassLoader) context.getClassLoader();
        //获取宿主的Elements数组
        Object[] hosElements = getElementsFromClassLoader(hostClassLoader);
        //获取插件的Elements数组
        Object[] pluginElements = getElementsFromClassLoader(pluginClassLoader);
        //将插件的Elements数组拼接到宿主的Elements数组上
        Object[] newDexElements=concat(hosElements,pluginElements);
        //将拼接的Elements设置到hostClassLoader内
        reflectSetNewDexElements(hostClassLoader,newDexElements);
    }

    private static Object[] concat(Object[] src, Object[] dst) {
        int srcLength = src.length;
        int dstLength = dst.length;
        src = Arrays.copyOf(src,srcLength + dstLength);
        System.arraycopy(dst, 0, src, srcLength, dstLength);
        return src;
    }


    private static void reflectSetNewDexElements(BaseDexClassLoader hostClassLoader, Object newDexElements) throws Exception{
        Object pathList = getFieldObject(hostClassLoader,Class.forName("dalvik.system.BaseDexClassLoader"),"pathList");
        Field dexElementsField = getField(pathList.getClass(),"dexElements");
        dexElementsField.set(pathList,newDexElements);
    }

    private static Object[] getElementsFromClassLoader(BaseDexClassLoader classLoader) throws Exception {
        Object pathList = getFieldObject(classLoader,Class.forName("dalvik.system.BaseDexClassLoader"),"pathList");
        getFieldObject(pathList,pathList.getClass(),"dexElements");
        return (Object[]) getFieldObject(pathList,pathList.getClass(),"dexElements");
    }

    private static Object getFieldObject(Object obj,Class<?> clz,String fieldName) throws Exception{
        Field field = clz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    private static Field getField(Class<?> clz,String fieldName) throws Exception{
        Field field = clz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

}
