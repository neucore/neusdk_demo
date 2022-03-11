package com.neucore.neusdk_demo.neulink.extend.bind.listener;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.extend.ActionResult;


/**
 * 绑定返回的数据结果
 */
public class BindActionResult extends ActionResult {

    @SerializedName("mode")   //bind：绑定，unbind：解绑
    String mode;

    @SerializedName("user")
    String user;

    @SerializedName("role")   //0:普通用户；1:管理员；2:超级管理员
    int role;


    public void toLogcat()
    {

    }

}
