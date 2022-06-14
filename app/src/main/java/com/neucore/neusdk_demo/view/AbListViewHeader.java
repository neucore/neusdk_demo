/*
 * Copyright (C) 2013 www.418log.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.neucore.neusdk_demo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.neucore.neusdk_demo.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

// TODO: Auto-generated Javadoc

/**
 * The Class AbListViewHeader.
 */
public class AbListViewHeader extends LinearLayout {
	private static String TAG = "AbListViewHeader";
	/** The mContext. */
	private Context mContext;
	
	/** The header view. */
	private LinearLayout headerView;
	
	/** The arrow image view. */
	private ImageView arrowImageView;
	
	/** The header progress bar. */
	private ProgressBar headerProgressBar;
	
	/** The arrow image. */
	private Bitmap arrowImage = null;
	
	/** The tips textview. */
	private TextView tipsTextview;
	
	/** The header time view. */
	private TextView headerTimeView;
	
	/** The m state. */
	private int mState = -1;

	/** The m rotate up anim. */
	private Animation mRotateUpAnim;
	
	/** The m rotate down anim. */
	private Animation mRotateDownAnim;
	
	/** The rotate anim duration. */
	private final int ROTATE_ANIM_DURATION = 180;
	
	/** The Constant STATE_NORMAL. */
	public final static int STATE_NORMAL = 0;
	
	/** The Constant STATE_READY. */
	public final static int STATE_READY = 1;
	
	/** The Constant STATE_REFRESHING. */
	public final static int STATE_REFRESHING = 2;
	
	/** 保存上一次的刷新时间. */
	private String lastRefreshTime = null;
	
	/** The head content height. */
	private int headerHeight;

	/**
	 * Instantiates a new ab list view header.
	 *
	 * @param context the context
	 */
	public AbListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * Instantiates a new ab list view header.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public AbListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	/**
	 * Inits the view.
	 *
	 * @param context the context
	 */
	private void initView(Context context) {
		
		mContext  = context;
		
		//顶部刷新栏整体内容
		headerView = new LinearLayout(context);
		headerView.setOrientation(LinearLayout.HORIZONTAL);
		//setBackgroundColor(Color.rgb(225, 225,225));
		headerView.setGravity(Gravity.CENTER);
		headerView.setPadding(0, 5, 0, 5);
		
		//显示箭头与进度
		FrameLayout headImage =  new FrameLayout(context);
		arrowImageView = new ImageView(context);
		//从包里获取的箭头图片
		//arrowImage = getBitmapFormSrc("image/arrow.png");
		 arrowImage = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow);
		arrowImageView.setImageBitmap(arrowImage);
		
		//style="?android:attr/progressBarStyleSmall" 默认的样式
		headerProgressBar = new ProgressBar(context,null,android.R.attr.progressBarStyle);
		headerProgressBar.setVisibility(View.GONE);
		
		LayoutParams layoutParamsWW = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParamsWW.gravity = Gravity.CENTER;
		layoutParamsWW.width = 50;
		layoutParamsWW.height = 50;
		headImage.addView(arrowImageView,layoutParamsWW);
		headImage.addView(headerProgressBar,layoutParamsWW);
		
		//顶部刷新栏文本内容
		LinearLayout headTextLayout  = new LinearLayout(context);
		tipsTextview = new TextView(context);
		headerTimeView = new TextView(context);
		headTextLayout.setOrientation(LinearLayout.VERTICAL);
		headTextLayout.setGravity(Gravity.CENTER_VERTICAL);
		headTextLayout.setPadding(12,0,0,0);
		LayoutParams layoutParamsWW2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		headTextLayout.addView(tipsTextview,layoutParamsWW2);
		headTextLayout.addView(headerTimeView,layoutParamsWW2);
		tipsTextview.setTextColor(Color.rgb(107, 107, 107));
		headerTimeView.setTextColor(Color.rgb(107, 107, 107));
		tipsTextview.setTextSize(15);
		headerTimeView.setTextSize(14);
		
		LayoutParams layoutParamsWW3 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParamsWW3.gravity = Gravity.CENTER;
		layoutParamsWW3.bottomMargin = 5;
		layoutParamsWW3.topMargin = 5;
		
		LinearLayout headerLayout = new LinearLayout(context);
		headerLayout.setOrientation(LinearLayout.HORIZONTAL);
		headerLayout.setGravity(Gravity.CENTER);
		
		headerLayout.addView(headImage,layoutParamsWW3);
		headerLayout.addView(headTextLayout,layoutParamsWW3);
		
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.BOTTOM;
		//添加大布局
		headerView.addView(headerLayout,lp);
		
		this.addView(headerView,lp);
		//获取View的高度
		measureView(this);
		headerHeight = this.getMeasuredHeight();
		//向上偏移隐藏起来
		headerView.setPadding(0, -1 * headerHeight, 0, 0);
		
		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
		
