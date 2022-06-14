package com.neucore.neusdk_demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.neucore.neusdk_demo.app.MyApplication;
import com.neucore.neusdk_demo.dialog.TransferAdminsDialog;
import com.neucore.neusdk_demo.neulink.MyInstaller;
import com.neucore.neusdk_demo.utility.Constants;
import com.neucore.neusdk_demo.utils.SPUtils;
import com.neucore.neusdk_demo.utils.SharePrefConstant;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private String TAG = "SplashActivity";
    private AppCompatActivity instance;
    public String[] STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //public String[] MOUNT = {Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
    public String[] NETWORK = {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET};
    public String[] PHONE = {Manifest.permission.READ_PHONE_STATE};
    public String[] CAMERA = {Manifest.permission.CAMERA};
    public String[] LOCK = {Manifest.permission.WAKE_LOCK};
    //public String[] WINDOW = {Manifest.permission.SYSTEM_ALERT_WINDOW};
    public String[] KEYGUARD = {Manifest.permission.DISABLE_KEYGUARD};
    public String[] WIFI = {Manifest.permission.ACCESS_WIFI_STATE};
    //public String[] REQUEST = {Manifest.permission.REQUEST_INSTALL_PACKAGES};
    public String[] RECEIVE = {Manifest.permission.RECEIVE_BOOT_COMPLETED};
    //public String[] READ_LOGS = {Manifest.permission.READ_LOGS};
    public String[] PERMISSIONS;
    {
        PERMISSIONS = concatAll(STORAGE, NETWORK,PHONE,CAMERA,LOCK,KEYGUARD,WIFI,RECEIVE);
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
        instance = this;
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
            mMyCountDownTimer = new MyCountDownTimer(1 * 1000, 2000);
            mMyCountDownTimer.start();
        } else {
            mMyCountDownTimer = new MyCountDownTimer(1 * 1000, 2000);
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
            if (!EasyPermissions.hasPermissions(SplashActivity.this, PERMISSIONS)) {
                EasyPermissions.requestPermissions(SplashActivity.this, "请允许权限，否则无法使用", 123, PERMISSIONS);
            }
        }

        @Override
        public void onFinish() {
            TransferAdminsDialog transferAdminsDialog = new TransferAdminsDialog(SplashActivity.this);
            transferAdminsDialog.show();
            transferAdminsDialog.setButtonCancelVerOnClick(new TransferAdminsDialog.CheckButtonCancelVerOnclick() {
                @Override
                public void onClick(View view) {
                    transferAdminsDialog.finish();

                    //竖屏 6421板子 800x1280
                    SPUtils.put(MyApplication.getContext(), SharePrefConstant.EQUIPMENT_TYPE, Constants.TYPE_6421_VER);
                    startActivity(new Intent(MyApplication.getContext(),MenuActivity.class));
                    finish();
                }
            });
            transferAdminsDialog.setButtonCancelOnClick(new TransferAdminsDialog.CheckButtonCancelOnclick() {
                @Override
                public void onClick(View view) {
                    transferAdminsDialog.finish();
                    //竖屏64010板子
                    //64010
                    SPUtils.put(MyApplication.getContext(), SharePrefConstant.EQUIPMENT_TYPE, Constants.TYPE_64010);
                    startActivity(new Intent(MyApplication.getContext(),MenuActivity.class));
                    finish();
                }
            });
            transferAdminsDialog.setButtonSureOnClick(new TransferAdminsDialog.CheckButtonSureOnclick() {
                @Override
                public void onClick(View view) {
                    transferAdminsDialog.finish();

                    //6421 横屏  1920x1080
                    //6421
                    SPUtils.put(MyApplication.getContext(), SharePrefConstant.EQUIPMENT_TYPE,Constants.TYPE_6421);
                    startActivity(new Intent(MyApplication.getContext(),MenuActivity.class));
                    finish();
                }
            });

            //Test
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    transferAdminsDialog.finish();

                    //竖屏 6421板子 800x1280
                    SPUtils.put(MyApplication.getContext(), SharePrefConstant.EQUIPMENT_TYPE, Constants.TYPE_6421_VER);
                    startActivity(new Intent(MyApplication.getContext(),MenuActivity.class));
                    finish();
                }
            },3000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
