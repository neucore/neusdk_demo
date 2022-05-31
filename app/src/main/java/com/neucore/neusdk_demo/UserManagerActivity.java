package com.neucore.neusdk_demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.neucore.neusdk_demo.adapter.ProjectAdapter;
import com.neucore.neusdk_demo.service.db.bean.User;
import com.neucore.neusdk_demo.service.db.UserDaoUtils;
import com.neucore.neusdk_demo.receiver.PermissionInterface;
import com.neucore.neusdk_demo.utils.PermissionHelper;
import com.neucore.neusdk_demo.view.AbOnListViewListener;
import com.neucore.neusdk_demo.view.AbPullListView;
import java.math.BigDecimal;
import java.net.NetworkInterface;
import java.util.List;

public class UserManagerActivity extends AppCompatActivity implements PermissionInterface {

    private TextView tv_title_name;
    private Button bt_search;
    private TextView tv_add;
    private TextView tv_back;

    String TAG = "NEUCORE UserManagerActivity";
    private NetworkInterface networkInterface;
    private PermissionHelper mPermissionHelper;
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_add:
                    startActivity(new Intent(UserManagerActivity.this,AddUserActivity.class));
                    break;
                case R.id.bt_search:
                    startActivity(new Intent(UserManagerActivity.this,SearchActivity.class));
                    break;
                case R.id.tv_back:
                    finish();
                    break;
            }
        }
    };
    private AbPullListView lv_yu;//用户列表
    private ProjectAdapter projectAdapter;
    private int limit=10;
    private UserDaoUtils mUserDaoUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);
//        mPermissionHelper = new PermissionHelper(this, this);
//        mPermissionHelper.requestPermissions();
        mUserDaoUtils=new UserDaoUtils(this);
        initView();
        initListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        List<User> list=mUserDaoUtils.queryUser(0);
        projectAdapter.setLists(list);
    }

    private void initView(){
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_add.setVisibility(View.VISIBLE);
        bt_search = (Button) findViewById(R.id.bt_search);
        bt_search.setVisibility(View.VISIBLE);
        tv_title_name = (TextView) findViewById(R.id.tv_title_name);
        tv_title_name.setText("用户管理");
        tv_title_name.setVisibility(View.VISIBLE);
        lv_yu = (AbPullListView) findViewById(R.id.lv_yu);
        lv_yu.getHeaderView().setHeaderProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
        lv_yu.getFooterView().setFooterProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
        lv_yu.setPullLoadEnable(true);
        lv_yu.setPullRefreshEnable(true);
        projectAdapter = new ProjectAdapter(this);
        lv_yu.setAdapter(projectAdapter);
        lv_yu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user= (User) parent.getAdapter().getItem(position);
                Intent intent=new Intent(UserManagerActivity.this,AddUserActivity.class);
                intent.putExtra("User",user);
                startActivity(intent);
            }
        });
        lv_yu.setAbOnListViewListener(new AbOnListViewListener() {
            @Override
            public void onRefresh() {
                List<User> list=mUserDaoUtils.queryUser(0);
//                List<User> list=mUserDaoUtils.queryAllUser();
                projectAdapter.setLists(list);
                lv_yu.stopRefresh();
            }

            @Override
            public void onLoadMore() {
                List<User> users=projectAdapter.getList();
                int listsize = projectAdapter.getCount();
                double y = div(listsize, limit, 2);
                List<User> list=mUserDaoUtils.queryUser((int) Math.ceil(y));
                users.addAll(list);
                projectAdapter.setLists(users);
                lv_yu.stopLoadMore();
//                getLocData(type, (int) Math.ceil(y), false, true,true);
            }
        });
    }
    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_UP).doubleValue();
    }
    private void initListener(){
        bt_search.setOnClickListener(listener);
        tv_add.setOnClickListener(listener);
        tv_back.setOnClickListener(listener);
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