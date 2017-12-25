package com.momo.tv.plugindemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.momo.tv.plugindemo.plugin.Plugin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.momo.tv.plugin",
                        "com.momo.tv.plugin.PluginActivity"));
                intent.putExtra(Plugin.PLUGIN_ACTIVITY,Plugin.PLUGIN_ACTIVITY);
                startActivity(intent);
            }
        });
    }

    @Override
    public Resources getResources() {
        return super.getResources();
    }
}
