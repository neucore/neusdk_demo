package com.neucore.neusdk_demo.neulink.extend.file.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neusdk_demo.neulink.extend.file.request.FileSyncCmd;


/**
 * 文件处理 数据接收
 */
public class FileCmdListener implements ICmdListener<FileActionResult, FileSyncCmd> {

    @Override
    public FileActionResult doAction(NeulinkEvent<FileSyncCmd> event) {
        FileSyncCmd cmd = event.getSource();
        toUserUpload(cmd);
        FileActionResult result = new FileActionResult();
        result.setCode(NeulinkConst.STATUS_200);
        result.setMessage(NeulinkConst.MESSAGE_SUCCESS);
        return result;
    }

    public void toUserUpload(FileSyncCmd cmd) {

    }

}

