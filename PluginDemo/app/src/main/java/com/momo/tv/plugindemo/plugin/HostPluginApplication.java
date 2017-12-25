package com.momo.tv.plugindemo.plugin;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by 默默 on 2017/12/25.
 */

public class HostPluginApplication extends Application {
    private Resources mResources;
    private AssetManager mAssetManager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        Plugin.init(base);

        String dexPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pluginDemo.apk";
        try {
            mAssetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            Resources hostResources = getResources();
            addAssetPathMethod.invoke(mAssetManager, dexPath);
            mResources = new Resources(mAssetManager, hostResources.getDisplayMetrics(), hostResources.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(Plugin.TAG,e.toString());
        }
    }

    @Override
    public AssetManager getAssets() {
        if (mAssetManager != null) {
            return mAssetManager;
        }
        return super.getAssets();
    }

    @Override
    public Resources getResources() {
        if (mResources != null) {
            return mResources;
        }
        return super.getResources();
    }
}
