package com.wmh.robotframework.log;

import org.slf4j.Logger;

import static com.wmh.robotframework.log.LogConstants.COLLECT_LOGGER_NAME;
import static org.slf4j.LoggerFactory.getLogger;

public interface LoggerAdapter {
    Logger LOGGER = getLogger(COLLECT_LOGGER_NAME);
}
