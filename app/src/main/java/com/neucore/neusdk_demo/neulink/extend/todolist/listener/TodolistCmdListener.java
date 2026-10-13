package com.neucore.neusdk_demo.neulink.extend.todolist.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neusdk_demo.neulink.extend.file.request.FileSyncCmd;
import com.neucore.neusdk_demo.neulink.extend.todolist.request.TodolistCmd;


/**
 * 文件处理 数据接收
 */
public class TodolistCmdListener implements ICmdListener<TodolistActionResult, TodolistCmd> {

    @Override
    public TodolistActionResult doAction(NeulinkEvent<TodolistCmd> event) {
        TodolistCmd cmd = event.getSource();
        TodolistActionResult result = new TodolistActionResult();
        result.setCode(NeulinkConst.STATUS_200);
        result.setMessage(NeulinkConst.MESSAGE_SUCCESS);
        return result;
    }

}

