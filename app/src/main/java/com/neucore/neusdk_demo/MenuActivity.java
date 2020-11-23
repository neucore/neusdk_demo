package com.neucore.neusdk_demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.neucore.NeuSDK.NeuHand;
import com.neucore.neulink.app.Const;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neusdk_demo.app.MyApplication;
import com.neucore.neusdk_demo.neucore.FaceProcessing;
import com.neucore.neusdk_demo.neucore.NeuFaceFactory;
import com.neucore.neusdk_demo.neucore.NeuHandFactory;
import com.neucore.neusdk_demo.neucore.NeuPoseFactory;
import com.neucore.neusdk_demo.neucore.NeuSegmentFactory;
import com.neucore.neusdk_demo.utility.Constants;
import com.neucore.neusdk_demo.utils.HelpUtil;
import com.neucore.neusdk_demo.utils.PermissionHelper;
import com.neucore.neulink.util.RequestContext;
import com.neucore.neusdk_demo.receiver.PermissionInterface;
import com.neucore.neusdk_demo.utils.SPUtils;
import com.neucore.neusdk_demo.utils.SharePrefConstant;
import com.neucore.neusdk_demo.utils.Util;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 菜单
 */
public class MenuActivity extends AppCompatActivity implements PermissionInterface {

    @BindView(R.id.ll_user)
    LinearLayout ll_user;
    @BindView(R.id.ll_data)
    LinearLayout ll_data;
    @BindView(R.id.ll_search)
    LinearLayout ll_search;
    @BindView(R.id.ll_export)
    LinearLayout ll_export;
    @BindView(R.id.ll_system)
    LinearLayout ll_system;
    @BindView(R.id.ll_face)
    LinearLayout ll_face;
    @BindView(R.id.tv_back)
    TextView tv_back;

    @BindView(R.id.ll_one_camera_life)
    LinearLayout ll_one_camera_life;
    @BindView(R.id.ll_one_camera_no_life)
    LinearLayout ll_one_camera_no_life;
    @BindView(R.id.ll_two_camera_life)
    LinearLayout ll_two_camera_life;
    @BindView(R.id.ll_gesture_discern)
    LinearLayout ll_gesture_discern;
    @BindView(R.id.ll_pose_testing)
    LinearLayout ll_pose_testing;
    @BindView(R.id.ll_background_fade)
    LinearLayout ll_background_fade;
    @BindView(R.id.ll_pedestrian_detection)
    LinearLayout ll_pedestrian_detection;
    @BindView(R.id.ll_face_key_points)
    LinearLayout ll_face_key_points;


    private TextView tv_title_name;
    String TAG = "NEUCORE MenuActivity";
    private PermissionHelper mPermissionHelper;
    private static MenuActivity instance;

    public static final int START_ACTIVITY = 11;

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case START_ACTIVITY: //人脸识别
                    if ( Math.abs(System.currentTimeMillis() - time) > 5000){
                        time = System.currentTimeMillis();
                        startActivity(new Intent(MenuActivity.this,DetectActivity.class));
                    }else {
                        Toast.makeText(MenuActivity.this,"请不要重复点击",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    public static MenuActivity getInstance(){
        return instance;
    }

    private long time;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance=this;
        //设置全屏
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Util.hideBottomUIMenu(this);

        //ExternalStoragePermissions.verifyStoragePermissions(this);
        mPermissionHelper = new PermissionHelper(this,this);

        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        initView();

        //跳转到主界面
        if (!EasyPermissions.hasPermissions(MenuActivity.this, PERMISSIONS)) {
            EasyPermissions.requestPermissions(MenuActivity.this, "请允许权限，否则无法使用", 123, PERMISSIONS);
        }

        Toast.makeText(this, "哈哈，我成功启动了！", Toast.LENGTH_LONG).show();

        initViewNb();
    }

    private void initViewNb() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!new File(Const.filePath).exists()){
                    new File(Const.filePath).mkdirs();
                }
                HelpUtil.createFileNB();
                //初始化拷贝nb文件
                HelpUtil.copyAssetResource2File(MyApplication.getContext());
                OpenCVLoader.initDebug();
                NeuFaceFactory.getInstance().create();
                NeuHandFactory.getInstance().create();
                NeuPoseFactory.getInstance().create();
                NeuSegmentFactory.getInstance().create();

                FaceProcessing.getInstance(MyApplication.getContext());

