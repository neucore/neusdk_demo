package com.luoye.bzcamera.model;

import android.graphics.PointF;

/**
 * Created by jack_liu on 2019-08-30 10:55.
 * 说明:
 */
public class FocusObj {
    private PointF focusPointF = null;
    private float focusRadius = 20;
    private float contentWidth = 0;
    private float contentHeight = 0;

    public PointF getFocusPointF() {
        return focusPointF;
    }

    public void setFocusPointF(PointF focusPointF) {
        this.focusPointF = focusPointF;
    }

    public float getFocusRadius() {
        return focusRadius;
    }

    public void setFocusRadius(float focusRadius) {
        this.focusRadius = focusRadius;
    }

    public float getContentWidth() {
        return contentWidth;
    }

    public void setContentWidth(float contentWidth) {
        this.contentWidth = contentWidth;
    }

    public float getContentHeight() {
        return contentHeight;
    }

    public void setContentHeight(float contentHeight) {
        this.contentHeight = contentHeight;
    }
}
