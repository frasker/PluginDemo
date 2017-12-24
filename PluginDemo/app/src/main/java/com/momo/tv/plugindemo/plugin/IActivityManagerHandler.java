package com.momo.tv.plugindemo.plugin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.momo.tv.plugindemo.StubActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.momo.tv.plugindemo.plugin.Plugin.TAG;

/**
 * Created by 默默 on 2017/12/23.
 */

public class IActivityManagerHandler implements InvocationHandler {
    private Context mContext;
    private Object mBase;
    public IActivityManagerHandler(Context context,Object base){
        mContext = context;
        mBase=base;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("startActivity")){
            Intent intent= (Intent) args[2];
            if (intent.getStringExtra(Plugin.PLUGIN_ACTIVITY).equals(Plugin.PLUGIN_ACTIVITY)){
                intent.putExtra(Plugin.PLUGIN_COMPONENT,intent.getComponent());
                intent.setComponent(new ComponentName(mContext, StubActivity.class));
                Log.i(TAG,"startActivity方法 hook 成功");
            }
        }
        return method.invoke(mBase,args);
    }
}
