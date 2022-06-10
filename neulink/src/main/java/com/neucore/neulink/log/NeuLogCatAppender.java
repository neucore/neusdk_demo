package com.neucore.neulink.log;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import android.util.Log;

/**
 * 源自 android-logging-log4j-1.0.3.jar
 *
 * @author Administrator
 */
class NeuLogCatAppender extends AppenderSkeleton {
    protected Layout tagLayout;

    public NeuLogCatAppender(Layout messageLayout, Layout tagLayout) {
        this.tagLayout = tagLayout;
        setLayout(messageLayout);
    }

    public NeuLogCatAppender(Layout messageLayout) {
        //这里定义的是Tag名称
        this(messageLayout, new PatternLayout("%c"));
    }

    public NeuLogCatAppender() {
        this(new PatternLayout("%c"));
    }

    protected void append(LoggingEvent le) {
        switch (le.getLevel().toInt()) {
            case 5000:
                if (le.getThrowableInformation() != null) {
                    Log.v(null, getLayout().format(le), le.getThrowableInformation().getThrowable());
                } else {
                    Log.v(null, getLayout().format(le));
                }
                break;
            case 10000:
                if (le.getThrowableInformation() != null) {
                    Log.d(null, getLayout().format(le), le.getThrowableInformation().getThrowable());
                } else {
                    Log.d(null, getLayout().format(le));
                }
                break;
            case 20000:
                if (le.getThrowableInformation() != null) {
                    Log.i(null, getLayout().format(le), le.getThrowableInformation().getThrowable());
                } else {
                    Log.i(null, getLayout().format(le));
                }
                break;
            case 30000:
                if (le.getThrowableInformation() != null) {
                    Log.w(null, getLayout().format(le), le.getThrowableInformation().getThrowable());
                } else {
                    Log.w(null, getLayout().format(le));
                }
                break;
            case 40000:
                if (le.getThrowableInformation() != null) {
                    Log.e(null, getLayout().format(le), le.getThrowableInformation().getThrowable());
                } else {
                    Log.e(null, getLayout().format(le));
                }
                break;
            case 50000:
                if (le.getThrowableInformation() != null) {
                    Log.wtf(null, getLayout().format(le), le.getThrowableInformation().getThrowable());
                } else
                    Log.wtf(null, getLayout().format(le));
                break;
        }
    }

    public void close() {
    }

    public boolean requiresLayout() {
        return true;
    }

    public Layout getTagLayout() {
        return this.tagLayout;
    }

    public void setTagLayout(Layout tagLayout) {
        this.tagLayout = tagLayout;
    }
}