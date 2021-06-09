package com.neucore.neulink;

public interface IFaceblibChangeListener {
    /**
     * 数据变化时调用改方法，改方法一般会调用load方法
     */
    void onChanged();
}
