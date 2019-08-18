/*
 * (C) Copyright 2015 Boni Garcia (http://bonigarcia.github.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.wmh.robotframework.browser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.wmh.robotframework.log.LoggerAdapter;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.util.*;

import static com.wmh.robotframework.browser.Architecture.X32;
import static com.wmh.robotframework.browser.Architecture.X64;
import static com.wmh.robotframework.browser.Config.isNullOrEmpty;
import static com.wmh.robotframework.browser.DriverManagerType.*;
import static com.wmh.robotframework.browser.OperatingSystem.WIN;
import static com.wmh.robotframework.browser.Shell.*;
import static java.lang.Integer.signum;
import static java.lang.Integer.valueOf;
import static java.util.Collections.sort;
import static java.util.Optional.empty;
import static javax.xml.xpath.XPathConstants.NODESET;
import static javax.xml.xpath.XPathFactory.newInstance;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.FilenameUtils.removeExtension;
import static org.apache.commons.lang3.SystemUtils.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Parent driver manager.
 *
 * @author Boni Garcia (boni.gg@gmail.com)
 * @since 2.1.0
 */
public abstract class BrowserDriverManager implements LoggerAdapter {

//    static final Logger log = getLogger(lookup().lookupClass());

    protected static final String SLASH = "/";
    protected static final String INSIDERS = "insiders";
    protected static final String BETA = "beta";
    protected static final String ONLINE = "online";
    protected static final String LOCAL = "local";

    protected abstract List<URL> getDrivers() throws IOException;

    protected abstract Optional<String> getBrowserVersion();

    protected abstract DriverManagerType getDriverManagerType();

    protected abstract String getDriverName();

    protected abstract void setDriverVersion(String version);

    protected abstract String getDriverVersion();

    protected abstract void setDriverUrl(URL url);

    protected abstract URL getDriverUrl();

    protected abstract Optional<URL> getMirrorUrl();

    protected abstract Optional<String> getExportParameter();

    protected static Map<DriverManagerType, BrowserDriverManager> instanceMap = new EnumMap<>(
            DriverManagerType.class);

    protected HttpClient httpClient;
    protected Downloader downloader;
    protected UrlFilter urlFilter;
    protected String versionToDownload;
    protected String downloadedVersion;
    protected String latestVersion;
    protected String binaryPath;
    protected boolean mirrorLog;
    protected List<String> listVersions;
    protected boolean forcedArch;
    protected boolean forcedOs;
    protected boolean isLatest;
    protected boolean retry = true;
    protected Config config = new Config();
    protected Preferences preferences = new Preferences(config);
    protected String preferenceKey;
    protected Properties versionsProperties;

    public static Config globalConfig() {
        Config global = new Config();
        global.setAvoidAutoReset(true);
        for (DriverManagerType type : DriverManagerType.values()) {
            BrowserDriverManager.getInstance(type).setConfig(global);
        }
        return global;
    }

    public Config config() {
        return config;
    }

    public static synchronized BrowserDriverManager chromedriver() {
        if (!instanceMap.containsKey(CHROME)) {
            instanceMap.put(CHROME, new ChromeDriverManager());
        }
        return instanceMap.get(CHROME);
    }

    public static synchronized BrowserDriverManager firefoxdriver() {
        if (!instanceMap.containsKey(FIREFOX)) {
            instanceMap.put(FIREFOX, new FirefoxDriverManager());
        }
        return instanceMap.get(FIREFOX);
    }

    public static synchronized BrowserDriverManager operadriver() {
        if (!instanceMap.containsKey(OPERA)) {
            instanceMap.put(OPERA, new OperaDriverManager());
        }
        return instanceMap.get(OPERA);
    }

    public static synchronized BrowserDriverManager edgedriver() {
        if (!instanceMap.containsKey(EDGE)) {
            instanceMap.put(EDGE, new EdgeDriverManager());
        }
        return instanceMap.get(EDGE);
    }

    public static synchronized BrowserDriverManager iedriver() {
        if (!instanceMap.containsKey(IEXPLORER)) {
            instanceMap.put(IEXPLORER, new InternetExplorerDriverManager());
        }
        return instanceMap.get(IEXPLORER);
    }

    public static synchronized BrowserDriverManager phantomjs() {
        if (!instanceMap.containsKey(PHANTOMJS)) {
            instanceMap.put(PHANTOMJS, new PhantomJsDriverManager());
        }
        return instanceMap.get(PHANTOMJS);
    }

