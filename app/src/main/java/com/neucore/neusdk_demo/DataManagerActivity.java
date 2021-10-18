package com.neucore.neusdk_demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neusdk_demo.db.RecordDaoUtils;
import com.neucore.neusdk_demo.db.UserDaoUtils;
import com.neucore.neusdk_demo.utils.CleanMessageUtil;
import com.neucore.neusdk_demo.utils.PermissionHelper;
import com.neucore.neusdk_demo.receiver.PermissionInterface;

import java.io.File;
import java.net.NetworkInterface;

/**
 * 数据管理
 */
public class DataManagerActivity extends AppCompatActivity implements PermissionInterface {

    private LinearLayout ll_data_user;
    private LinearLayout ll_data_data;
    private LinearLayout ll_data_search;
    private LinearLayout ll_data_set;
    private LinearLayout ll_data_export;
    private LinearLayout ll_data_system;
    private LinearLayout ll_data_face;
    private TextView tv_title_name;
    private TextView tv_back;
    String TAG = "NEUCORE DataManagerActivity";
    private NetworkInterface networkInterface;
    private PermissionHelper mPermissionHelper;
    private UserDaoUtils mUserDaoUtils;
    private RecordDaoUtils mRecordDaoUtils;
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_data_user:
                    if(!mRecordDaoUtils.deleteAll())Toast.makeText(DataManagerActivity.this, "删除考勤记录失败", Toast.LENGTH_SHORT).show();
                    if(!CleanMessageUtil.deleteDir(new File(NeulinkConst.filePath))) Toast.makeText(DataManagerActivity.this, "删除本地数据失败", Toast.LENGTH_SHORT).show();
                    if(!mUserDaoUtils.deleteAll()) Toast.makeText(DataManagerActivity.this, "删除用户数据失败", Toast.LENGTH_SHORT).show();
                    Toast.makeText(DataManagerActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.ll_data_search:
                    if(mRecordDaoUtils.deleteAll()) {
                        Toast.makeText(DataManagerActivity.this, "删除考勤记录成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(DataManagerActivity.this, "删除考勤记录失败", Toast.LENGTH_SHORT).show();
                    }                    break;
                case R.id.ll_data_data:
                    if(CleanMessageUtil.deleteDir(new File(NeulinkConst.photoPath))) {
                        Toast.makeText(DataManagerActivity.this, "删除抓拍照片成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(DataManagerActivity.this, "删除抓拍照片失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.ll_data_set:
                    if(mUserDaoUtils.deleteAll()) {
                        Toast.makeText(DataManagerActivity.this, "删除用户数据成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(DataManagerActivity.this, "删除用户数据失败", Toast.LENGTH_SHORT).show();
                    }                    break;
                case R.id.ll_data_system:
//                    MenuActivity.this.startActivity(new Intent(MenuActivity.this,UserManagerActivity.class));
                    break;
                case R.id.ll_data_export:
//                    MenuActivity.this.startActivity(new Intent(MenuActivity.this,UserManagerActivity.class));
                    break;
                case R.id.ll_data_face:
                    DataManagerActivity.this.startActivity(new Intent(DataManagerActivity.this,DetectActivity.class));
                    break;
                case R.id.tv_back:
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
//        mPermissionHelper = new PermissionHelper(this, this);
//        mPermissionHelper.requestPermissions();
        mUserDaoUtils=new UserDaoUtils(this);
        mRecordDaoUtils=new RecordDaoUtils(this);
        initView();
        initListener();
    }
    private void initView(){
        ll_data_user = (LinearLayout) findViewById(R.id.ll_data_user);
        ll_data_search = (LinearLayout) findViewById(R.id.ll_data_search);
        ll_data_data = (LinearLayout) findViewById(R.id.ll_data_data);
        ll_data_export = (LinearLayout) findViewById(R.id.ll_data_export);
        ll_data_system = (LinearLayout) findViewById(R.id.ll_data_system);
        ll_data_set = (LinearLayout) findViewById(R.id.ll_data_set);
        ll_data_face = (LinearLayout) findViewById(R.id.ll_data_face);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_title_name = (TextView) findViewById(R.id.tv_title_name);
        tv_title_name.setText("数据管理");
        tv_title_name.setVisibility(View.VISIBLE);
    }
    private void initListener(){
        ll_data_user.setOnClickListener(listener);
        ll_data_search.setOnClickListener(listener);
        ll_data_data.setOnClickListener(listener);
        ll_data_export.setOnClickListener(listener);
        ll_data_system.setOnClickListener(listener);
        ll_data_set.setOnClickListener(listener);
        tv_back.setOnClickListener(listener);
        ll_data_face.setOnClickListener(listener);
    }
    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
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