package com.momo.tv.plugin.plugin;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;

/**
 * Created by 默默 on 2017/12/25.
 */

public abstract class BasePluginActivity extends Activity {

    @Override
    public AssetManager getAssets() {
        if (getApplication() != null && getApplication().getAssets() != null)
            return getApplication().getAssets();
        return super.getAssets();
    }

    @Override
    public Resources getResources() {
        if (getApplication() != null && getApplication().getResources() != null)
            return getApplication().getResources();
        return super.getResources();
    }
}
