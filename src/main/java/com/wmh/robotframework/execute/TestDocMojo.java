package com.wmh.robotframework.execute;

/*
 * Copyright 2011 Michael Mallete, Dietrich Schulten
 * Copyright 2013 Nokia Siemens Networks Oyj
 * Copyright 2013 Gaurav Arora
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.robotframework.RobotFramework;

import java.io.File;
import java.io.IOException;

/**
 * Create documentation of test suites using the Robot Framework <code>testdoc</code> tool.
 * 
 * Uses the <code>testdoc</code> bundled in Robot Framework jar distribution. For more help see
 * <a href="http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#test-data-documentation-tool-testdoc">testdoc documentation</a>.
 *
 */
public class TestDocMojo
        extends AbstractMojoWithLoadedClasspath {

    protected void subclassExecute() {
        try {
            runTestDoc();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to execute testdoc script: " + e.getMessage());
        }
    }

    public void runTestDoc()
            throws IOException {
        testdoc.populateDefaults(this);
        testdoc.ensureOutputDirectoryExists();
        String[] args = testdoc.generateRunArguments();
        LOGGER.info("Run arguments -> " + args);
        RobotFramework.run(args);
    }

    /**
     * Test case documentation configuration.
     *
     * Required settings:
     * <ul>
     * <li><code>outputFile</code>          The name for the output file.</li>
     * <li><code>dataSourceFile</code>     Name or path of the documented test case(s).</li>
     * </ul>
     * 
     * Paths are considered relative to the location of <code>pom.xml</code> and must point to a valid test case file. 
     * For example <code>src/main/test/ExampleTest.txt</code>
     * Optional settings:
     * <ul>
     * <li><code>outputDirectory</code>     Specifies the directory where documentation files are written.
     *                                      Considered to be relative to the ${basedir} of the project.
     *                                      Default ${project.build.directory}/robotframework/testdoc</li>
     * <li><code>title</code>               Set the title of the generated documentation. Underscores in 
     *                                      the title are converted to spaces. The default title is the 
     *                                      name of the top level suite.</li>
     * <li><code>name</code>                Override the name of the top level test suite.</li>
     * <li><code>doc</code>                 Override the documentation of the top level test suite.</li>
     * </ul>
     *
     * Example:
     * <pre><![CDATA[<testdoc>
     *      <outputFile>MyTests.html</outputFile>
     *      <dataSourceFile>src/test/resources/MyTests.txt</dataSourceFile>
     * </testdoc>]]></pre>
     *
     * @parameter
     * @required
     */
    private TestDocConfiguration testdoc;

    /**
     * Default output directory. Effective if outputDirectory is empty. Cannot be overridden.
     *
     * @parameter default-value="${project.build.directory}/robotframework/testdoc"
     * @readonly
     */
    File defaultTestdocOutputDirectory;
}
