package com.neucore.neulink.impl.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.cmd.rrpc.QResult;
import com.neucore.neulink.impl.cmd.rrpc.TLibQueryCmd;

public class DefaultCarQueryListener implements ICmdListener<QResult, TLibQueryCmd> {

    @Override
    public QResult doAction(NeulinkEvent<TLibQueryCmd> event) {
        TLibQueryCmd cmd = event.getSource();
        String objtype = cmd.getObjtype();
        QResult result = new QResult();
        return result;
    }
}
