package com.wmh.robotframework.execute;

import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMojoWithLoadedClasspath {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractMojoWithLoadedClasspath.class);

    private static RobotMojoClassLoader currentMojoLoader;
    private static String ROBOT_ARTIFACT = join(File.separator, "org", "robotframework", "robotframework");

    /**
     * @parameter property="project.testClasspathElements"
     * @required
     * @readonly
     */
    private List<String> classpathElements;

    /**
     * @parameter property="settings.localRepository"
     * @required
     * @readonly
     */
    private String localRepository;

    public void execute() {
        loadClassPath();
        subclassExecute();
    }

    protected abstract void subclassExecute();

    private void loadClassPath() {
        List<URL> urls = new ArrayList<URL>();

        if (classpathElements != null) {
            for (String element : classpathElements) {
                File elementFile = new File(element);
                try {
                    urls.add(elementFile.toURI().toURL());
                } catch (MalformedURLException e) {
                    throw new IllegalStateException("Classpath loading error: " + element, e);
                }
            }
        }

        if (urls.size() > 0) {
            updateMojoLoader(urls);
            Thread.currentThread().setContextClassLoader(currentMojoLoader);
        }
    }

    protected String getClassPathString() {
        if (localRepository ==null || classpathElements==null) {
            // when executed outside of maven (like in unit tests)
            return System.getProperty("java.class.path");
        }
        String result= getRobotJar();
        for (String elem: classpathElements) {
            result +=File.pathSeparator + elem;
        }
        return result;
    }

    protected String getRobotJar() {
        File robots = new File(localRepository, ROBOT_ARTIFACT);
        String configured = currentVersion();
        return join(File.separator, robots.toString(), configured, "robotframework-"+configured+".jar");
    }

    static String currentVersion() {
        PythonInterpreter interp = new PythonInterpreter();
        try {
            interp.exec("from robot import version");
            interp.exec("rf_version = version.VERSION");
            return interp.get("rf_version").toString();
        } finally {
            interp.cleanup();
        }
    }

    protected static String join(String joiner, String... elements) {
        StringBuilder result = new StringBuilder();
        for (String elem: elements) {
            result.append(elem).append(joiner);
        }
        return result.substring(0, result.length()-joiner.length());
    }

    private void updateMojoLoader(List<URL> urls) {
        if (currentMojoLoader==null)
            currentMojoLoader = new RobotMojoClassLoader(urls.toArray(new URL[0]), getClass().getClassLoader());
        else
            currentMojoLoader.append(urls);
    }

    public File makeAbsolute(File folder, File file) {
        final File output;
        if (file.isAbsolute()) {
            output = file;
        } else {
            output = new File(folder, file.getName());
        }
        return output;
    }

}
