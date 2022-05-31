package com.neucore.neusdk_demo;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.neucore.neusdk_demo.receiver.PermissionInterface;
import com.neucore.neulink.util.DatesUtil;
import com.neucore.neusdk_demo.utils.PermissionHelper;

import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 记录查询
 */
public class RecoSeachActivity extends AppCompatActivity implements PermissionInterface {

    private TextView tv_title_name;
    private Button bt_search;
    private TextView tv_back;

    String TAG = "NEUCORE RecoSeachActivity";
    private NetworkInterface networkInterface;
    private PermissionHelper mPermissionHelper;
    private int type=0;//0自定义 1全部 2其他
    private long startTime;
    private long endTime;
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_search:
                    String keyType=spinner_simple.getSelectedItem().toString();
                    String keyWord=et_search.getText().toString().trim();
                    Intent intent=new Intent(RecoSeachActivity.this,RecordSearchListActivity.class);
                    intent.putExtra("keyType",keyType);
                    intent.putExtra("keyWord",keyWord);
                    intent.putExtra("type",type);
                    if(type==0){
                    String dateTimeStartStr=tv_start.getText().toString().trim();
                    String dateTimeEndStr=tv_end.getText().toString().trim();
                    if("xxxx.xx.xx xx.xx".equals(dateTimeEndStr)||"xxxx.xx.xx xx.xx".equals(dateTimeStartStr)) {
                        Toast.makeText(RecoSeachActivity.this, "请选择开始和结束日期", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        try {
                            Date dateStart=dateFormat.parse(dateTimeStartStr);
                            Date dateEnd=dateFormat.parse(dateTimeEndStr);
                            startTime=dateStart.getTime();
                            endTime=dateEnd.getTime();
                        } catch (ParseException e) {
                            LogUtils.eTag(TAG,"listener",e);
                        }
                    }
                    }else if(type==1){
                        startTime=0;
                        endTime=0;
                    }else{
                    }
                    if(startTime>endTime){
                        Toast.makeText(RecoSeachActivity.this, "开始日期不能大于结束日期", Toast.LENGTH_SHORT).show();
                    }else{
                        intent.putExtra("start",startTime);
                        intent.putExtra("end",endTime);
                        startActivity(intent);
                    }
                    break;
                case R.id.tv_back:
                    finish();
                    break;
                case R.id.btn_all:
                    type=1;
                    checkColor(btn_all);
                    startTime=0;
                    endTime=0;
                    break;
                case R.id.btn_auto:
                    type=0;
                    checkColor(btn_auto);
                    break;
                case R.id.btn_benyue:
                    type=2;
                    checkColor(btn_benyue);
                    startTime=DatesUtil.getBeginDayOfMonth().getTime();
                    endTime=DatesUtil.getBeginDayOfMonth().getTime();
                    break;
                case R.id.btn_shangyue:
                    type=2;
                    checkColor(btn_shangyue);
                    startTime=DatesUtil.getBeginDayOfLastMonth().getTime();
                    endTime=DatesUtil.getEndDayOfLastMonth().getTime();
                    break;
                case R.id.btn_benzhou:
                    type=2;
                    checkColor(btn_benzhou);
                    startTime=DatesUtil.getBeginDayOfWeek().getTime();
                    endTime=DatesUtil.getEndDayOfWeek().getTime();
                    break;
                case R.id.btn_shangzhou:
                    type=2;
                    checkColor(btn_shangzhou);
                    startTime=DatesUtil.getBeginDayOfLastWeek().getTime();
                    endTime=DatesUtil.getEndDayOfLastWeek().getTime();
                    break;
                case R.id.btn_zuotian:
                    type=2;
                    checkColor(btn_zuotian);
                    startTime=DatesUtil.getBeginDayOfYesterday().getTime();
                    endTime=DatesUtil.getEndDayOfYesterDay().getTime();
                    break;
                case R.id.tv_start:
                    showDialogPick(tv_start);
                    break;
                case R.id.tv_end:
                    showDialogPick(tv_end);
                    break;
            }
        }
    };

    private void checkColor(Button btn) {
        btn_shangzhou.setBackground(getResources().getDrawable(R.drawable.sel_btn));
        btn_benzhou.setBackground(getResources().getDrawable(R.drawable.sel_btn));
        btn_shangyue.setBackground(getResources().getDrawable(R.drawable.sel_btn));
        btn_benyue.setBackground(getResources().getDrawable(R.drawable.sel_btn));
        btn_auto.setBackground(getResources().getDrawable(R.drawable.sel_btn));
        btn_zuotian.setBackground(getResources().getDrawable(R.drawable.sel_btn));
        btn_all.setBackground(getResources().getDrawable(R.drawable.sel_btn));
        btn.setBackground(getResources().getDrawable(R.drawable.sel_btn2));
    }

    private Spinner spinner_simple;
    private EditText et_search;
    private Button btn_auto;
    private Button btn_benzhou;
    private Button btn_shangzhou;
    private Button btn_benyue;
    private Button btn_shangyue;
    private Button btn_zuotian;
    private Button btn_all;
    private TextView tv_start;
    private TextView tv_end;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_search);