    public static synchronized BrowserDriverManager seleniumServerStandalone() {
        if (!instanceMap.containsKey(SELENIUM_SERVER_STANDALONE)) {
            instanceMap.put(SELENIUM_SERVER_STANDALONE,
                    new SeleniumServerStandaloneManager());
        }
        return instanceMap.get(SELENIUM_SERVER_STANDALONE);
    }

    protected static synchronized BrowserDriverManager voiddriver() {
        return new VoidDriverManager();
    }

    public static synchronized BrowserDriverManager getInstance(
            DriverManagerType driverManagerType) {
        if (driverManagerType == null) {
            return voiddriver();
        }
        switch (driverManagerType) {
            case CHROME:
                return chromedriver();
            case FIREFOX:
                return firefoxdriver();
            case OPERA:
                return operadriver();
            case IEXPLORER:
                return iedriver();
            case EDGE:
                return edgedriver();
            case PHANTOMJS:
                return phantomjs();
            case SELENIUM_SERVER_STANDALONE:
                return seleniumServerStandalone();
            default:
                return voiddriver();
        }
    }

    public static synchronized BrowserDriverManager getInstance(
            Class<?> webDriverClass) {
        switch (webDriverClass.getName()) {
            case "org.openqa.selenium.chrome.ChromeDriver":
                return chromedriver();
            case "org.openqa.selenium.firefox.FirefoxDriver":
                return firefoxdriver();
            case "org.openqa.selenium.opera.OperaDriver":
                return operadriver();
            case "org.openqa.selenium.ie.InternetExplorerDriver":
                return iedriver();
            case "org.openqa.selenium.edge.EdgeDriver":
                return edgedriver();
            case "org.openqa.selenium.phantomjs.PhantomJSDriver":
                return phantomjs();
            default:
                return voiddriver();
        }
    }

    public synchronized void setup() {
        if (getDriverManagerType() != null) {
            try {
                Architecture architecture = config().getArchitecture();
                String driverVersion = getDriverVersion();
                isLatest = isVersionLatest(driverVersion);
                manage(architecture, driverVersion);
            } finally {
                if (!config().isAvoidAutoReset()) {
                    reset();
                }
            }
        }
    }

