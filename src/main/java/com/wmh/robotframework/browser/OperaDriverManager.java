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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import static com.wmh.robotframework.browser.DriverManagerType.OPERA;


/**
 * Manager for Opera.
 *
 * @author Boni Garcia (boni.gg@gmail.com)
 * @since 1.0.0
 */
public class OperaDriverManager extends BrowserDriverManager {

    @Override
    protected DriverManagerType getDriverManagerType() {
        return OPERA;
    }

    @Override
    protected String getDriverName() {
        return "operadriver";
    }

    @Override
    protected String getDriverVersion() {
        return config().getOperaDriverVersion();
    }

    @Override
    protected URL getDriverUrl() {
        return getDriverUrlCkeckingMirror(config().getOperaDriverUrl());
    }

    @Override
    protected Optional<URL> getMirrorUrl() {
        return Optional.of(config().getOperaDriverMirrorUrl());
    }

    @Override
    protected Optional<String> getExportParameter() {
        return Optional.of(config().getOperaDriverExport());
    }

    @Override
    protected void setDriverVersion(String version) {
        config().setOperaDriverVersion(version);
    }

    @Override
    protected void setDriverUrl(URL url) {
        config().setOperaDriverUrl(url);
    }

    @Override
    protected String getCurrentVersion(URL url, String driverName) {
        if (isUsingTaobaoMirror()) {
            int i = url.getFile().lastIndexOf(SLASH);
            int j = url.getFile().substring(0, i).lastIndexOf(SLASH) + 1;
            return url.getFile().substring(j, i);
        } else {
            return url.getFile().substring(
                    url.getFile().indexOf(SLASH + "v") + 2,
                    url.getFile().lastIndexOf(SLASH));
        }
    }

    @Override
    protected List<URL> getDrivers() throws IOException {
        return getDriversFromGitHub();
    }

    @Override
    protected File postDownload(File archive) {
        LOGGER.trace("Post processing for Opera: {}", archive);

        File extractFolder = archive.getParentFile()
                .listFiles(getFolderFilter())[0];
        if (!extractFolder.isFile()) {
            File target;
            try {
                LOGGER.trace("Opera extract folder (to be deleted): {}",
                        extractFolder);
                File[] listFiles = extractFolder.listFiles();
                int i = 0;
                File operadriver;
                boolean isOperaDriver;
                do {
                    if (i >= listFiles.length) {
                        throw new WebDriverManagerException(
                                "Driver binary for Opera not found in zip file");
                    }
                    operadriver = listFiles[i];
                    isOperaDriver = config().isExecutable(operadriver)
                            && operadriver.getName().contains(getDriverName());
                    i++;
                    LOGGER.trace("{} is valid: {}", operadriver, isOperaDriver);
                } while (!isOperaDriver);
                LOGGER.info("Operadriver binary: {}", operadriver);

                target = new File(archive.getParentFile().getAbsolutePath(),
                        operadriver.getName());
                LOGGER.trace("Operadriver target: {}", target);

                downloader.renameFile(operadriver, target);
            } finally {
                downloader.deleteFolder(extractFolder);
            }
            return target;
        } else {
            return super.postDownload(archive);
        }
    }

    @Override
    protected Optional<String> getBrowserVersion() {
        return getDefaultBrowserVersion("PROGRAMFILES",
                "\\\\Opera\\\\launcher.exe", "opera",
                "/Applications/Opera.app/Contents/MacOS/Opera", "--version",
                "");
    }

}
