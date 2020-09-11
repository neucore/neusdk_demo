package com.neucore.neusdk_demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.neucore.neusdk_demo.app.MyApplication;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    public String[] STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public String[] CAMERA = {Manifest.permission.CAMERA};
    public String[] PHONE = {Manifest.permission.READ_PHONE_STATE};
    public String[] PERMISSIONS;

    {
        PERMISSIONS = concatAll(STORAGE,CAMERA,PHONE);
    }
    public <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    private MyCountDownTimer mMyCountDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //跳转到主界面
        if (!EasyPermissions.hasPermissions(SplashActivity.this, PERMISSIONS)) {
            EasyPermissions.requestPermissions(SplashActivity.this, "请允许权限，否则无法使用", 123, PERMISSIONS);
        }else {
            initView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == 123) {
            initView();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        EasyPermissions.requestPermissions(SplashActivity.this, "请允许权限，否则无法使用", 123, PERMISSIONS);
    }

    private void initView() {
        if (mMyCountDownTimer != null) {
            mMyCountDownTimer.cancel();
            mMyCountDownTimer = new MyCountDownTimer(1 * 1000, 1000);
            mMyCountDownTimer.start();
        } else {
            mMyCountDownTimer = new MyCountDownTimer(1 * 1000, 1000);
            mMyCountDownTimer.start();
        }

    }

    class MyCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            startActivity(new Intent(MyApplication.getContext(),MenuActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
