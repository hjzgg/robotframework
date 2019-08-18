package com.wmh.robotframework.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.util.Date;

public class CollectAppender extends AppenderBase<ILoggingEvent> {

    @Override
    public void append(ILoggingEvent event) {
        StringBuilder exception = new StringBuilder();
        IThrowableProxy throwable = event.getThrowableProxy();
        if (throwable != null) {
            exception.append(throwable.getClassName()).append(StringUtils.SPACE).append(throwable.getMessage());
            for (int i = 0; i < throwable.getStackTraceElementProxyArray().length; i++) {
                exception.append(throwable.getStackTraceElementProxyArray()[i]).append(System.lineSeparator());
            }
        }
        LoggerMessage loggerMessage = new LoggerMessage(
                event.getMessage()
                , DateFormat.getDateTimeInstance().format(new Date(event.getTimeStamp())),
                event.getThreadName(),
                event.getLoggerName(),
                event.getLevel().levelStr,
                exception.toString()
        );
        LoggerQueue.getInstance().push(loggerMessage);
    }

}