package com.momo.tv.plugindemo.plugin;

import android.content.Context;
import android.os.Environment;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

/**
 * Created by 默默 on 2017/12/22.
 */

public class Plugin {


    public boolean init(Context context) {
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

        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    private void handleDexElements(Context context) throws Exception {
        //dex 加载路径，也就是apk加载路径
        String dexPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pluginDemo.apk";
        String optimizedDirectory = context.getCacheDir().getAbsolutePath();
        //创建DexClassLoader
        DexClassLoader pluginClassLoader = new DexClassLoader(dexPath, optimizedDirectory, optimizedDirectory, context.getClassLoader());
        DexClassLoader hostClassLoader = (DexClassLoader) context.getClassLoader();
        //获取宿主的Elements数组
        Object hosElements = getElementsFromClassLoader(hostClassLoader);
        //获取插件的Elements数组
        Object pluginElements = getElementsFromClassLoader(pluginClassLoader);
        //拿到Elements数组的class,也就是Element.class
        Class<?> ELementClass = hosElements.getClass().getComponentType();
        //拿到宿主Elements数组的class,也就是Element.class
        int hostElementsLength = Array.getLength(hosElements);
        int pluginElementsLength = Array.getLength(pluginElements);
        Object newDexElements = Array.newInstance(ELementClass, hostElementsLength + pluginElementsLength);
        for (int i = 0; i < hostElementsLength + pluginElementsLength; i++) {
            if (i < hostElementsLength) {
                Array.set(newDexElements, i, Array.get(hosElements, i));
            } else {
                Array.set(newDexElements, i, Array.get(pluginElements, i - hostElementsLength));
            }
        }
        //将新构建的dexElements设置到宿主PathClassLoader中
        reflectSetNewDexElements(hostClassLoader,newDexElements);
    }

    private void reflectSetNewDexElements(DexClassLoader hostClassLoader, Object newDexElements) throws Exception{
        Field pathListField = hostClassLoader.getClass().getDeclaredField("pathList");
        pathListField.setAccessible(true);
        Object pathList = pathListField.get(null);

        Field dexElementsField = pathList.getClass().getDeclaredField("dexElements");
        dexElementsField.setAccessible(true);

        dexElementsField.set(pathList,newDexElements);
    }

    private Object getElementsFromClassLoader(DexClassLoader pluginClassLoader) throws Exception {
        Field pathListField = pluginClassLoader.getClass().getDeclaredField("pathList");
        pathListField.setAccessible(true);
        Object pathList = pathListField.get(null);

        Field dexElementsField = pathList.getClass().getDeclaredField("dexElements");
        dexElementsField.setAccessible(true);
        return dexElementsField.get(null);
    }


}