//        mPermissionHelper = new PermissionHelper(this, this);
//        mPermissionHelper.requestPermissions();
        initView();
        initListener();
    }
    private void initView(){
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        bt_search = (Button) findViewById(R.id.bt_search);
        bt_search.setVisibility(View.VISIBLE);
        tv_title_name = (TextView) findViewById(R.id.tv_title_name);
        tv_title_name.setText("记录查询");
        tv_title_name.setVisibility(View.VISIBLE);
        et_search= (EditText) findViewById(R.id.et_search);
        spinner_simple= (Spinner) findViewById(R.id.spinner_simple);
        btn_all= (Button) findViewById(R.id.btn_all);
        btn_zuotian= (Button) findViewById(R.id.btn_zuotian);
        btn_benzhou= (Button) findViewById(R.id.btn_benzhou);
        btn_shangzhou= (Button) findViewById(R.id.btn_shangzhou);
        btn_benyue= (Button) findViewById(R.id.btn_benyue);
        btn_shangyue= (Button) findViewById(R.id.btn_shangyue);
        btn_auto= (Button) findViewById(R.id.btn_auto);
        tv_start= (TextView) findViewById(R.id.tv_start);
        tv_end= (TextView) findViewById(R.id.tv_end);
    }
    private void initListener(){
        bt_search.setOnClickListener(listener);
        tv_back.setOnClickListener(listener);
        btn_all.setOnClickListener(listener);
        btn_zuotian.setOnClickListener(listener);
        btn_benzhou.setOnClickListener(listener);
        btn_shangzhou.setOnClickListener(listener);
        btn_benyue.setOnClickListener(listener);
        btn_shangyue.setOnClickListener(listener);
        btn_auto.setOnClickListener(listener);
        tv_start.setOnClickListener(listener);
        tv_end.setOnClickListener(listener);
    }
    //将两个选择时间的dialog放在该函数中
    private void showDialogPick(final TextView timeText) {
        final StringBuffer time = new StringBuffer();
        //获取Calendar对象，用于获取当前时间
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        //实例化TimePickerDialog对象
        final TimePickerDialog timePickerDialog = new TimePickerDialog(RecoSeachActivity.this, new TimePickerDialog.OnTimeSetListener() {
            //选择完时间后会调用该回调函数
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time.append(" "  + hourOfDay + ":" + minute);
                //设置TextView显示最终选择的时间
                timeText.setText(time);
            }
        }, hour, minute, true);
        //实例化DatePickerDialog对象
        DatePickerDialog datePickerDialog = new DatePickerDialog(RecoSeachActivity.this, new DatePickerDialog.OnDateSetListener() {
            //选择完日期后会调用该回调函数
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //因为monthOfYear会比实际月份少一月所以这边要加1
                time.append(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                //选择完日期后弹出选择时间对话框
                timePickerDialog.show();
            }
        }, year, month, day);
        //弹出选择日期对话框
        datePickerDialog.show();
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