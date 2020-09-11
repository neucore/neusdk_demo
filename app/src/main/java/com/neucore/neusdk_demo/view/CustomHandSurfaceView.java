package com.neucore.neusdk_demo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.neucore.NeuSDK.NeuPose;
import com.neucore.neusdk_demo.DetectActivity;
import com.neucore.neusdk_demo.R;
import com.neucore.neusdk_demo.app.MyApplication;
import com.neucore.neusdk_demo.utility.Constants;
import com.neucore.neusdk_demo.utils.AppInfo;
import com.neucore.neusdk_demo.utils.NeuHandInfo;
import com.neucore.neusdk_demo.utils.SPUtils;
import com.neucore.neusdk_demo.utils.SharePrefConstant;
import com.neucore.neusdk_demo.utils.Util;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

//手势+识别, 人脸关键点, Pose检测, (3个共用一个view)
public class CustomHandSurfaceView extends SurfaceView implements
        SurfaceHolder.Callback{
    // SurfaceHolder
    private SurfaceHolder mSurfaceHolder;

    private Context context;

    private int viewWidth;
    private int viewHeight;

    private boolean startDraw;
    //半径
    private int radius;
    // Path
    private Path mPath = new Path();
    // 画笔
    private Paint mpaint = new Paint();

    private Canvas canvas;
    //滑板背景（保存绘制的图片）
    private Bitmap saveBitmap;
    //图像
    Bitmap bitmap;


    private static int state = 0;
    private Bitmap newBitmap;
    private boolean startBooleanDraw = true;
    private Bitmap bmm;
    private boolean startMove = false;
    private int height;
    private int width;

    public CustomHandSurfaceView(Context context) {
        super(context);
        this.context = context;
        try {
            String type = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.type,"");
            if ("5".equals(type) ) {  //虚拟背景
                saveBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_surface_drawable),AppInfo.getWidthPingMu(),AppInfo.getHeightPingMu(),true);
            }else {  //手势检测 , Pose检测
                saveBitmap = Bitmap.createBitmap(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels, Bitmap.Config.ARGB_8888);
            }
        }catch (Exception e){

        }

        startBooleanDraw = true;
        startMove = false;
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;

        canvas = new Canvas(saveBitmap);
        canvas.setBitmap(saveBitmap);
        initView(); // 初始化

    }

    public CustomHandSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    private void initView() {
        setMeasuredDimension(AppInfo.getWidthPingMu(), AppInfo.getHeightPingMu());
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);//设置背景透明

        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);

    }


    /*
         * 创建
         */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startDraw = true;

        canvas = mSurfaceHolder.lockCanvas();
        //canvas.drawColor(Color.WHITE);
        canvas.setBitmap(saveBitmap);
        mSurfaceHolder.unlockCanvasAndPost(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getWidth();
        viewHeight = getHeight();

    }

    /*
         *
         */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    /*
     * 销毁
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        startDraw = false;
    }

    public void setUpStartDraw(){

        startBooleanDraw = false;

    };

    public void setUpStartDrawTwo(){

        startBooleanDraw = true;

    };


    public void draws() {
        if (canvas != null){
            canvas.drawColor(0x00FFFFFF);    //设置画布背景色
        }
        canvas = mSurfaceHolder.lockCanvas();

        mpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        if (mpaint != null){
            canvas.drawPaint(mpaint);
        }
        mpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

//        canvas.setBitmap(saveBitmap);
        Rect rectF = new Rect(0, 0, getWidth(), getHeight());   //w和h分别是屏幕的宽和高，也就是你想让图片显示的宽和高
        canvas.drawBitmap(saveBitmap, null, rectF, null);
        // 抗锯齿
        mpaint.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mpaint.setDither(true);

        mpaint.setColor(getResources().getColor(R.color.red_fa));
        mpaint.setStyle(Paint.Style.STROKE);
        mpaint.setStrokeWidth(dip2px(context, 1));
        canvas.drawRect(0, 0, 0, 0, mpaint);

        mSurfaceHolder.unlockCanvasAndPost(canvas);

    }

    public void drawsTwo(List<NeuHandInfo> rectList) {
        canvas = mSurfaceHolder.lockCanvas();

        if (canvas != null){
            canvas.drawColor(0x00FFFFFF);    //设置画布背景色
        }

        mpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        if (mpaint != null){
            if (canvas != null){
                canvas.drawPaint(mpaint);
            }
        }
        mpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        // 抗锯齿
        mpaint.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mpaint.setDither(true);
        mpaint.setColor(getResources().getColor(R.color.red_fa));
        String type = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.type,"");
        if ("3".equals(type) ){  //手势检测
            if (canvas != null){
                for (int a = 0; a < rectList.size(); a++){
                    mpaint.setStyle(Paint.Style.FILL);
                    mpaint.setStrokeWidth(dip2px(context, 2));
                    mpaint.setTextSize(50);
                    canvas.drawText(rectList.get(a).getText(),(rectList.get(a).getRect().left * 2) /3 + rectList.get(a).getRect().right/3, rectList.get(a).getRect().top - 20,mpaint);
                    canvas.drawText(rectList.get(a).getSwipe(),(rectList.get(a).getRect().left * 2) /3 + rectList.get(a).getRect().right/3, rectList.get(a).getRect().bottom + 70,mpaint);

                    mpaint.setStyle(Paint.Style.STROKE);
                    mpaint.setStrokeWidth(dip2px(context, 3));
                    canvas.drawRect(rectList.get(a).getRect().left, rectList.get(a).getRect().top, rectList.get(a).getRect().right,rectList.get(a).getRect().bottom, mpaint);

                }
            }
        }else if ("4".equals(type)){ //手势
            if (canvas != null){
                for (int a = 0; a < rectList.size(); a++){
                    float[] pose_node = rectList.get(a).getPose_node();
                    float[] pose_node_score = rectList.get(a).getPose_node_score();

                    for(int j = 0; j < 18; ++j){
                        mpaint.setStrokeWidth(dip2px(context, 3));
                        mpaint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(Util.widthPointTrans(pose_node[j * 2]) , Util.heightPointTrans(pose_node[j * 2 + 1]),5,mpaint);

                        // draw line of Nose and Neck
                        for (int i = NeuPose.Nose; i <= NeuPose.Neck-1; i++) {
                            draw_line(pose_node[i*2], pose_node[i*2 + 1], pose_node[(i+1)*2], pose_node[(i+1)*2 + 1],
                                    pose_node_score[i], pose_node_score[i+1]);
                        }

                        for (int i = NeuPose.RShoulder; i <= NeuPose.RWrist-1; i++) {
                            draw_line(pose_node[i*2], pose_node[i*2 + 1], pose_node[(i+1)*2], pose_node[(i+1)*2 + 1],
                                    pose_node_score[i], pose_node_score[i+1]);
                        }

                        for (int i = NeuPose.LShoulder; i <= NeuPose.LWrist-1; i++) {
                            draw_line(pose_node[i*2], pose_node[i*2 + 1], pose_node[(i+1)*2], pose_node[(i+1)*2 + 1],
                                    pose_node_score[i], pose_node_score[i+1]);
                        }

                        for (int i = NeuPose.RHip; i <= NeuPose.RAnkle-1; i++) {
                            draw_line(pose_node[i*2], pose_node[i*2 + 1], pose_node[(i+1)*2], pose_node[(i+1)*2 + 1],
                                    pose_node_score[i], pose_node_score[i+1]);
                        }

                        for (int i = NeuPose.LHip; i <= NeuPose.LAnkle-1; i++) {
                            draw_line(pose_node[i*2], pose_node[i*2 + 1], pose_node[(i+1)*2], pose_node[(i+1)*2 + 1],
                                    pose_node_score[i], pose_node_score[i+1]);
                        }

                        draw_line(pose_node[NeuPose.LEye*2], pose_node[NeuPose.LEye*2 + 1], pose_node[NeuPose.LEar*2], pose_node[NeuPose.LEar*2 + 1],
                                pose_node_score[NeuPose.LEye], pose_node_score[NeuPose.LEar]);
                        draw_line(pose_node[NeuPose.REye*2], pose_node[NeuPose.REye*2 + 1], pose_node[NeuPose.REar*2], pose_node[NeuPose.REar*2 + 1],
                                pose_node_score[NeuPose.REye], pose_node_score[NeuPose.REar]);
                        draw_line(pose_node[NeuPose.LEye*2], pose_node[NeuPose.LEye*2 + 1], pose_node[NeuPose.Nose*2], pose_node[NeuPose.Nose*2 + 1],
                                pose_node_score[NeuPose.LEye], pose_node_score[NeuPose.Nose]);
                        draw_line(pose_node[NeuPose.REye*2], pose_node[NeuPose.REye*2 + 1], pose_node[NeuPose.Nose*2], pose_node[NeuPose.Nose*2 + 1],
                                pose_node_score[NeuPose.REye], pose_node_score[NeuPose.Nose]);

                        draw_line(pose_node[NeuPose.Neck*2], pose_node[NeuPose.Neck*2 + 1], pose_node[NeuPose.RShoulder*2], pose_node[NeuPose.RShoulder*2 + 1],
                                pose_node_score[NeuPose.Neck], pose_node_score[NeuPose.RShoulder]);
                        draw_line(pose_node[NeuPose.Neck*2], pose_node[NeuPose.Neck*2 + 1], pose_node[NeuPose.LShoulder*2], pose_node[NeuPose.LShoulder*2 + 1],
                                pose_node_score[NeuPose.Neck], pose_node_score[NeuPose.LShoulder]);
                        draw_line(pose_node[NeuPose.RShoulder*2], pose_node[NeuPose.RShoulder*2 + 1], pose_node[NeuPose.RHip*2], pose_node[NeuPose.RHip*2 + 1],
                                pose_node_score[NeuPose.RShoulder], pose_node_score[NeuPose.RHip]);
                        draw_line(pose_node[NeuPose.LShoulder*2], pose_node[NeuPose.LShoulder*2 + 1], pose_node[NeuPose.LHip*2], pose_node[NeuPose.LHip*2 + 1],
                                pose_node_score[NeuPose.LShoulder], pose_node_score[NeuPose.LHip]);
                    }

                }
            }

        }else if ("5".equals(type) ){  //虚拟背景
            if (canvas != null){
                Rect rect = new Rect(0,0, AppInfo.getWidthPingMu(), AppInfo.getHeightPingMu());
                Rect rectF = new Rect(0,0, AppInfo.getWidthPingMu(), AppInfo.getHeightPingMu());
                for (int a = 0; a < rectList.size(); a++){
                    mpaint.setStyle(Paint.Style.STROKE);
                    canvas.drawBitmap(saveBitmap,rect,rectF,mpaint);
                    //canvas.drawBitmap(int[] colors,0,0,0,0,saveBitmap.getWidth(),saveBitmap.getHeight(),false,mpaint);

                    mpaint.setStyle(Paint.Style.FILL);
                    mpaint.setStrokeWidth(dip2px(context, 2));
                    mpaint.setTextSize(50);
                    canvas.drawText(rectList.get(a).getText(),(rectList.get(a).getRect().left * 2) /3 + rectList.get(a).getRect().right/3, rectList.get(a).getRect().top - 20,mpaint);
                    canvas.drawText(rectList.get(a).getSwipe(),(rectList.get(a).getRect().left * 2) /3 + rectList.get(a).getRect().right/3, rectList.get(a).getRect().bottom + 70,mpaint);

                    canvas.drawColor(0x00FFFFFF);    //设置画布背景色
                    mpaint.setStyle(Paint.Style.FILL);
                    mpaint.setStrokeWidth(dip2px(context, 3));
                    canvas.drawRect(rectList.get(a).getRect().left, rectList.get(a).getRect().top, rectList.get(a).getRect().right,rectList.get(a).getRect().bottom, mpaint);

                }
            }
        }

        if (canvas != null){
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }


    }

    private void draw_line(float p1_x, float p1_y, float p2_x, float p2_y, float p1_score, float p2_score) {
        if (p1_score == 0.0f || p2_score == 0.0f) {
            return;
        } else {
            canvas.drawLine(Util.widthPointTrans(p1_x),Util.heightPointTrans(p1_y),
                    Util.widthPointTrans(p2_x),Util.heightPointTrans(p2_y),mpaint);
        }
    }


    /**
     * dip转px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


}
