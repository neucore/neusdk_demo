package com.neucore.neulink;

import com.google.gson.JsonObject;
import com.neucore.neulink.impl.NeulinkTopicParser;

import cn.hutool.json.JSONObject;

public interface IProcessor extends NeulinkConst {

    void execute(boolean debug,int qos,boolean retained,NeulinkTopicParser.Topic topic, JsonObject headers, JsonObject payload);
}
