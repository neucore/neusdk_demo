package com.neucore.neulink.log;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternParser;

public class MyPatternLayout extends PatternLayout {

    public MyPatternLayout(String pattern) {
        super(pattern);
    }

    public PatternParser createPatternParser(String pattern) {
        return new MyPatternParser(pattern == null ? DEFAULT_CONVERSION_PATTERN : pattern);
    }
}
