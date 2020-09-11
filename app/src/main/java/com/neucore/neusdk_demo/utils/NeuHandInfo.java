package com.neucore.neusdk_demo.utils;

import android.graphics.Rect;

import java.util.List;

public class NeuHandInfo {

    private Rect rect;
    private String swipe;
    private String text;
    private float[] pose_node = new float[36];
    private float[] pose_node_score = new float[18];

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public String getSwipe() {
        return swipe;
    }

    public void setSwipe(String swipe) {
        this.swipe = swipe;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float[] getPose_node() {
        return pose_node;
    }

    public void setPose_node(float[] pose_node) {
        this.pose_node = pose_node;
    }

    public float[] getPose_node_score() {
        return pose_node_score;
    }

    public void setPose_node_score(float[] pose_node_score) {
        this.pose_node_score = pose_node_score;
    }
}









