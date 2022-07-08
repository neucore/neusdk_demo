package com.neucore.neulink.log;

import com.neucore.neulink.util.RequestContext;

import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

public class MyPatternParser extends PatternParser {
    public MyPatternParser(String pattern) {
        super(pattern);
    }

    public void finalizeConverter(char c) {
        if (c == 'I') {
            addConverter(new UserDirPatternConverter(formattingInfo));
            currentLiteral.setLength(0);
        } else {
            super.finalizeConverter(c);
        }
    }

    private class UserDirPatternConverter extends PatternConverter {
        UserDirPatternConverter(FormattingInfo formattingInfo) {
            super(formattingInfo);
        }

        public String convert(LoggingEvent event) {
            return RequestContext.getId();
        }
    }
}