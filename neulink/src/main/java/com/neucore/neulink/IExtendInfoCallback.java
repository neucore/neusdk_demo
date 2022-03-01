package com.neucore.neulink;

import com.neucore.neulink.cmd.msg.SoftVInfo;

import java.util.List;
import java.util.Map;

public interface IExtendInfoCallback {

    List<SoftVInfo> getSubApps();

    List<Map<String,String>> getAttrs();
}
