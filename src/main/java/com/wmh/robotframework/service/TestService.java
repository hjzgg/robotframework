package com.wmh.robotframework.service;

import com.wmh.robotframework.browser.BrowserDriverManager;
import com.wmh.robotframework.config.BrowserExportProperties;
import com.wmh.robotframework.config.CommonConfig;
import com.wmh.robotframework.config.SpringContext;
import com.wmh.robotframework.execute.RobotFrameworkMojo;
import com.wmh.robotframework.log.ILogService;
import com.wmh.robotframework.log.LoggerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    public void execute(TestProperties tp) {
        String testCaseId = tp.getTestCaseId();
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
        ES.submit(() -> {
            //一次 读取 字节数
            final int length = 1024;
            //重试次数
            int retry = 0;
            //日志同步
            ILogService logService = SpringContext.getBean(ILogService.class);
            try (RandomAccessFile raf = new RandomAccessFile(tp.getDebugLogFile(), "r")) {
                while (true) {
                    byte[] content = new byte[length];
                    int ll = raf.read(content);
                    //文件已经读取完毕
                    if (ll <= 0) {
                        if ((future.isDone() || future.isCancelled()) && retry++ > 2) {
                            break;
                        }
                        TimeUnit.MILLISECONDS.sleep(200);
                    } else {
                        logService.log(testCaseId, new String(content, 0, ll, StandardCharsets.UTF_8));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Debug log 同步异常...", e);
            }
        });
    }
}
