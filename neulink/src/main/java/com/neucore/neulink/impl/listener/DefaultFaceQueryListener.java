package com.neucore.neulink.impl.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.cmd.rrpc.QResult;
import com.neucore.neulink.impl.cmd.rrpc.TLibQueryCmd;
import com.neucore.neulink.impl.NeulinkEvent;

public class DefaultFaceQueryListener implements ICmdListener<QResult, TLibQueryCmd> {

    @Override
    public QResult doAction(NeulinkEvent<TLibQueryCmd> event) {
        try {
            TLibQueryCmd cmd = event.getSource();
            String objtype = cmd.getObjtype();
            QResult result = new QResult();
            if("face".equalsIgnoreCase(objtype)){
                //QueryActionResult queryResult = ListenerRegistry.getInstance().getFaceQueryListener().doAction(new NeulinkEvent(cmd));
                //result.setCount(count);
                throw new RuntimeException("人脸目标库查询还在建设中");
            }
            else if("body".equalsIgnoreCase(objtype)){
                throw new RuntimeException("人体目标库还在建设中");
            }
            else if("car".equalsIgnoreCase(objtype)){
                throw new RuntimeException("车辆目标库还在建设中");
            }
            else if("lic".equalsIgnoreCase(objtype)){
                throw new RuntimeException("车牌目标库还在建设中");
            }
            return result;
        }
        catch(Throwable ex){
            throw new RuntimeException(ex);
        }
    }
}
