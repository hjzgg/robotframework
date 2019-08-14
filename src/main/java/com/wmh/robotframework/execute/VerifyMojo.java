package com.wmh.robotframework.execute;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;

/**
 * Verifies the results from acceptance-test goal.
 */
public class VerifyMojo extends AbstractMojoWithLoadedClasspath {

    /**
     * The directory where the test cases are located.
     *
     * @parameter default-value="${project.basedir}/src/test/resources/robotframework/acceptance"
     */
    private File testCasesDirectory;

    /**
     * Sets the path to the generated XUnit compatible result file, relative to outputDirectory. The file is in xml
     * format. By default, the file name is derived from the testCasesDirectory parameter, replacing blanks in the
     * directory name by underscores.
     *
     * @parameter
     */
    private File xunitFile;

    /**
     * Configures where generated reports are to be placed.
     *
     * @parameter default-value="${project.build.directory}/robotframework-reports"
     */
    private File outputDirectory;

    /**
     * Log failures without failing the build.
     *
     * @parameter default-value="false"
     */
    private boolean isTestFailureIgnore;

    /**
     * Skip verification of tests. Bound to -DskipTests. This allows to skip acceptance tests together with all
     * other tests.
     *
     * @parameter property="skipTests"
     */
    private boolean skipTests;

    /**
     * Skip verification of  acceptance tests executed by this plugin. Bound to -DskipATs. This allows to run tests
     * and integration tests, but no acceptance tests.
     *
     * @parameter property="skipATs"
     */
    private boolean skipATs;

    /**
     * Skip verification of acceptance tests executed by this plugin together with other integration tests, e.g.
     * tests run by the maven-failsafe-plugin. Bound to -DskipITs
     *
     * @parameter property="skipITs"
     */
    private boolean skipITs;

    /**
     * Skip verification of tests, bound to -Dmaven.test.skip, which suppresses test compilation as well.
     *
     * @parameter default-value="false" property="maven.test.skip"
     */
    private boolean skip;

    private boolean shouldSkipTests() {
        return skipTests || skipITs || skipATs || skip;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void subclassExecute() {
        if (shouldSkipTests()) {
            LOGGER.info("RobotFramework tests are skipped.");
            return;
        }
        if (xunitFile == null) {
            String testCasesFolderName = testCasesDirectory.getName();
            xunitFile = new File("TEST-" + testCasesFolderName.replace(' ', '_') + ".xml");
        }

        LOGGER.info("Output directory is " + outputDirectory);

        final int errors;
        final int failures;

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xunitResult = documentBuilder.parse(makeAbsolute(outputDirectory, xunitFile));

            errors = readIntAttribute(xunitResult, "/testsuite/@errors");
            failures = readIntAttribute(xunitResult, "/testsuite/@failures");
            int tests = readIntAttribute(xunitResult, "/testsuite/@tests");

            LOGGER.info("\nTest Results :");

            if (failures > 0 || errors > 0) {

                LOGGER.info("\nFailing acceptance tests:\n");
                NodeList testCases = getFailuresAndErrors(xunitResult);
                for (int i = 0; i < testCases.getLength(); i++) {
                    Node testCase = testCases.item(i);
                    NamedNodeMap attributes = testCase.getAttributes();
                    LOGGER.info("    "
                            + attributes.getNamedItem("name").getNodeValue() + "(" + attributes.getNamedItem("classname").getNodeValue() + ")");
                }
            }

            LOGGER.info("\nTests run: " + tests + ", Failures: " + failures + ", Errors: " + errors + "\n");
        } catch (Exception e) {
            throw new IllegalStateException("failed to verify robotframework acceptance-test results", e);
        }
        if (errors > 0 || failures > 0) {
            String msg =
                    "There are acceptance test failures.\n\nPlease refer to " + outputDirectory.getAbsolutePath()
                            + " for the individual test results.";

            if (isTestFailureIgnore) {
                LOGGER.error(msg);
            } else {
                throw new IllegalStateException(msg);
            }
        }

    }

    private NodeList getFailuresAndErrors(Document xunitResult)
            throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String xPathExpression = "/testsuite/testcase[failure or error]";
        NodeList testCases = (NodeList) xPath.evaluate(xPathExpression, xunitResult, XPathConstants.NODESET);
        return testCases;
    }

    private int readIntAttribute(Document xunitResult, String xPathExpression)
            throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        return ((Number) xPath.evaluate(xPathExpression, xunitResult, XPathConstants.NUMBER)).intValue();
    }

}
