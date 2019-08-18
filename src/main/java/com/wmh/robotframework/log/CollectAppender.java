package com.wmh.robotframework.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import com.wmh.robotframework.config.SpringContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import static com.wmh.robotframework.log.LogConstants.TEST_CASE_CONTEXT_ID;

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
                event.getFormattedMessage()
                , DateFormat.getDateTimeInstance().format(new Date(event.getTimeStamp())),
                event.getThreadName(),
                event.getLoggerName(),
                event.getLevel().levelStr,
                exception.toString()
        );
        this.log(loggerMessage);
    }

    private void log(LoggerMessage message) {
        ILogService logService = SpringContext.getBean(ILogService.class);
        if (Objects.nonNull(logService)) {
            String caseId = MDC.get(TEST_CASE_CONTEXT_ID);
            String msg = String.format("%s - %s - %s - %s", message.getTimestamp(), message.getClassName(), message.getBody(), message.getException());
            logService.log(caseId, msg);
        }
    }
}