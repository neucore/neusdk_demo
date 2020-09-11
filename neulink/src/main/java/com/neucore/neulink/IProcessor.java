package com.neucore.neulink;

import com.neucore.neulink.impl.NeuLinkConstant;
import com.neucore.neulink.impl.NeulinkTopicParser;

public interface IProcessor extends NeuLinkConstant {

    void execute(NeulinkTopicParser.Topic topic, String payload);

    String auth(NeulinkTopicParser.Topic topic,String payload);
}
