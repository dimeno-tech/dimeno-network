package com.dimeno.network.sample;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dimeno.network.callback.LoadingCallback;
import com.dimeno.network.callback.ProgressCallback;
import com.dimeno.network.loading.DefaultLoadingPage;
import com.dimeno.network.sample.entity.PluginVersion;
import com.dimeno.network.sample.task.TestGetTask;
import com.dimeno.network.sample.task.TestGetTokenTask;
import com.dimeno.network.sample.task.TestPostFormTask;
import com.dimeno.network.sample.task.TestPostJsonTask;
import com.dimeno.network.sample.task.TestUploadTask;
import com.dimeno.permission.PermissionManager;
import com.dimeno.permission.callback.AbsPermissionCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TestGetTask testGetTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_get).setOnClickListener(this);
        findViewById(R.id.btn_post_json).setOnClickListener(this);
        findViewById(R.id.btn_post_form).setOnClickListener(this);
        findViewById(R.id.btn_upload).setOnClickListener(this);
        findViewById(R.id.btn_loading_page).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get:
                get();
                break;
            case R.id.btn_post_json:
                postJson();
                break;
            case R.id.btn_post_form:
                postForm();
                postJson();
                break;
            case R.id.btn_upload:
                PermissionManager.request(this, new AbsPermissionCallback() {
                    @Override
                    public void onGrant(String[] permissions) {
                        getToken();
                    }

                    @Override
                    public void onDeny(String[] deniedPermissions, String[] neverAskPermissions) {
                        Toast.makeText(MainActivity.this, "需要存储权限", Toast.LENGTH_SHORT).show();
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE);
                break;
            case R.id.btn_loading_page:
                startActivity(new Intent(this, LoadingActivity.class));
                break;
        }
    }

    private void getToken() {
        new TestGetTokenTask(new LoadingCallback<String>() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    upload(jsonObject.getString("result"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }).exe("gddc", "OjE1NjI4MjUxOTk1MzIsI");
    }

    private void upload(String token) {
        new TestUploadTask(new ProgressCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(int progress) {
                Log.e("TAG", "-> upload onProgress " + progress + " " + Thread.currentThread().getName());
            }

            @Override
            public void onError(int code, String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }).setTag(this)
                .put("token", token)
                .put("fileType", "MP3")
                .put("param1", "4")
                .put("param2", "{}")
                .put("param3", "")
                .putFile("sourceFile", Environment.getExternalStorageDirectory() + "/Recorder/sample.mp3")
                .exe();
    }

    private void postForm() {
        new TestPostFormTask(new ProgressCallback<PluginVersion>() {
            @Override
            public void onStart() {
                Log.e("TAG", "-> onStart");
            }

            @Override
            public void onProgress(int progress) {
                Log.e("TAG", "-> postForm onProgress " + progress + " " + Thread.currentThread().getName());
            }

            @Override
            public void onSuccess(PluginVersion data) {
                Toast.makeText(MainActivity.this, "Post Form -> " + data.version_name + " " + data.version_description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int code, String message) {
                Toast.makeText(MainActivity.this, "Post Form -> " + message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                Log.e("TAG", "-> onComplete");
            }
        }).setLoadingPage(new DefaultLoadingPage(findViewById(R.id.container))).exe();
    }

    private void postJson() {
        new TestPostJsonTask(new LoadingCallback<PluginVersion>() {
            @Override
            public void onSuccess(PluginVersion data) {
                Toast.makeText(MainActivity.this, "Post Json -> " + data.version_name + " " + data.version_description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int code, String message) {
                Toast.makeText(MainActivity.this, "Post Json -> " + message, Toast.LENGTH_SHORT).show();
            }
        }).exe();
    }

    private void get() {
        if (testGetTask != null) {
            Toast.makeText(this, "重试", Toast.LENGTH_SHORT).show();
            testGetTask.retry();
            return;
        }
        testGetTask = new TestGetTask(new LoadingCallback<PluginVersion>() {
            @Override
            public void onSuccess(PluginVersion data) {
                Toast.makeText(MainActivity.this, "Get -> " + data.version_name + " " + data.version_description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int code, String message) {
                Toast.makeText(MainActivity.this, "Get -> " + message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "请求被取消", Toast.LENGTH_SHORT).show();
            }
        });
        testGetTask.setTag(this).exe();
    }
}