		setState(STATE_NORMAL);
	}
	
	
	/**
	 * 测量这个view，最后通过getMeasuredWidth()获取宽度和高度.
	 *
	 * @param v 要测量的view
	 * @return 测量过的view
	 */
	public static void measureView(View v){
		if(v == null){
			return;
		}
		int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
	    int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
	    v.measure(w, h);
	}
	
	
	public static Bitmap getBitmapFormSrc(String src){
		Bitmap bit = null;
		try {
			bit = BitmapFactory.decodeStream(AbListViewHeader.class.getResourceAsStream(src));
	    } catch (Exception e) {
		}
		return bit;
	}
	/**
	 * 描述：获取表示当前日期时间的字符串.
	 *
	 * @param format  格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return String String类型的当前日期时间
	 */
	public static String getCurrentDate(String format) {
		String curDateTime = null;
		try {
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			Calendar c = new GregorianCalendar();
			curDateTime = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			Log.e(TAG,e.getMessage(),e);
		}
		return curDateTime;

	}
	/** 时分秒. */
	public static String dateFormatHMS = "HH:mm:ss";
	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public void setState(int state) {
		if (state == mState) return ;
		
		if (state == STATE_REFRESHING) {	
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.INVISIBLE);
			headerProgressBar.setVisibility(View.VISIBLE);
		} else {	
			arrowImageView.setVisibility(View.VISIBLE);
			headerProgressBar.setVisibility(View.INVISIBLE);
		}
		
		switch(state){
			case STATE_NORMAL:
				if (mState == STATE_READY) {
					arrowImageView.startAnimation(mRotateDownAnim);
				}
				if (mState == STATE_REFRESHING) {
					arrowImageView.clearAnimation();
				}
				tipsTextview.setText("下拉刷新");
				
				if(lastRefreshTime==null){
					lastRefreshTime = getCurrentDate(dateFormatHMS);
					headerTimeView.setText("刷新时间：" + lastRefreshTime);
				}else{
					headerTimeView.setText("上次刷新时间：" + lastRefreshTime);
				}
				
				break;
			case STATE_READY:
				if (mState != STATE_READY) {
					arrowImageView.clearAnimation();
					arrowImageView.startAnimation(mRotateUpAnim);
					tipsTextview.setText("松开刷新");
					headerTimeView.setText("上次刷新时间：" + lastRefreshTime);
					lastRefreshTime = getCurrentDate(dateFormatHMS);
					
				}
				break;
			case STATE_REFRESHING:
				tipsTextview.setText("正在刷新...");
				headerTimeView.setText("本次刷新时间：" + lastRefreshTime);
				break;
				default:
			}
		
		mState = state;
	}
	
	/**
	 * Sets the visiable height.
	 *
	 * @param height the new visiable height
	 */
	public void setVisiableHeight(int height) {
		if (height < 0) height = 0;
		LayoutParams lp = (LayoutParams) headerView.getLayoutParams();
		lp.height = height;
		headerView.setLayoutParams(lp);
	}

	/**
	 * Gets the visiable height.
	 *
	 * @return the visiable height
	 */
	public int getVisiableHeight() {
		LayoutParams lp = (LayoutParams)headerView.getLayoutParams();
		return lp.height;
	}

	/**
	 * 描述：获取HeaderView.
	 *
	 * @return the header view
	 */
	public LinearLayout getHeaderView() {
		return headerView;
	}
	
	/**
	 * set last refresh time.
	 *
	 * @param time the new refresh time
	 */
	public void setRefreshTime(String time) {
		headerTimeView.setText(time);
	}

	/**
	 * Gets the header height.
	 *
	 * @return the header height
	 */
	public int getHeaderHeight() {
		return headerHeight;
	}
	
	/**
	 * 
	 * 描述：设置字体颜色
	 * @param color
	 * @throws 
	 */
	public void setTextColor(int color){
		tipsTextview.setTextColor(color);
		headerTimeView.setTextColor(color);
	}
	
	/**
	 * 
	 * 描述：设置背景颜色
	 * @param color
	 * @throws 
	 */
	public void setBackgroundColor(int color){
		headerView.setBackgroundColor(color);
	}

	/**
	 * 
	 * 描述：获取Header ProgressBar，用于设置自定义样式
	 * @return
	 * @throws 
	 */
	public ProgressBar getHeaderProgressBar() {
		return headerProgressBar;
	}

	/**
	 * 
	 * 描述：设置Header ProgressBar样式
	 * @return
	 * @throws 
	 */
	public void setHeaderProgressBarDrawable(Drawable indeterminateDrawable) {
		headerProgressBar.setIndeterminateDrawable(indeterminateDrawable);
	}
	

}
