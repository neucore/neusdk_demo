package com.neucore.neusdk_demo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neucore.neulink.app.Const;
import com.neucore.neusdk_demo.db.bean.Record;
import com.neucore.neusdk_demo.db.bean.User;
import com.neucore.neusdk_demo.db.RecordDaoUtils;
import com.neucore.neusdk_demo.db.UserDaoUtils;
import com.neucore.neusdk_demo.ecport.ExcelUtils;
import com.neucore.neusdk_demo.ecport.JavaBean;
import com.neucore.neusdk_demo.receiver.PermissionInterface;
import com.neucore.neusdk_demo.utils.FileUtils2;
import com.neucore.neulink.util.LogUtils;
import com.neucore.neusdk_demo.utils.PermissionHelper;

import java.io.File;
import java.net.NetworkInterface;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WriteException;

/**
 * 导入导出
 */
public class ExportActivity extends AppCompatActivity implements PermissionInterface {

    private static String TAG = "ExportActivity";
    private LinearLayout ll_export_record;
    private LinearLayout ll_export_face;
    private LinearLayout ll_export_user;
    private LinearLayout ll_import_face;
    private LinearLayout ll_export_photo;
    private LinearLayout ll_import_user;
    private LinearLayout ll_face;
    private TextView tv_title_name;
    private TextView tv_back;
    private NetworkInterface networkInterface;
    private PermissionHelper mPermissionHelper;
    private UserDaoUtils mUserDaoUtils;
    private RecordDaoUtils mRecordDaoUtils;
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_export_record:
                    alertDialog(1);
                    break;
                case R.id.ll_export_user:
                    alertDialog(0);
                    break;
                case R.id.ll_export_face:
//                    ExportActivity.this.startActivity(new Intent(ExportActivity.this,DataManagerActivity.class));
                    break;
                case R.id.ll_import_user:
                    //导入格式为 .xls .xlsx
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/*");//设置类型
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, 1);
//                    MenuActivity.this.startActivity(new Intent(MenuActivity.this,UserManagerActivity.class));
                    break;
                case R.id.ll_export_photo:
//                    MenuActivity.this.startActivity(new Intent(MenuActivity.this,UserManagerActivity.class));
                    break;
                case R.id.ll_face:
//                    ExportActivity.this.startActivity(new Intent(ExportActivity.this,MainActivity.class));
                    break;
                case R.id.tv_back:
                    finish();
                    break;
            }
        }
    };
    private String mContentType = "application/vnd.ms-excel";//打开excel
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
//        mPermissionHelper = new PermissionHelper(this, this);
//        mPermissionHelper.requestPermissions();
        mUserDaoUtils=new UserDaoUtils(this);
        mRecordDaoUtils=new RecordDaoUtils(this);
        initView();
        initListener();
    }
    private void initView(){
        ll_export_record = (LinearLayout) findViewById(R.id.ll_export_record);
        ll_export_user = (LinearLayout) findViewById(R.id.ll_export_user);
        ll_export_face = (LinearLayout) findViewById(R.id.ll_export_face);
        ll_export_photo = (LinearLayout) findViewById(R.id.ll_export_photo);
        ll_import_user = (LinearLayout) findViewById(R.id.ll_import_user);
        ll_import_face = (LinearLayout) findViewById(R.id.ll_import_face);
        ll_face = (LinearLayout) findViewById(R.id.ll_face);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_title_name = (TextView) findViewById(R.id.tv_title_name);
        tv_title_name.setText("导入导出");
        tv_title_name.setVisibility(View.VISIBLE);
    }
    private void initListener(){
        ll_export_record.setOnClickListener(listener);
        ll_export_user.setOnClickListener(listener);
        ll_export_face.setOnClickListener(listener);
        ll_export_photo.setOnClickListener(listener);
        ll_import_user.setOnClickListener(listener);
        ll_import_face.setOnClickListener(listener);
        tv_back.setOnClickListener(listener);
        ll_face.setOnClickListener(listener);
    }
    /**
     * 导出数据,耗时操作最好放在子线程
     * @param tableName
     * @param datas
     */
    private void exportExcel(String tableName,List<User> datas) {
        if(!new File(Const.fileExport).exists())new File(Const.fileExport).mkdirs();
        ExcelUtils excelUtils = ExcelUtils.getInstance().create(Const.fileExport, tableName);
        List<Object> javaBeans=new ArrayList<>();
        for (int i=0;i<datas.size();i++) {
            User user=datas.get(i);
            JavaBean javaBean = new JavaBean(user.getName(), user.getUserId(), user.getCardId(),user.getOrg(),String.valueOf(user.getTime()),user.getUserType(),user.getCheckType());
            javaBeans.add(javaBean);
        }
        try {
            WritableCellFormat titleFormat = new WritableCellFormat();
            WritableCellFormat dataFormat = new WritableCellFormat();
            titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            titleFormat.setAlignment(Alignment.CENTRE);
            dataFormat.setAlignment(Alignment.RIGHT);
            excelUtils.createSheetSetTitle(tableName, new String[]{"姓名", "工号", "卡号","部门","时间","类型","验证方式"}, titleFormat)
                    .fillData(javaBeans, dataFormat).close();
        } catch (WriteException e) {
            Log.e(TAG,"exportExcel",e);
        }
        MediaScannerConnection.scanFile(this, new String[] { Const.fileExport+tableName+".xls" }, null, null);
        Message msg= new Message();
        msg.what=0;
        msg.obj= Const.fileExport+tableName+".xls";
        handler.sendMessage(msg);
    }
    /**
     * 导出数据,耗时操作最好放在子线程
     * @param tableName
     * @param datas
     */
    private void exportRecordExcel(String tableName,List<Record> datas) {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy.MM.dd HH:mm");
        if(!new File(Const.fileExport).exists())new File(Const.fileExport).mkdirs();
        ExcelUtils excelUtils = ExcelUtils.getInstance().create(Const.fileExport, tableName);
        List<Object> javaBeans=new ArrayList<>();
        for (int i=0;i<datas.size();i++) {
            Record record=datas.get(i);
            String name=record.getName();
            String userId=record.getUserId();
            String cardId=record.getCardId();
            String department=record.getOrg();
            String date=dateFormat.format(new Date(record.getTime()));
            int isUp=record.getIsUp();
            String isUpStr="";
            if(isUp==0){
                isUpStr="未上传";
            }else{
                isUpStr="已上传";
            }
            String upTime=dateFormat.format(new Date(record.getUpTime()));
            String faliUp=record.getFailUp()+"";
            JavaBean javaBean = new JavaBean(name,userId,cardId,department,date,isUpStr,upTime);
            javaBeans.add(javaBean);
        }
        try {
            WritableCellFormat titleFormat = new WritableCellFormat();
            WritableCellFormat dataFormat = new WritableCellFormat();
            titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            titleFormat.setAlignment(Alignment.CENTRE);
            dataFormat.setAlignment(Alignment.RIGHT);
            excelUtils.createSheetSetTitle(tableName, new String[]{"姓名", "工号", "卡号","部门","时间","是否上传","上传时间"}, titleFormat)
                    .fillData(javaBeans, dataFormat).close();
        } catch (WriteException e) {
            Log.e(TAG,"exportRecordExcel",e);
        }
        Message msg= new Message();
        msg.what=0;
        msg.obj= Const.fileExport+tableName+".xls";
        handler.sendMessage(msg);
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    showStudyProgressDialog((String) msg.obj);
//                    Toast.makeText(ExportActivity.this,"导出Excel成功",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    //0记录1用户
    private void alertDialog(final int type) {
        final Dialog dlg = new Dialog(ExportActivity.this);
//        final Dialog dlg = new Dialog.Builder(CreateClassActivity.this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        View view = View.inflate(ExportActivity.this, R.layout.add_class_dialog, null);
        TextView tv_exit = (TextView) view.findViewById(R.id.tv_exit);
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        final EditText et_class_id = (EditText) view.findViewById(R.id.et_class_id);
        String fileName=new SimpleDateFormat("yyyyMMDDHHmmss").format(new Date());
        et_class_id.setText(fileName);
        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String classID=et_class_id.getText().toString().trim();
                if(TextUtils.isEmpty(classID)){
                    Toast.makeText(ExportActivity.this,"请输入文件名",Toast.LENGTH_SHORT).show();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(type==0){
                                List<User> users=mUserDaoUtils.queryAllUser();
                                exportExcel(classID,users);
                            }else if(type==1){
                                List<Record> records=mRecordDaoUtils.queryAllRecord();
                                exportRecordExcel(classID,records);
                            }
                        }
                    }).start();
                    dlg.dismiss();
                }
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        window.setContentView(view);
    }
    private void showStudyProgressDialog(final String path){
        String title="导出完成";
        String msg=path;
        String btn1="取消";
        String btn2="打开文件";
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ExportActivity.this);
        normalDialog.setIcon(R.mipmap.ic_launcher);
        normalDialog.setTitle(title);
        normalDialog.setMessage(msg);
        normalDialog.setPositiveButton(btn1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        normalDialog.setNegativeButton(btn2,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file=new File(path);
                        if(file.exists()) {
                            try {
                                FileUtils2.startActionFile(ExportActivity.this,file,mContentType);
                            }catch (ActivityNotFoundException e){
                                Toast.makeText(ExportActivity.this,"找不到打开文件的APP",Toast.LENGTH_SHORT).show();
                            }
//                            startActivity( OpenFiles.getPdfFileIntent(file));
                            dialog.cancel();
                        }else{
                            Toast.makeText(ExportActivity.this,"找不到文件",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //如果想自定义三个按钮的对话框，可以把下面的方法注释打开
//            normalDialog.setNeutralButton("第三个按钮",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // ...To-do
//                        }
//                    });
        // 显示
        normalDialog.setCancelable(false);
        normalDialog.show();
    }
    //然后进入系统的文件管理,选择文件后
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            LogUtils.e(TAG, "选择的文件Uri = " + data.toString());
            //通过Uri获取真实路径
//            final String excelPath = getRealFilePath(this, data.getData());
            String excelPath = "";
            try {
                excelPath = getPath(this, data.getData());
            } catch (URISyntaxException e) {
                Log.e(TAG,"onActivityResult",e);
            }
            LogUtils.e(TAG, "excelPath = " + excelPath);//    /storage/emulated/0/test.xls
            if (excelPath.contains(".xls") || excelPath.contains(".xlsx")) {
                showSnack("正在加载Excel中...");
                //载入excel
                readExcel(excelPath);
            } else {
                showSnack("此文件不是excel格式");
            }
        }
    }
    //读取Excel表
    private void readExcel(String excelPath) {
        try {
            List<User> user_list = new ArrayList<>();
        Sheet sheet;
        Workbook book;
 
        //Q1G_Path为要读取的excel文件完全路径名
        book = Workbook.getWorkbook(new File(excelPath));
 
        //获得第一个工作表对象(ecxel中sheet的编号从0开始,0,1,2,3,....)
        sheet = book.getSheet(0);
            int rows = sheet.getRows();
        for (int i = 1; i <rows; i++) {
            Cell [] cell = sheet.getRow(i);
                            if(cell.length == 0) continue;
                            User user = new User();
                            //"姓名", "工号", "卡号","部门","时间","类型","验证方式"
            user.setName(sheet.getCell(0, i).getContents());
            user.setUserId(sheet.getCell(1, i).getContents());
            user.setCardId(sheet.getCell(2, i).getContents());
            user.setOrg(sheet.getCell(3, i).getContents());
            user.setTime(Long.valueOf(sheet.getCell(4, i).getContents()));
            user.setUserType(sheet.getCell(5, i).getContents());
            user.setCheckType(sheet.getCell(6, i).getContents());
            user_list.add(user);
        }
        if(mUserDaoUtils.insertMultUser(user_list)){
            showSnack("导入成功");
        }else{
            showSnack("导入失败");
        }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG,"readExcel",ex);
            showSnack("导入失败");
        }
    }
    private void showSnack(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                Log.e(TAG,"getPath",e);
                // Eat it  Or Log it.
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    /**
     * 根据Uri获取真实图片路径
     * <p/>
     * 一个android文件的Uri地址一般如下：
     * content://media/external/images/media/62026
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
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