package com.neucore.neusdk_demo.view;

import android.content.Context;
import android.graphics.Bitmap;
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

import com.neucore.neusdk_demo.R;
import com.neucore.neusdk_demo.app.MyApplication;
import com.neucore.neusdk_demo.utils.SPUtils;
import com.neucore.neusdk_demo.utils.SharePrefConstant;

import java.util.List;


public class CustomSurfaceView extends SurfaceView implements
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

    public CustomSurfaceView(Context context) {
        super(context);
        this.context = context;
        try {
            saveBitmap = Bitmap.createBitmap(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels, Bitmap.Config.ARGB_8888);
        }catch (Exception e){

        }
        //saveBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.new1111),720,1000,true);

        startBooleanDraw = true;
        startMove = false;
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;

        canvas = new Canvas(saveBitmap);
        canvas.setBitmap(saveBitmap);
        initView(); // 初始化

    }

    public CustomSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    private void initView() {
        setMeasuredDimension(720, 1000);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);//设置背景透明

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
        mpaint.setAntiAlias(true);

        mpaint.setColor(getResources().getColor(R.color.red_fa));
        mpaint.setStyle(Paint.Style.STROKE);
        mpaint.setStrokeWidth(dip2px(context, 1));
        canvas.drawRect(0, 0, 0, 0, mpaint);

        mSurfaceHolder.unlockCanvasAndPost(canvas);

    }

    public void drawsTwo(List<Rect> rectList) {
        String type = (String) SPUtils.get(MyApplication.getContext(), SharePrefConstant.type,"");
        if ("2".equals(type)){  //双目的
            canvas = mSurfaceHolder.lockCanvas();
        }else {
            canvas = mSurfaceHolder.lockCanvas();
        }

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
        mpaint.setAntiAlias(true);
        mpaint.setColor(getResources().getColor(R.color.red_fa));
        mpaint.setStyle(Paint.Style.STROKE);
        mpaint.setStrokeWidth(dip2px(context, 3));
        if (canvas != null){
            for (int a = 0; a < rectList.size(); a++){
                canvas.drawRect(rectList.get(a).left, rectList.get(a).top, rectList.get(a).right,rectList.get(a).bottom, mpaint);
            }
        }

        if (canvas != null){
            if (!"2".equals(type)) {  //不是双目的
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }else {
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
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
