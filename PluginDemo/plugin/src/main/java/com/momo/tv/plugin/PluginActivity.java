package com.momo.tv.plugin;

import android.os.Bundle;

import com.momo.tv.plugin.plugin.BasePluginActivity;

public class PluginActivity extends BasePluginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);
    }
}
