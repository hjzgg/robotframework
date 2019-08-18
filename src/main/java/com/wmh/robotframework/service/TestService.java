package com.wmh.robotframework.service;

import com.wmh.robotframework.browser.BrowserDriverManager;
import com.wmh.robotframework.config.BrowserExportProperties;
import com.wmh.robotframework.config.SpringContext;
import com.wmh.robotframework.execute.RobotFrameworkMojo;
import com.wmh.robotframework.file.FileUtils;
import com.wmh.robotframework.file.ReadSign;
import com.wmh.robotframework.log.ILogService;
import com.wmh.robotframework.log.LoggerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.concurrent.*;

import static com.wmh.robotframework.log.LogConstants.TEST_CASE_CONTEXT_ID;

@Service
public class TestService implements LoggerAdapter {
    private static final ExecutorService ES = new ThreadPoolExecutor(
            20,
            30,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(100)
    );

    @Autowired
    private BrowserExportProperties browserExportProperties;

    public String execute(TestProperties tp) {
        String testCaseId = DigestUtils.md5DigestAsHex(tp.getTestCasesDirectory().getBytes()).toUpperCase();
        if (StringUtils.isBlank(tp.getDebugLogFile())) {
            try {
                File tmpFile = Files.createTempFile(Files.createTempDirectory(testCaseId), "debug", ".log").toFile();
                tp.setDebugLogFile(tmpFile.getAbsolutePath());
            } catch (IOException e) {
                LOGGER.error("DEBUG LOG FILE CREATE ERROR...", e);
            }
        }

        Future<?> future = ES.submit(() -> {
            MDC.put(TEST_CASE_CONTEXT_ID, testCaseId);
            BrowserDriverManager browserDriverManager = BrowserDriverManager.getInstance(tp.getDriverManagerType());
            if (browserDriverManager.canUseMirror()) {
                browserDriverManager.useMirror();
            }
            if (StringUtils.isNoneBlank(tp.getDriverVersion())) {
                browserDriverManager.version(tp.getDriverVersion());
            }
            browserDriverManager.setup();
            String browserDriverPath = browserDriverManager.getBinaryPath();
            System.setProperty(browserExportProperties.resovleExportPath(tp.getDriverManagerType()), browserDriverPath);
            LOGGER.info("load browser driver path: " + browserDriverPath);

            RobotFrameworkMojo robotFrameworkMojo = new RobotFrameworkMojo();
            LOGGER.error("TEST CASE PATH IS " + tp.getTestCasesDirectory());
            robotFrameworkMojo.setTestCasesDirectory(new File(tp.getTestCasesDirectory()));
            LOGGER.error("TEST REPORT PATH IS " + tp.getTestCasesDirectory());
            robotFrameworkMojo.setOutputDirectory(new File(tp.getOutputDirectory()));
            LOGGER.error("TEST DEBUG LOG PATH IS " + tp.getDebugLogFile());
            robotFrameworkMojo.setDebugFile(new File(tp.getDebugLogFile()));
            robotFrameworkMojo.execute();
        });

        //注册debug log文件的监听器
        try {
            Path debuglog = Paths.get(tp.getDebugLogFile());
            WatchService watcher = debuglog.getFileSystem().newWatchService();
            debuglog.register(
                    watcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY
            );

            ILogService logService = SpringContext.getBean(ILogService.class);
            long pos = 0;
            while (future.isDone() || future.isCancelled()) {
                WatchKey watckKey = watcher.take();
                if (Objects.isNull(watckKey)) {
                    continue;
                }
                for (WatchEvent<?> watchEvent : watckKey.pollEvents()) {
                    final WatchEvent.Kind<?> eventKind = watchEvent.kind();
                    if (eventKind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        ReadSign rs = FileUtils.readFile(tp.getDebugLogFile(), pos);
                        pos = rs.getPos();
                        if (Objects.nonNull(logService)) {
                            logService.log(testCaseId, rs.getContent());
                        }
                        break;
                    }
                }
            }
            watcher.close();
        } catch (Exception e) {
            LOGGER.error("文件监听器注册失败...", e);
        }

        return testCaseId;
    }
}