                Util.clearAllCache(MyApplication.getContext());
            }
        },1000);
    }


    private void initView(){
        tv_back.setVisibility(View.VISIBLE);
        tv_title_name = (TextView) findViewById(R.id.tv_title_name);
        tv_title_name.setText("菜单");
        tv_title_name.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.ll_user, R.id.ll_search, R.id.ll_data, R.id.ll_system, R.id.ll_export, R.id.ll_face, R.id.tv_back,
            R.id.ll_one_camera_life, R.id.ll_one_camera_no_life, R.id.ll_two_camera_life, R.id.ll_gesture_discern,
            R.id.ll_pose_testing, R.id.ll_background_fade, R.id.ll_pedestrian_detection, R.id.ll_face_key_points})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_user:
                MenuActivity.this.startActivity(new Intent(MenuActivity.this,UserManagerActivity.class));
                break;
            case R.id.ll_search:
                MenuActivity.this.startActivity(new Intent(MenuActivity.this,RecoSeachActivity.class));
                break;
            case R.id.ll_data:
                MenuActivity.this.startActivity(new Intent(MenuActivity.this,DataManagerActivity.class));
                break;
            case R.id.ll_system:
                MenuActivity.this.startActivity(new Intent(MenuActivity.this,DeviceInfoActivity.class));
                break;
            case R.id.ll_export:
                MenuActivity.this.startActivity(new Intent(MenuActivity.this,ExportActivity.class));
                break;
            case R.id.ll_face:  //人脸识别
                mHandler.sendEmptyMessage(START_ACTIVITY);
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.ll_one_camera_life:  //单目活体
                SPUtils.put(MyApplication.getContext(), SharePrefConstant.type,"0");
                String equip_type0 = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.EQUIPMENT_TYPE, Constants.TYPE_64010);
                if (Constants.TYPE_64010.equals(equip_type0)){
                    //64010竖屏
                    //竖屏64010板子专属
//                    mHandler.sendEmptyMessage(START_ACTIVITY);
                    startActivity(new Intent(MenuActivity.this,Camera2PortraitActivity.class));
                }else if (Constants.TYPE_6421.equals(equip_type0)){
                    //6421横屏
                    //横屏6421板子专属
                    startActivity(new Intent(MenuActivity.this,Camera2Activity.class));
                }
                break;
            case R.id.ll_one_camera_no_life: //单目非活体
                SPUtils.put(MyApplication.getContext(), SharePrefConstant.type,"1");
                String equip_type1 = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.EQUIPMENT_TYPE, Constants.TYPE_64010);
                if (Constants.TYPE_64010.equals(equip_type1)){
                    //64010竖屏
                    //竖屏64010板子专属
//                    mHandler.sendEmptyMessage(START_ACTIVITY);
                    startActivity(new Intent(MenuActivity.this,Camera2PortraitActivity.class));
                }else if (Constants.TYPE_6421.equals(equip_type1)){
                    //6421横屏
                    //横屏6421板子专属
                    startActivity(new Intent(MenuActivity.this,Camera2Activity.class));
                }
                break;
            case R.id.ll_two_camera_life:   //双目活体
                SPUtils.put(MyApplication.getContext(), SharePrefConstant.type,"2");
                String equip_type2 = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.EQUIPMENT_TYPE, Constants.TYPE_64010);
                if (Constants.TYPE_64010.equals(equip_type2)){
                    //64010竖屏
                    //竖屏64010板子专属
                    mHandler.sendEmptyMessage(START_ACTIVITY);
                }else if (Constants.TYPE_6421.equals(equip_type2)){
                    //6421横屏
                    //横屏6421板子专属
                    Toast.makeText(MyApplication.getContext(),"该板子不支持双摄像头",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_gesture_discern:   //手势检测+识别
                SPUtils.put(MyApplication.getContext(), SharePrefConstant.type,"3");

                String equip_type3 = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.EQUIPMENT_TYPE, Constants.TYPE_64010);
                if (Constants.TYPE_64010.equals(equip_type3)){
                    //64010竖屏
                    //竖屏64010板子专属
//                    mHandler.sendEmptyMessage(START_ACTIVITY);
                    startActivity(new Intent(MenuActivity.this,Camera2PortraitActivity.class));
                }else if (Constants.TYPE_6421.equals(equip_type3)){
                    //6421横屏
                    //横屏6421板子专属
                    startActivity(new Intent(MenuActivity.this,Camera2Activity.class));
                }
                break;
            case R.id.ll_pose_testing:  //Pose检测
                SPUtils.put(MyApplication.getContext(), SharePrefConstant.type,"4");

                String equip_type4 = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.EQUIPMENT_TYPE, Constants.TYPE_64010);
                if (Constants.TYPE_64010.equals(equip_type4)){
                    //64010竖屏
                    //竖屏64010板子专属
//                    mHandler.sendEmptyMessage(START_ACTIVITY);
                    startActivity(new Intent(MenuActivity.this,Camera2PortraitActivity.class));
                }else if (Constants.TYPE_6421.equals(equip_type4)){
                    //6421横屏
                    //横屏6421板子专属
                    startActivity(new Intent(MenuActivity.this,Camera2Activity.class));
                }
                break;
            case R.id.ll_background_fade:   //虚拟背景
                SPUtils.put(MyApplication.getContext(), SharePrefConstant.type,"5");
                mHandler.sendEmptyMessage(START_ACTIVITY);
                break;
            case R.id.ll_pedestrian_detection:  //行人检测
                SPUtils.put(MyApplication.getContext(), SharePrefConstant.type,"6");
                mHandler.sendEmptyMessage(START_ACTIVITY);
                break;
            case R.id.ll_face_key_points:   //人脸关键点
                SPUtils.put(MyApplication.getContext(), SharePrefConstant.type,"7");

                String equip_type7 = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.EQUIPMENT_TYPE, Constants.TYPE_64010);
                if (Constants.TYPE_64010.equals(equip_type7)){
                    //64010竖屏
                    //竖屏64010板子专属
//                    mHandler.sendEmptyMessage(START_ACTIVITY);
                    startActivity(new Intent(MenuActivity.this,Camera2PortraitActivity.class));
                }else if (Constants.TYPE_6421.equals(equip_type7)){
                    //6421横屏
                    //横屏6421板子专属
                    startActivity(new Intent(MenuActivity.this,Camera2Activity.class));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // 退出时，请求杀死进程
            System.exit(0);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
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
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    //openCV4Android 需要加载用到
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
//                    mOpenCvCameraView.enableView();
//                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private String clientId = null;

    public void installAPK(final String mUpdateUrl,final String newVersion){
        File appFile = null;
        try {
            appFile = NeuHttpHelper.dld2File(getApplicationContext(),RequestContext.getId(),mUpdateUrl);
            //AppUpdateUtils.installApp(MenuActivity.this,appFile);
        }
        catch (IOException ex){
            throw new RuntimeException(ex);
        }
        finally {
            if(appFile!=null){
                appFile.delete();
            }
        }
    }
}