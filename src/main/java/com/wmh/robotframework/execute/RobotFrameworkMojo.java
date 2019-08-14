package com.wmh.robotframework.execute;

/**
 * Runs the Robot tests. Behaves like invoking the "jybot" command.
 * <p>
 * Robot Framework tests cases are created in files and directories, and they are executed by configuring the path to
 * the file or directory in question to the testCasesDirectory configuration. The given file or directory creates the
 * top-level tests suites, which gets its name, unless overridden with the "name" option, from the file or directory
 * name.
 *
 * @goal run
 */
public class RobotFrameworkMojo extends AcceptanceTestMojo {

    protected void evaluateReturnCode(int robotRunReturnValue) {
    // ======== ==========================================
    // RC Explanation
    // ======== ==========================================
    // 0 All critical tests passed.
    // 1-249 Returned number of critical tests failed.
    // 250 250 or more critical failures.
    // 251 Help or version information printed.
    // 252 Invalid test data or command line options.
    // 253 Test execution stopped by user.
    // 255 Unexpected internal error.
    // ======== ==========================================
        switch (robotRunReturnValue) {
            case 0:
                // success
                break;
            case 1:
                throw new IllegalStateException("1 critical test case failed. Check the logs for details.");
            case 250:
                throw new IllegalStateException("250 or more critical test cases failed. Check the logs for details.");
            case 251:
                LOGGER.info("Help or version information printed. No tests were executed.");
                break;
            case 252:
                throw new IllegalStateException("Invalid test data or command line options.");
            case 255:
                throw new IllegalStateException("Unexpected internal error.");
            case 253:
                LOGGER.info("Test execution stopped by user.");
                break;
            default:
                throw new IllegalStateException(robotRunReturnValue
                        + " critical test cases failed. Check the logs for details.");
        }
    }

}
