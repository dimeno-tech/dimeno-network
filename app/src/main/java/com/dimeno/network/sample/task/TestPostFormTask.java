package com.dimeno.network.sample.task;

import com.dimeno.network.callback.RequestCallback;
import com.dimeno.network.task.PostFormTask;

/**
 * PostFormTask
 * Created by wangzhen on 2020/4/16.
 */
public class TestPostFormTask extends PostFormTask {
    public <EntityType> TestPostFormTask(RequestCallback<EntityType> callback) {
        super(callback);
    }

    @Override
    public String getApi() {
        return "http://192.168.10.100:8080/wangzhen/plugin/plugin.json";
    }
}
