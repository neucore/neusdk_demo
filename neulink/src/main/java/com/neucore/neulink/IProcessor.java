package com.neucore.neulink;

import com.neucore.neulink.impl.NeulinkTopicParser;

public interface IProcessor extends NeulinkConst {

    void execute(NeulinkTopicParser.Topic topic, String payload);
}
