package com.momo.tv.plugindemo.plugin;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;

import static com.momo.tv.plugindemo.plugin.Plugin.TAG;

/**
 * Created by 默默 on 2017/12/23.
 */

public class PluginAcitivityThreadHandlerCallback implements Handler.Callback {
    public static final int LAUNCH_ACTIVITY  = 100;
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case LAUNCH_ACTIVITY:
                Class<?> ActivityClientRecordClass;
                try {
                    ActivityClientRecordClass = Class.forName("android.app.ActivityThread$ActivityClientRecord");
                    Field intentField = ActivityClientRecordClass.getDeclaredField("intent");
                    intentField.setAccessible(true);
                    Intent intent = (Intent) intentField.get(msg.obj);
                    if (intent.getStringExtra(Plugin.PLUGIN_ACTIVITY).equals(Plugin.PLUGIN_ACTIVITY)) {
                        intent.setComponent((ComponentName) intent.getParcelableExtra(Plugin.PLUGIN_COMPONENT));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "插件 mCallback 替换失败 "+e.toString());
                }
                break;
        }
        return false;
    }
}
