package com.dimeno.network.sample;

import android.app.Application;

import com.dimeno.network.Network;
import com.dimeno.network.config.NetConfig;

/**
 * BaseApplication
 * Created by wangzhen on 2020/4/15.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Network.init(new NetConfig.Builder().build());
    }
}