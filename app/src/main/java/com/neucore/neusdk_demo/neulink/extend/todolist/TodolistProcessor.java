package com.neucore.neusdk_demo.neulink.extend.todolist;

import static cn.hutool.poi.excel.sax.AttributeName.s;

import android.content.Context;

import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neusdk_demo.neulink.extend.file.listener.FileActionResult;
import com.neucore.neusdk_demo.neulink.extend.file.request.FileSyncCmd;
import com.neucore.neusdk_demo.neulink.extend.file.response.FileSyncCmdRes;
import com.neucore.neusdk_demo.neulink.extend.todolist.listener.TodolistActionResult;
import com.neucore.neusdk_demo.neulink.extend.todolist.request.TodolistCmd;
import com.neucore.neusdk_demo.neulink.extend.todolist.response.TodolistCmdRes;

public class TodolistProcessor extends GProcessor<TodolistCmd, TodolistCmdRes, TodolistActionResult> {
    public TodolistProcessor() {
        this(ContextHolder.getInstance().getContext());
    }

    public TodolistProcessor(Context context) {
        super(context);
    }
    @Override
    protected TodolistCmd parser(String payload) {
        return JSonUtils.toObject(payload, TodolistCmd.class);
    }

    @Override
    protected TodolistCmdRes responseWrapper(TodolistCmd todolistCmd, TodolistActionResult actionResult) {
        TodolistCmdRes res = new TodolistCmdRes();
        res.setCmdStr(todolistCmd.getCmdStr());
        res.setCode(actionResult.getCode());
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setMsg(actionResult.getMessage());
        return res;
    }

    @Override
    protected TodolistCmdRes fail(TodolistCmd todolistCmd, String error) {
        TodolistCmdRes res = new TodolistCmdRes();
        res.setCmdStr(todolistCmd.getCmdStr());
        res.setCode(500);
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setData(error);
        res.setMsg("失败");
        return res;
    }

    @Override
    protected TodolistCmdRes fail(TodolistCmd todolistCmd, int code, String error) {
        TodolistCmdRes res = new TodolistCmdRes();
        res.setCmdStr(todolistCmd.getCmdStr());
        res.setCode(code);
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setData(error);
        res.setMsg("失败");
        return res;
    }
}
