package com.neucore.neusdk_demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.neucore.neusdk_demo.adapter.RecordAdapter;
import com.neucore.neusdk_demo.receiver.PermissionInterface;
import com.neucore.neusdk_demo.service.db.RecordDaoUtils;
import com.neucore.neusdk_demo.service.db.bean.Record;
import com.neucore.neusdk_demo.utils.PermissionHelper;
import com.neucore.neusdk_demo.view.AbOnListViewListener;
import com.neucore.neusdk_demo.view.AbPullListView;

import java.math.BigDecimal;
import java.net.NetworkInterface;
import java.util.List;

/**
 * 记录查询
 */
public class RecordSearchListActivity extends AppCompatActivity implements PermissionInterface {

    private TextView tv_title_name;
    private TextView tv_back;

    String TAG = "NEUCORE RecordSearchListActivity";
    private NetworkInterface networkInterface;
    private PermissionHelper mPermissionHelper;
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_back:
                    finish();
                    break;
            }
        }
    };
    private AbPullListView lv_yu;//列表
    private RecordAdapter projectAdapter;
    private int limit=10;
    private RecordDaoUtils mRecordDaoUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_search_list);
//        mPermissionHelper = new PermissionHelper(this, this);
//        mPermissionHelper.requestPermissions();
        mRecordDaoUtils=new RecordDaoUtils(this);
        initView();
        initListener();
        initData();
    }
    int type=1;
    String keyWord="";
    String keyType="";
    long start=0;
    long end=0;
    private void initData() {
        Intent intent=getIntent();
        if(intent.getExtras()!=null){
             type=intent.getExtras().getInt("type");//0自定义 1全部 2其他
             keyWord=intent.getExtras().getString("keyWord");
             keyType=intent.getExtras().getString("keyType");
             start=intent.getExtras().getLong("start");
             end=intent.getExtras().getLong("end");
            projectAdapter.setLists(getSearchData(type,keyType,keyWord,start,end,0));
        }
    }
    private List<Record> getSearchData(int type, String keyType, String keyWord, long start, long end, int offset){
        List<Record> records=null;
        if(type==1){
            if("姓名".equals(keyType))
            records=mRecordDaoUtils.queryName(keyWord,offset);
            if("工号".equals(keyType))
                records=mRecordDaoUtils.queryRecordId(keyWord,offset);
            if("卡号".equals(keyType))
                records=mRecordDaoUtils.queryCardId(keyWord,offset);
        }else{
            if("姓名".equals(keyType))
                records=mRecordDaoUtils.queryName(keyWord,offset,start,end);
            if("工号".equals(keyType))
                records=mRecordDaoUtils.queryRecordId(keyWord,offset,start,end);
            if("卡号".equals(keyType))
                records=mRecordDaoUtils.queryCardId(keyWord,offset,start,end);
        }
        return records;
    }
    private void initView(){
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_title_name = (TextView) findViewById(R.id.tv_title_name);
        tv_title_name.setText("搜索结果");
        tv_title_name.setVisibility(View.VISIBLE);
        lv_yu = (AbPullListView) findViewById(R.id.lv_yu);
        lv_yu.getHeaderView().setHeaderProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
        lv_yu.getFooterView().setFooterProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
        lv_yu.setPullLoadEnable(true);
        lv_yu.setPullRefreshEnable(true);
        projectAdapter = new RecordAdapter(this);
        lv_yu.setAdapter(projectAdapter);
        lv_yu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Record user= (Record) parent.getAdapter().getItem(position);
                Intent intent=new Intent(RecordSearchListActivity.this,AddRecordActivity.class);
                intent.putExtra("Record",user);
                startActivity(intent);*/
            }
        });
        lv_yu.setAbOnListViewListener(new AbOnListViewListener() {
            @Override
            public void onRefresh() {
                List<Record> list=getSearchData(type,keyType,keyWord,start,end,0);
                projectAdapter.setLists(list);
                lv_yu.stopRefresh();
            }

            @Override
            public void onLoadMore() {
                List<Record> users=projectAdapter.getList();
                int listsize = projectAdapter.getCount();
                double y = div(listsize, limit, 2);
                List<Record> list=getSearchData(type,keyType,keyWord,start,end,(int) Math.ceil(y));
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
        tv_back.setOnClickListener(listener);
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