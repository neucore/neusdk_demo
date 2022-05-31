package com.neucore.neusdk_demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

/**
 * 用户查询
 */
public class SearchActivity extends AppCompatActivity implements PermissionInterface {

    private TextView tv_title_name;
    private TextView tv_back;

    String TAG = "NEUCORE SearchActivity";
    private NetworkInterface networkInterface;
    private PermissionHelper mPermissionHelper;
    private UserDaoUtils mUserDaoUtils;
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_search:
                    String sec=et_search.getText().toString().trim();
                    if(TextUtils.isEmpty(sec)){
                        Toast.makeText(SearchActivity.this,"请输入搜索内容",Toast.LENGTH_SHORT).show();
                    }else{
                        projectAdapter.setLists(getUserList(sec,0));
                    }
                    break;
                case R.id.tv_back:
                    finish();
                    break;
            }
        }
    };
    private AbPullListView lv_yu;//用户列表
    private ProjectAdapter projectAdapter;

    private Spinner spinner_simple;
    private EditText et_search;
    private Button btn_search;
    private int limit=10;//分页查询 每页10条记录
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
//        mPermissionHelper = new PermissionHelper(this, this);
//        mPermissionHelper.requestPermissions();
        mUserDaoUtils=new UserDaoUtils(this);
        initView();
        initListener();
        initData();
    }

    private void initData() {

    }

    private void initView(){
        et_search= (EditText) findViewById(R.id.et_search);
        btn_search= (Button) findViewById(R.id.btn_search);
        spinner_simple= (Spinner) findViewById(R.id.spinner_simple);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_title_name = (TextView) findViewById(R.id.tv_title_name);
        tv_title_name.setText("用户查询");
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
                Intent intent=new Intent(SearchActivity.this,AddUserActivity.class);
                intent.putExtra("User",user);
                startActivity(intent);
            }
        });
        lv_yu.setAbOnListViewListener(new AbOnListViewListener() {
            @Override
            public void onRefresh() {
                String sec=et_search.getText().toString().trim();
                projectAdapter.setLists(getUserList(sec,0));
                lv_yu.stopRefresh();
            }

            @Override
            public void onLoadMore() {
                String sec=et_search.getText().toString().trim();
                List<User> users=projectAdapter.getList();
                int listsize = projectAdapter.getCount();
                double y = div(listsize, limit, 2);
                users.addAll(getUserList(sec,(int) Math.ceil(y)));
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
        tv_back.setOnClickListener(listener);
        btn_search.setOnClickListener(listener);
    }
    private List<User> getUserList(String sec,int offset){
        String itemSel=spinner_simple.getSelectedItem().toString();
        List<User> lists=new ArrayList<>();
        if("工号".equals(itemSel))
            lists=mUserDaoUtils.queryUserId(sec,offset);
        if("姓名".equals(itemSel))
            lists=mUserDaoUtils.queryName(sec,offset);
        if("卡号".equals(itemSel))
            lists=mUserDaoUtils.queryCardId(sec,offset);
        return lists;
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