package com.neucore.neulink.impl;

import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.registry.ListenerRegistry;
import com.neucore.neulink.util.SystemProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.hutool.core.util.ObjectUtil;

public class PropChgWatcher extends Thread implements NeulinkConst {
    private Properties last = SystemProperties.get();
    public PropChgWatcher(){
        start();
    }
    @Override
    public void run() {
        while (true){
            try{
                Thread.sleep(1000);
            }
            catch (Exception e){}
            Properties props = SystemProperties.get();
            int size = props.size();
            String[] keys = new String[size];
            props.keySet().toArray(keys);
            List<SysPropAction> adds = new ArrayList<>();
            List<SysPropAction> updates = new ArrayList<>();
            List<SysPropAction> deletes = new ArrayList<>();
            for (String key:keys){
                if(!last.containsKey(key)){
                    adds.add(new SysPropAction(PROP_CHG_ACTION_ADD,key,props.getProperty(key)));
                }
                else if(last.containsKey(key)){
                    if(ObjectUtil.isNotEmpty(last.get(key))
                            && ObjectUtil.isNotEmpty(props.getProperty(key))
                            && !last.getProperty(key).equalsIgnoreCase(props.getProperty(key))){
                        updates.add(new SysPropAction(PROP_CHG_ACTION_UPD,key,props.getProperty(key)));
                    }
                    else if(ObjectUtil.isNotEmpty(last.get(key))
                            && ObjectUtil.isEmpty(props.get(key))){
                        deletes.add(new SysPropAction(PROP_CHG_ACTION_DEL,key,props.getProperty(key)));
                    }
                    else if(ObjectUtil.isEmpty(last.get(key))
                            && ObjectUtil.isNotEmpty(props.get(key))){
                        adds.add(new SysPropAction(PROP_CHG_ACTION_ADD,key,props.getProperty(key)));
                    }
                }
            }
            last = props;
            List<SysPropAction> chgs = new ArrayList<>();
            chgs.addAll(adds);
            chgs.addAll(updates);
            chgs.addAll(deletes);
            if(ObjectUtil.isNotEmpty(chgs)){
                ListenerRegistry.getInstance().fireProChgListener(chgs);
            }
        }
    }
}
