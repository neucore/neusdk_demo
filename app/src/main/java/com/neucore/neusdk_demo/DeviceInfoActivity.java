package com.neucore.neusdk_demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neusdk_demo.receiver.PermissionInterface;
import com.neucore.neusdk_demo.utils.PermissionHelper;

import java.net.NetworkInterface;

/**
 * 系统信息
 */
public class DeviceInfoActivity extends AppCompatActivity implements PermissionInterface {

    private TextView tv_title_name;
    private TextView tv_back;
    private TextView tv_model;
    private TextView tv_serial;
    private TextView tv_mac;
    private TextView tv_version;
    private TextView tv_android_version;
    String TAG = "NEUCORE DeviceInfoActivity";
    private NetworkInterface networkInterface;
    private PermissionHelper mPermissionHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deviceinfo);
//        mPermissionHelper = new PermissionHelper(this, this);
//        mPermissionHelper.requestPermissions();
        initView();
        initData();
        initListener();
    }
    private void initData() {
        tv_title_name.setText("系统信息");
        tv_model.setText(DeviceUtils.getModel());
        tv_serial.setText(android.os.Build.SERIAL);
        tv_android_version.setText(android.os.Build.VERSION.RELEASE);
        tv_version.setText(android.os.Build.VERSION.CODENAME);
        tv_mac.setText(DeviceUtils.getMacAddress());
    }

    private void initView(){
        tv_android_version = (TextView) findViewById(R.id.tv_android_version);
        tv_model = (TextView) findViewById(R.id.tv_model);
        tv_serial = (TextView) findViewById(R.id.tv_serial);
        tv_mac = (TextView) findViewById(R.id.tv_mac);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_title_name = (TextView) findViewById(R.id.tv_title_name);
        tv_title_name.setVisibility(View.VISIBLE);
    }
    private void initListener(){
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100) {
        }
    }

    @Override
    protected void onDestroy() {
        LogUtils.eTag(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public int getPermissionsRequestCode() {
        //设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可。
        return 10000;
    }

    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        };
    }

    @Override
    public void requestPermissionsSuccess() {
        initView();
    }

    @Override
    public void requestPermissionsFail() {
        initView();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}