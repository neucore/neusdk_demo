package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.bak.BackupItem;
import com.neucore.neulink.cfg.CfgCmd;
import com.neucore.neulink.cfg.CfgItem;
import com.neucore.neulink.cfg.ConfigContext;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.QueryResult;
import com.neucore.neulink.extend.Result;
import com.neucore.neulink.extend.StorageFactory;
import com.neucore.neulink.impl.NeuLinkConstant;
import com.neucore.neulink.recv.RecoverCmd;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.MD5Utils;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neulink.util.RequestContext;
import com.neucore.neusdk_demo.db.DaoManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CfgActionListener implements ICmdListener<Result>, NeuLinkConstant {
    @Override
    public Result doAction(NeulinkEvent event) {
        CfgCmd cmd = (CfgCmd) event.getSource();
        CfgItem[] items = cmd.getData();
        int size = items==null?0:items.length;

        if ("add".equalsIgnoreCase(cmd.getCmdStr())) {
            ConfigContext.getInstance().add(items);
        }
        else if("update".equalsIgnoreCase(cmd.getCmdStr())){
            ConfigContext.getInstance().update(items);
        }
        else if("del".equalsIgnoreCase(cmd.getCmdStr())){
            ConfigContext.getInstance().delete(items);
        }
        else if("sync".equalsIgnoreCase(cmd.getCmdStr())){
            ConfigContext.getInstance().sync(items);
        }
        return new Result();
    }
}
