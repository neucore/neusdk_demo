package com.neucore.neulink;

import com.neucore.neulink.impl.NeulinkTopicParser;

import cn.hutool.json.JSONObject;

public interface IProcessor extends NeulinkConst {

    void execute(NeulinkTopicParser.Topic topic,JSONObject headers, JSONObject payload);
}