    public BrowserDriverManager version(String version) {
        setDriverVersion(version);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager architecture(Architecture architecture) {
        config().setArchitecture(architecture);
        forcedArch = true;
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager arch32() {
        architecture(X32);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager arch64() {
        architecture(X64);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager operatingSystem(OperatingSystem os) {
        config().setOs(os.name());
        forcedOs = true;
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager forceCache() {
        config().setForceCache(true);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager forceDownload() {
        config().setOverride(true);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager driverRepositoryUrl(URL url) {
        setDriverUrl(url);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager useMirror() {
        Optional<URL> mirrorUrl = getMirrorUrl();
        if (!mirrorUrl.isPresent()) {
            throw new WebDriverManagerException("Mirror URL not available");
        }
        config().setUseMirror(true);
        setDriverUrl(mirrorUrl.get());
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager proxy(String proxy) {
        config().setProxy(proxy);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager proxyUser(String proxyUser) {
        config().setProxyUser(proxyUser);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager proxyPass(String proxyPass) {
        config().setProxyPass(proxyPass);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager useBetaVersions() {
        config().setUseBetaVersions(true);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager ignoreVersions(String... versions) {
        config().setIgnoreVersions(versions);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager gitHubTokenName(String gitHubTokenName) {
        config().setGitHubTokenName(gitHubTokenName);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager gitHubTokenSecret(String gitHubTokenSecret) {
        config().setGitHubTokenSecret(gitHubTokenSecret);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager timeout(int timeout) {
        config().setTimeout(timeout);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager properties(String properties) {
        config().setProperties(properties);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager targetPath(String targetPath) {
        config().setTargetPath(targetPath);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager avoidExport() {
        config().setAvoidExport(true);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager avoidOutputTree() {
        config().setAvoidOutputTree(true);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager avoidAutoVersion() {
        config().setAvoidAutoVersion(true);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager avoidPreferences() {
        config().setAvoidPreferences(true);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager ttl(int seconds) {
        config().setTtl(seconds);
        return instanceMap.get(getDriverManagerType());
    }

    public BrowserDriverManager browserPath(String browserPath) {
        config().setBinaryPath(browserPath);
        return instanceMap.get(getDriverManagerType());
    }

    // ------------

    public String getBinaryPath() {
        return instanceMap.get(getDriverManagerType()).binaryPath;
    }

    public String getDownloadedVersion() {
        return instanceMap.get(getDriverManagerType()).downloadedVersion;
    }

    public List<String> getVersions() {
        httpClient = new HttpClient(config());
        try {
            List<URL> drivers = getDrivers();
            List<String> versions = new ArrayList<>();
            for (URL url : drivers) {
                String version = getCurrentVersion(url, getDriverName());
                if (version.isEmpty() || version.equalsIgnoreCase("icons")) {
                    continue;
                }
                if (version.startsWith(".")) {
                    version = version.substring(1);
                }
                if (!versions.contains(version)) {
                    versions.add(version);
                }
            }
            LOGGER.trace("Version list before sorting {}", versions);
            sort(versions, new VersionComparator());
            return versions;
        } catch (IOException e) {
            throw new WebDriverManagerException(e);
        }
    }

    public void clearPreferences() {
        instanceMap.get(getDriverManagerType()).preferences.clear();
    }

    public void clearCache() {
        String targetPath = config().getTargetPath();
        try {
            LOGGER.debug("Clearing cache at {}", targetPath);
            deleteDirectory(new File(targetPath));
        } catch (Exception e) {
            LOGGER.warn("Exception deleting cache at {}", targetPath, e);
        }
    }

    // ------------

    protected String preDownload(String target, String version) {
        LOGGER.trace("Pre-download. target={}, version={}", target, version);
        return target;
    }

    protected File postDownload(File archive) {
        File parentFolder = archive.getParentFile();
        File[] ls = parentFolder.listFiles();
        for (File f : ls) {
            if (getDriverName().contains(removeExtension(f.getName()))) {
                LOGGER.trace("Found binary in post-download: {}", f);
                return f;
            }
        }
        throw new WebDriverManagerException("Driver " + getDriverName()
                + " not found (using temporal folder " + parentFolder + ")");
    }

    protected String getCurrentVersion(URL url, String driverName) {
        String currentVersion = "";
        try {
            currentVersion = url.getFile().substring(
                    url.getFile().indexOf(SLASH) + 1,
                    url.getFile().lastIndexOf(SLASH));
        } catch (StringIndexOutOfBoundsException e) {
            LOGGER.trace("Exception getting version of URL {} ({})", url,
                    e.getMessage());
        }

        return currentVersion;
    }

    protected void manage(Architecture arch, String version) {
        httpClient = new HttpClient(config());
        try (HttpClient wdmHttpClient = httpClient) {
            downloader = new Downloader(getDriverManagerType());
            urlFilter = new UrlFilter();

            boolean getLatest = isVersionLatest(version);
            boolean cache = config().isForceCache();

            if (getLatest) {
                version = detectDriverVersionFromBrowser();
            }
            getLatest = isNullOrEmpty(version);

            // For Edge
            if (checkInsiderVersion(version)) {
                return;
            }

            String os = config().getOs();
            LOGGER.trace("Managing {} arch={} version={} getLatest={} cache={}",
                    getDriverName(), arch, version, getLatest, cache);

            if (getLatest && latestVersion != null) {
                LOGGER.debug("Latest version of {} is {} (recently resolved)",
                        getDriverName(), latestVersion);
                version = latestVersion;
                cache = true;
            }

            Optional<String> driverInCache = handleCache(arch, version, os,
                    getLatest, cache);

            String versionStr = getLatest ? "(latest version)" : version;
            if (driverInCache.isPresent() && !config().isOverride()) {
                storeVersionToDownload(version);
                downloadedVersion = version;
                LOGGER.debug("Driver {} {} found in cache", getDriverName(),
                        versionStr);
                exportDriver(driverInCache.get());
            } else {
                List<URL> candidateUrls = filterCandidateUrls(arch, version,
                        getLatest);

                if (candidateUrls.isEmpty()) {
                    String errorMessage = getDriverName() + " " + versionStr
                            + " for " + os + arch.toString() + " not found in "
                            + getDriverUrl();
                    LOGGER.error(errorMessage);
                    throw new WebDriverManagerException(errorMessage);
                }

                downloadCandidateUrls(candidateUrls);
            }

        } catch (Exception e) {
            handleException(e, arch, version);
        }
    }

    private String detectDriverVersionFromBrowser() {
        String version = "";
        if (config().isAvoidAutoVersion()) {
            return version;
        }

        Optional<String> optionalBrowserVersion = getBrowserVersion();
        if (optionalBrowserVersion.isPresent()) {
            String browserVersion = optionalBrowserVersion.get();
            LOGGER.trace("Detected {} version {}", getDriverManagerType(),
                    browserVersion);
            preferenceKey = getDriverManagerType().name().toLowerCase()
                    + browserVersion;

            if (usePreferences()
                    && preferences.checkKeyInPreferences(preferenceKey)) {
                // Get driver version from preferences
                version = preferences.getValueFromPreferences(preferenceKey);
            } else {
                // Get driver version from properties
                version = getVersionForInstalledBrowser(browserVersion);
            }
            if (!isNullOrEmpty(version)) {
                LOGGER.info(
                        "Using {} {} (since {} {} is installed in your machine)",
                        getDriverName(), version, getDriverManagerType(),
                        browserVersion);
            }
        } else {
            LOGGER.debug(
                    "The proper {} version for your {} is unknown ... trying with the latest",
                    getDriverName(), getDriverManagerType());
        }

        return version;
    }

    private boolean usePreferences() {
        boolean usePrefs = !config().isAvoidPreferences()
                && !config().isOverride() && !forcedArch && !forcedOs;
        LOGGER.trace("Using preferences {}", usePrefs);
        return usePrefs;
    }

    private boolean checkInsiderVersion(String version) {
        if (version.equals(INSIDERS)) {
            String systemRoot = System.getenv("SystemRoot");
            File microsoftWebDriverFile = new File(systemRoot,
                    "System32" + File.separator + "MicrosoftWebDriver.exe");
            if (microsoftWebDriverFile.exists()) {
                downloadedVersion = INSIDERS;
                exportDriver(microsoftWebDriverFile.toString());
                return true;
            } else {
                retry = false;
                throw new WebDriverManagerException(
                        "MicrosoftWebDriver.exe should be installed in an elevated command prompt executing: "
                                + "dism /Online /Add-Capability /CapabilityName:Microsoft.WebDriver~~~~0.0.1.0");
            }
        }
        return false;
    }

    private boolean isVersionLatest(String version) {
        return isNullOrEmpty(version) || version.equalsIgnoreCase("latest");
    }

    private String getVersionForInstalledBrowser(String browserVersion) {
        String driverVersion = "";
        DriverManagerType driverManagerType = getDriverManagerType();
        String driverLowerCase = driverManagerType.name().toLowerCase();
        Optional<String> driverVersionForBrowser = getDriverVersionForBrowserFromProperties(
                driverLowerCase + browserVersion, false);
        if (driverVersionForBrowser.isPresent()) {
            driverVersion = driverVersionForBrowser.get();
        } else {
            LOGGER.debug(
                    "The driver version for {} {} is unknown ... trying with latest",
                    driverManagerType, browserVersion);
        }
        return driverVersion;
    }

    private Optional<String> getDriverVersionForBrowserFromProperties(
            String key, boolean online) {
        String onlineMessage = online ? ONLINE : LOCAL;
        LOGGER.trace("Getting driver version from {} properties for {}",
                onlineMessage, key);
        String value = getVersionFromProperties(online).getProperty(key);
        if (value == null) {
            LOGGER.debug("Driver for {} not found in {} properties", key,
                    onlineMessage);
            versionsProperties = null;
            value = getVersionFromProperties(!online).getProperty(key);
        }
        return value == null ? empty() : Optional.of(value);
    }

    private Properties getVersionFromProperties(boolean online) {
        if (versionsProperties != null) {
            LOGGER.trace("Already created versions.properties");
            return versionsProperties;
        } else {
            try {
                InputStream inputStream = getVersionsInputStream(online);
                versionsProperties = new Properties();
                versionsProperties.load(inputStream);
                inputStream.close();
            } catch (Exception e) {
                versionsProperties = null;
                throw new IllegalStateException(
                        "Cannot read versions.properties", e);
            }
            return versionsProperties;
        }
    }

    private InputStream getVersionsInputStream(boolean online)
            throws IOException {
        String onlineMessage = online ? ONLINE : LOCAL;
        LOGGER.trace("Reading {} version.properties to find out driver version",
                onlineMessage);
        InputStream inputStream;
        try {
            if (online) {
                inputStream = getOnlineVersionsInputStream();
            } else {
                inputStream = getLocalVersionsInputStream();
            }
        } catch (Exception e) {
            String exceptionMessage = online ? LOCAL : ONLINE;
            LOGGER.warn("Error reading version.properties, using {} instead",
                    exceptionMessage);
            if (online) {
                inputStream = getLocalVersionsInputStream();
            } else {
                inputStream = getOnlineVersionsInputStream();
            }
        }
        return inputStream;
    }

    private InputStream getLocalVersionsInputStream() {
        InputStream inputStream;
        inputStream = Config.class.getResourceAsStream("/versions.properties");
        return inputStream;
    }

    private InputStream getOnlineVersionsInputStream() throws IOException {
        return httpClient
                .execute(httpClient
                        .createHttpGet(config().getVersionsPropertiesUrl()))
                .getEntity().getContent();
    }

    protected void handleException(Exception e, Architecture arch,
                                   String version) {
        String versionStr = isNullOrEmpty(version) ? "(latest version)"
                : version;
        String errorMessage = String.format(
                "There was an error managing %s %s (%s)", getDriverName(),
                versionStr, e.getMessage());
        if (!config().isForceCache() && retry) {
            config().setForceCache(true);
            config().setUseMirror(true);
            retry = false;
            LOGGER.warn("{} ... trying again using cache and mirror",
                    errorMessage);
            manage(arch, version);
        } else {
            LOGGER.error("{}", errorMessage, e);
            throw new WebDriverManagerException(e);
        }
    }

    protected void downloadCandidateUrls(List<URL> candidateUrls)
            throws IOException, InterruptedException {
        URL url = candidateUrls.iterator().next();

        String exportValue = downloader.download(url, versionToDownload,
                getDriverName());
        exportDriver(exportValue);
        downloadedVersion = versionToDownload;
    }

    protected List<URL> filterCandidateUrls(Architecture arch, String version,
                                            boolean getLatest) throws IOException {
        List<URL> urls = getDrivers();
        List<URL> candidateUrls;
        LOGGER.trace("All URLs: {}", urls);

        boolean continueSearchingVersion;
        do {
            // Get the latest or concrete version
            candidateUrls = getLatest ? checkLatest(urls, getDriverName())
                    : getVersion(urls, getDriverName(), version);
            LOGGER.trace("Candidate URLs: {}", candidateUrls);
            if (versionToDownload == null
                    || this.getClass().equals(EdgeDriverManager.class)) {
                break;
            }

            // Filter by OS
            if (!getDriverName().equalsIgnoreCase("IEDriverServer")
                    && !getDriverName()
                    .equalsIgnoreCase("selenium-server-standalone")) {
                candidateUrls = urlFilter.filterByOs(candidateUrls,
                        config().getOs());
            }

            // Filter by architecture
            candidateUrls = urlFilter.filterByArch(candidateUrls, arch,
                    forcedArch);

            // Filter by distro
            candidateUrls = filterByDistro(candidateUrls);

            // Filter by ignored versions
            candidateUrls = filterByIgnoredVersions(candidateUrls);

            // Find out if driver version has been found or not
            continueSearchingVersion = candidateUrls.isEmpty() && getLatest;
            if (continueSearchingVersion) {
                LOGGER.info(
                        "No binary found for {} {} ... seeking another version",
                        getDriverName(), versionToDownload);
                urls = removeFromList(urls, versionToDownload);
                versionToDownload = null;
            }
        } while (continueSearchingVersion);
        return candidateUrls;
    }

    protected List<URL> filterByIgnoredVersions(List<URL> candidateUrls) {
        if (config().getIgnoreVersions() != null && !candidateUrls.isEmpty()) {
            candidateUrls = urlFilter.filterByIgnoredVersions(candidateUrls,
                    config().getIgnoreVersions());
        }
        return candidateUrls;
    }

    protected List<URL> filterByDistro(List<URL> candidateUrls)
            throws IOException {
        // Filter phantomjs 2.5.0 in Linux
        if (config().getOs().equalsIgnoreCase("linux")
                && getDriverName().contains("phantomjs")) {
            candidateUrls = urlFilter.filterByDistro(candidateUrls, "2.5.0");
        }
        return candidateUrls;
    }

    protected Optional<String> handleCache(Architecture arch, String version,
                                           String os, boolean getLatest, boolean cache) {
        Optional<String> driverInCache = empty();
        if (cache || !getLatest) {
            driverInCache = getDriverFromCache(version, arch, os);
        }
        storeVersionToDownload(version);
        return driverInCache;
    }

    protected Optional<String> getDriverFromCache(String driverVersion,
                                                  Architecture arch, String os) {
        LOGGER.trace("Checking if {} exists in cache", getDriverName());
        List<File> filesInCache = getFilesInCache();
        if (!filesInCache.isEmpty()) {
            // Filter by name
            filesInCache = filterCacheBy(filesInCache, getDriverName());

            // Filter by version
            filesInCache = filterCacheBy(filesInCache, driverVersion);

            // Filter by OS
            if (!getDriverName().equals("MicrosoftWebDriver")) {
                filesInCache = filterCacheBy(filesInCache, os.toLowerCase());
            }

            if (filesInCache.size() == 1) {
                return Optional.of(filesInCache.get(0).toString());
            }

            // Filter by arch
            filesInCache = filterCacheBy(filesInCache, arch.toString());

            if (!filesInCache.isEmpty()) {
                return Optional.of(
                        filesInCache.get(filesInCache.size() - 1).toString());
            }
        }

        LOGGER.trace("{} not found in cache", getDriverName());
        return empty();
    }

    protected List<File> filterCacheBy(List<File> input, String key) {
        List<File> output = new ArrayList<>(input);
        if (!key.isEmpty() && !input.isEmpty()) {
            for (File f : input) {
                if (!f.toString().contains(key)) {
                    output.remove(f);
                }
            }
        }
        LOGGER.trace("Filter cache by {} -- input list {} -- output list {} ", key,
                input, output);
        return output;
    }

    protected List<File> getFilesInCache() {
        return (List<File>) listFiles(new File(downloader.getTargetPath()),
                null, true);
    }

    protected List<URL> removeFromList(List<URL> list, String version) {
        List<URL> out = new ArrayList<>(list);
        for (URL url : list) {
            if (url.getFile().contains(version)) {
                out.remove(url);
            }
        }
        return out;
    }

    protected List<URL> getVersion(List<URL> list, String driver,
                                   String version) {
        List<URL> out = new ArrayList<>();
        if (getDriverName().contains("MicrosoftWebDriver")) {
            int i = listVersions.indexOf(version);
            if (i != -1) {
                out.add(list.get(i));
            }
        }

        for (URL url : list) {
            if (url.getFile().contains(driver)
                    && url.getFile().contains(version)
                    && !url.getFile().contains("-symbols")) {
                out.add(url);
            }
        }

        if (versionToDownload != null && !versionToDownload.equals(version)) {
            versionToDownload = version;
            LOGGER.info("Using {} {}", driver, version);
        }
        return out;
    }

    protected List<URL> checkLatest(List<URL> list, String driver) {
        LOGGER.trace("Checking the lastest version of {} with URL list {}", driver,
                list);
        List<URL> out = new ArrayList<>();
        List<URL> copyOfList = new ArrayList<>(list);

        for (URL url : copyOfList) {
            try {
                handleDriver(url, driver, out);
            } catch (Exception e) {
                LOGGER.trace("There was a problem with URL {} : {}", url,
                        e.getMessage());
                list.remove(url);
            }
        }
        storeVersionToDownload(versionToDownload);
        latestVersion = versionToDownload;
        LOGGER.info("Latest version of {} is {}", driver, versionToDownload);
        return out;
    }

    protected void handleDriver(URL url, String driver, List<URL> out) {
        if (!config().isUseBetaVersions()
                && (url.getFile().toLowerCase().contains("beta"))) {
            return;
        }

        if (url.getFile().contains(driver)) {
            String currentVersion = getCurrentVersion(url, driver);

            if (currentVersion.equalsIgnoreCase(driver)) {
                return;
            }
            if (versionToDownload == null) {
                versionToDownload = currentVersion;
            }
            if (versionCompare(currentVersion, versionToDownload) > 0) {
                versionToDownload = currentVersion;
                out.clear();
            }
            if (url.getFile().contains(versionToDownload)) {
                out.add(url);
            }
        }
    }

    protected boolean isUsingTaobaoMirror() {
        return getDriverUrl().getHost().equalsIgnoreCase("npm.taobao.org");
    }

    protected Integer versionCompare(String str1, String str2) {
        String[] vals1 = str1.replaceAll("v", "").split("\\.");
        String[] vals2 = str2.replaceAll("v", "").split("\\.");

        if (vals1[0].equals("")) {
            vals1[0] = "0";
        }
        if (vals2[0].equals("")) {
            vals2[0] = "0";
        }

        int i = 0;
        while (i < vals1.length && i < vals2.length
                && vals1[i].equals(vals2[i])) {
            i++;
        }

        if (i < vals1.length && i < vals2.length) {
            return signum(valueOf(vals1[i]).compareTo(valueOf(vals2[i])));
        } else {
            return signum(vals1.length - vals2.length);
        }
    }

    /**
     * This method works also for http://npm.taobao.org/ and
     * https://bitbucket.org/ mirrors.
     */
    protected List<URL> getDriversFromMirror(URL driverUrl) throws IOException {
        if (mirrorLog) {
            LOGGER.info("Crawling driver list from mirror {}", driverUrl);
            mirrorLog = true;
        } else {
            LOGGER.trace("[Recursive call] Crawling driver list from mirror {}",
                    driverUrl);
        }

        String driverStr = driverUrl.toString();
        String driverUrlContent = driverUrl.getPath();

        HttpResponse response = httpClient
                .execute(httpClient.createHttpGet(driverUrl));
        try (InputStream in = response.getEntity().getContent()) {
            org.jsoup.nodes.Document doc = Jsoup.parse(in, null, "");
            Iterator<org.jsoup.nodes.Element> iterator = doc.select("a")
                    .iterator();
            List<URL> urlList = new ArrayList<>();

            while (iterator.hasNext()) {
                String link = iterator.next().attr("href");
                if (link.contains("mirror") && link.endsWith(SLASH)) {
                    urlList.addAll(getDriversFromMirror(new URL(
                            driverStr + link.replace(driverUrlContent, ""))));
                } else if (link.startsWith(driverUrlContent)
                        && !link.contains("icons")) {
                    urlList.add(new URL(
                            driverStr + link.replace(driverUrlContent, "")));
                }
            }
            return urlList;
        }
    }

    protected List<URL> getDriversFromXml(URL driverUrl) throws IOException {
        LOGGER.info("Reading {} to seek {}", driverUrl, getDriverName());
        List<URL> urls = new ArrayList<>();
        HttpResponse response = httpClient
                .execute(httpClient.createHttpGet(driverUrl));
        try {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()))) {
                Document xml = loadXML(reader);
                NodeList nodes = (NodeList) newInstance().newXPath().evaluate(
                        "//Contents/Key", xml.getDocumentElement(), NODESET);

                for (int i = 0; i < nodes.getLength(); ++i) {
                    Element e = (Element) nodes.item(i);
                    urls.add(new URL(driverUrl
                            + e.getChildNodes().item(0).getNodeValue()));
                }
            }
        } catch (Exception e) {
            throw new WebDriverManagerException(e);
        }
        return urls;
    }

    protected Document loadXML(Reader reader)
            throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(reader);
        return builder.parse(is);
    }

    protected void exportDriver(String variableValue) {
        binaryPath = variableValue;
        Optional<String> exportParameter = getExportParameter();
        if (!config.isAvoidExport() && exportParameter.isPresent()) {
            String variableName = exportParameter.get();
            LOGGER.info("Exporting {} as {}", variableName, variableValue);
            System.setProperty(variableName, variableValue);
        } else {
            LOGGER.info("Resulting binary {}", variableValue);
        }
    }

    protected InputStream openGitHubConnection(URL driverUrl)
            throws IOException {
        HttpGet get = httpClient.createHttpGet(driverUrl);

        String gitHubTokenName = config().getGitHubTokenName();
        String gitHubTokenSecret = config().getGitHubTokenSecret();
        if (!isNullOrEmpty(gitHubTokenName)
                && !isNullOrEmpty(gitHubTokenSecret)) {
            String userpass = gitHubTokenName + ":" + gitHubTokenSecret;
            String basicAuth = "Basic "
                    + new String(new Base64().encode(userpass.getBytes()));
            get.addHeader("Authorization", basicAuth);
        }

        return httpClient.execute(get).getEntity().getContent();
    }

    protected List<URL> getDriversFromGitHub() throws IOException {
        List<URL> urls;
        URL driverUrl = getDriverUrl();
        LOGGER.info("Reading {} to seek {}", driverUrl, getDriverName());

        if (isUsingTaobaoMirror()) {
            urls = getDriversFromMirror(driverUrl);
        } else {
            String driverVersion = versionToDownload;

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(openGitHubConnection(driverUrl)))) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                GitHubApi[] releaseArray = gson.fromJson(reader,
                        GitHubApi[].class);

                if (driverVersion != null) {
                    releaseArray = new GitHubApi[]{
                            getVersion(releaseArray, driverVersion)};
                }

                urls = new ArrayList<>();
                for (GitHubApi release : releaseArray) {
                    if (release != null) {
                        List<LinkedTreeMap<String, Object>> assets = release
                                .getAssets();
                        for (LinkedTreeMap<String, Object> asset : assets) {
                            urls.add(new URL(asset.get("browser_download_url")
                                    .toString()));
                        }
                    }
                }
            }
        }
        return urls;
    }

    protected GitHubApi getVersion(GitHubApi[] releaseArray, String version) {
        GitHubApi out = null;
        for (GitHubApi release : releaseArray) {
            LOGGER.trace("Get version {} of {}", version, release);
            if ((release.getName() != null
                    && release.getName().contains(version))
                    || (release.getTagName() != null
                    && release.getTagName().contains(version))) {
                out = release;
                break;
            }
        }
        return out;
    }

    protected HttpClient getHttpClient() {
        return httpClient;
    }

    protected FilenameFilter getFolderFilter() {
        return (dir, name) -> dir.isDirectory()
                && name.toLowerCase().contains(getDriverName());
    }

    protected Optional<String> getDefaultBrowserVersion(String programFilesEnv,
                                                        String winBrowserName, String linuxBrowserName,
                                                        String macBrowserName, String versionFlag,
                                                        String browserNameInOutput) {
        String browserBinaryPath = config().getBinaryPath();
        if (IS_OS_WINDOWS) {
            String programFiles = System.getenv(programFilesEnv)
                    .replaceAll("\\\\", "\\\\\\\\");

            String browserPath = isNullOrEmpty(browserBinaryPath)
                    ? programFiles + winBrowserName
                    : browserBinaryPath;
            String browserVersionOutput = runAndWait(getExecFile(), "wmic",
                    "datafile", "where", "name='" + browserPath + "'", "get",
                    "Version", "/value");
            if (!isNullOrEmpty(browserVersionOutput)) {
                return Optional
                        .of(getVersionFromWmicOutput(browserVersionOutput));
            }
        } else if (IS_OS_LINUX || IS_OS_MAC) {
            String browserPath;
            if (!isNullOrEmpty(browserBinaryPath)) {
                browserPath = browserBinaryPath;
            } else {
                browserPath = IS_OS_LINUX ? linuxBrowserName : macBrowserName;
            }
            String browserVersionOutput = runAndWait(browserPath, versionFlag);
            if (!isNullOrEmpty(browserVersionOutput)) {
                return Optional.of(getVersionFromPosixOutput(
                        browserVersionOutput, browserNameInOutput));
            }
        }
        return empty();
    }

    protected File getExecFile() {
        String systemRoot = System.getenv("SystemRoot");
        File system32 = new File(systemRoot, "System32");
        if (IS_OS_WINDOWS && system32.exists() && system32.isDirectory()) {
            return system32;
        }
        return new File(".");
    }

    protected void reset() {
        config().reset();
        mirrorLog = false;
        listVersions = null;
        versionToDownload = null;
        forcedArch = false;
        forcedOs = false;
        retry = true;
        isLatest = true;
    }

    protected String getProgramFilesEnv() {
        return System.getProperty("os.arch").contains("64")
                ? "PROGRAMFILES(X86)"
                : "PROGRAMFILES";
    }

    protected URL getDriverUrlCkeckingMirror(URL url) {
        if (config().isUseMirror()) {
            Optional<URL> mirrorUrl = getMirrorUrl();
            if (mirrorUrl.isPresent()) {
                return mirrorUrl.get();
            }
        }
        return url;
    }

    private static void resolveLocal(String validBrowsers, String arg) {
        LOGGER.info("Using WebDriverManager to resolve {}", arg);
        try {
            DriverManagerType driverManagerType = DriverManagerType
                    .valueOf(arg.toUpperCase());
            BrowserDriverManager wdm = BrowserDriverManager
                    .getInstance(driverManagerType).avoidExport()
                    .targetPath(".").forceDownload();
            if (arg.equalsIgnoreCase("edge")
                    || arg.equalsIgnoreCase("iexplorer")) {
                wdm.operatingSystem(WIN);
            }
            wdm.avoidOutputTree().setup();
        } catch (Exception e) {
            LOGGER.error("Driver for {} not found (valid browsers {})", arg,
                    validBrowsers);
        }
    }

    private static void logCliError(String validBrowsers) {
        LOGGER.error("There are 3 options to run WebDriverManager CLI");
        LOGGER.error(
                "1. WebDriverManager used to resolve binary drivers locally:");
        LOGGER.error("\tWebDriverManager browserName");
        LOGGER.error("\t(where browserName={})", validBrowsers);

        LOGGER.error("2. WebDriverManager as a server:");
        LOGGER.error("\tWebDriverManager server <port>");
        LOGGER.error("\t(where default port is 4041)");

        LOGGER.error(
                "3. To clear previously resolved driver versions (as Java preferences):");
        LOGGER.error("\tWebDriverManager clear-preferences");
    }

    private void storeVersionToDownload(String version) {
        if (!version.isEmpty()) {
            if (version.startsWith(".")) {
                version = version.substring(1);
            }
            versionToDownload = version;
            if (isLatest && usePreferences() && !isNullOrEmpty(preferenceKey)) {
                preferences.putValueInPreferencesIfEmpty(preferenceKey,
                        version);
            }
        }
    }

    private void setConfig(Config config) {
        this.config = config;
    }

    public boolean canUseMirror() {
        return getMirrorUrl().isPresent();
    }
}
