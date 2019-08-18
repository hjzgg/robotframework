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

import com.wmh.robotframework.log.LoggerAdapter;
import org.rauschig.jarchivelib.Archiver;

import java.io.*;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.io.File.separator;
import static java.lang.Runtime.getRuntime;
import static java.nio.file.Files.*;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.io.FileUtils.*;
import static org.rauschig.jarchivelib.ArchiveFormat.TAR;
import static org.rauschig.jarchivelib.ArchiverFactory.createArchiver;
import static org.rauschig.jarchivelib.CompressionType.BZIP2;
import static org.rauschig.jarchivelib.CompressionType.GZIP;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Downloader class.
 *
 * @author Boni Garcia (boni.gg@gmail.com)
 * @since 1.0.0
 */
public class Downloader implements LoggerAdapter {

//    final Logger log = getLogger(lookup().lookupClass());

    DriverManagerType driverManagerType;
    HttpClient httpClient;
    Config config;

    public Downloader(DriverManagerType driverManagerType) {
        this.driverManagerType = driverManagerType;

        BrowserDriverManager browserDriverManager = BrowserDriverManager
                .getInstance(driverManagerType);
        config = browserDriverManager.config();
        httpClient = browserDriverManager.getHttpClient();
    }

    public synchronized String download(URL url, String version,
                                        String driverName) throws IOException, InterruptedException {
        File targetFile = getTarget(version, url);
        Optional<File> binary = checkBinary(driverName, targetFile);
        if (!binary.isPresent()) {
            binary = downloadAndExtract(url, targetFile);
        }
        return binary.get().toString();
    }

    public File getTarget(String version, URL url) {
        LOGGER.trace("getTarget {} {}", version, url);
        String zip = url.getFile().substring(url.getFile().lastIndexOf('/'));

        int iFirst = zip.indexOf('_');
        int iSecond = zip.indexOf('-');
        int iLast = zip.length();
        if (iFirst != zip.lastIndexOf('_')) {
            iLast = zip.lastIndexOf('_');
        } else if (iSecond != -1) {
            iLast = iSecond;
        }

        String folder = zip.substring(0, iLast).replace(".zip", "")
                .replace(".tar.bz2", "").replace(".tar.gz", "")
                .replace(".msi", "").replace(".exe", "")
                .replace("_", separator);
        String path = config.isAvoidOutputTree() ? getTargetPath() + zip
                : getTargetPath() + folder + separator + version + zip;
        String target = BrowserDriverManager.getInstance(driverManagerType)
                .preDownload(path, version);

        LOGGER.trace("Target file for URL {} version {} = {}", url, version,
                target);

        return new File(target);
    }

    public String getTargetPath() {
        String targetPath = config.getTargetPath();
        LOGGER.trace("Target path {}", targetPath);

        // Create repository folder if not exits
        File repository = new File(targetPath);
        if (!repository.exists()) {
            repository.mkdirs();
        }
        return targetPath;
    }

    private Optional<File> downloadAndExtract(URL url, File targetFile)
            throws IOException, InterruptedException {
        LOGGER.info("Downloading {}", url);
        File targetFolder = targetFile.getParentFile();
        File tempDir = createTempDirectory("").toFile();
        File temporaryFile = new File(tempDir, targetFile.getName());

        LOGGER.trace("Target folder {} ... using temporal file {}", targetFolder,
                temporaryFile);
        copyInputStreamToFile(httpClient.execute(httpClient.createHttpGet(url))
                .getEntity().getContent(), temporaryFile);

        File extractedFile = extract(temporaryFile);
        File resultingBinary = new File(targetFolder, extractedFile.getName());
        boolean binaryExists = resultingBinary.exists();

        if (!binaryExists || config.isOverride()) {
            if (binaryExists) {
                LOGGER.info("Overriding former binary {}", resultingBinary);
                deleteFile(resultingBinary);
            }
            moveFileToDirectory(extractedFile, targetFolder, true);
        }
        if (!config.isExecutable(resultingBinary)) {
            setFileExecutable(resultingBinary);
        }
        deleteFolder(tempDir);
        LOGGER.trace("Binary driver after extraction {}", resultingBinary);

        return of(resultingBinary);
    }

    private Optional<File> checkBinary(String driverName, File targetFile) {
        File parentFolder = targetFile.getParentFile();
        if (parentFolder.exists() && !config.isOverride()) {
            // Check if binary exits in parent folder and it is valid

            Collection<File> listFiles = listFiles(parentFolder, null, true);
            for (File file : listFiles) {
                if (file.getName().startsWith(driverName)
                        && config.isExecutable(file)) {
                    LOGGER.info("Using binary driver previously downloaded");
                    return of(file);
                }
            }
            LOGGER.trace("{} does not exist in cache", driverName);
        }
        return empty();
    }

    private File extract(File compressedFile)
            throws IOException, InterruptedException {
        String fileName = compressedFile.getName().toLowerCase();

        boolean extractFile = !fileName.endsWith("exe")
                && !fileName.endsWith("jar");
        if (extractFile) {
            LOGGER.info("Extracting binary from compressed file {}", fileName);
        }
        if (fileName.endsWith("tar.bz2")) {
            unBZip2(compressedFile);
        } else if (fileName.endsWith("tar.gz")) {
            unTarGz(compressedFile);
        } else if (fileName.endsWith("gz")) {
            unGzip(compressedFile);
        } else if (fileName.endsWith("msi")) {
            extractMsi(compressedFile);
        } else if (fileName.endsWith("zip")) {
            unZip(compressedFile);
        }

        if (extractFile) {
            deleteFile(compressedFile);
        }

        File result = BrowserDriverManager.getInstance(driverManagerType)
                .postDownload(compressedFile).getAbsoluteFile();
        LOGGER.trace("Resulting binary file {}", result);

        return result;
    }

    private void unZip(File compressedFile) throws IOException {
        File file = null;
        try (ZipFile zipFolder = new ZipFile(compressedFile)) {
            Enumeration<?> enu = zipFolder.entries();

            while (enu.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) enu.nextElement();

                String name = zipEntry.getName();
                long size = zipEntry.getSize();
                long compressedSize = zipEntry.getCompressedSize();
                LOGGER.trace("Unzipping {} (size: {} KB, compressed size: {} KB)",
                        name, size, compressedSize);

                file = new File(compressedFile.getParentFile(), name);
                if (!file.exists() || config.isOverride()) {
                    if (name.endsWith("/")) {
                        file.mkdirs();
                        continue;
                    }

                    File parent = file.getParentFile();
                    if (parent != null) {
                        parent.mkdirs();
                    }

                    try (InputStream is = zipFolder.getInputStream(zipEntry)) {
                        copyInputStreamToFile(is, file);
                    }
                    setFileExecutable(file);
                } else {
                    LOGGER.debug("{} already exists", file);
                }

            }
        }
    }

    private void unGzip(File archive) throws IOException {
        LOGGER.trace("UnGzip {}", archive);
        String fileName = archive.getName();
        int iDash = fileName.indexOf('-');
        if (iDash != -1) {
            fileName = fileName.substring(0, iDash);
        }
        int iDot = fileName.indexOf('.');
        if (iDot != -1) {
            fileName = fileName.substring(0, iDot);
        }
        File target = new File(archive.getParentFile(), fileName);

        try (GZIPInputStream in = new GZIPInputStream(
                new FileInputStream(archive))) {
            try (FileOutputStream out = new FileOutputStream(target)) {
                for (int c = in.read(); c != -1; c = in.read()) {
                    out.write(c);
                }
            }
        }

        if (!target.getName().toLowerCase().contains(".exe")
                && target.exists()) {
            setFileExecutable(target);
        }
    }

    private void unTarGz(File archive) throws IOException {
        Archiver archiver = createArchiver(TAR, GZIP);
        archiver.extract(archive, archive.getParentFile());
        LOGGER.trace("unTarGz {}", archive);
    }

    private void unBZip2(File archive) throws IOException {
        Archiver archiver = createArchiver(TAR, BZIP2);
        archiver.extract(archive, archive.getParentFile());
        LOGGER.trace("Unbzip2 {}", archive);
    }

    private void extractMsi(File msi) throws IOException, InterruptedException {
        File tmpMsi = new File(
                createTempDirectory("").toFile().getAbsoluteFile() + separator
                        + msi.getName());
        move(msi.toPath(), tmpMsi.toPath());
        LOGGER.trace("Temporal msi file: {}", tmpMsi);

        Process process = getRuntime().exec(new String[]{"msiexec", "/a",
                tmpMsi.toString(), "/qb", "TARGETDIR=" + msi.getParent()});
        try {
            process.waitFor();
        } finally {
            process.destroy();
        }

        deleteFolder(tmpMsi.getParentFile());
    }

    protected void setFileExecutable(File file) {
        LOGGER.trace("Setting file {} as executable", file);
        if (!file.setExecutable(true)) {
            LOGGER.warn("Error setting file {} as executable", file);
        }
    }

    protected void renameFile(File from, File to) {
        LOGGER.trace("Renaming file from {} to {}", from, to);
        if (to.exists()) {
            deleteFile(to);
        }
        if (!from.renameTo(to)) {
            LOGGER.warn("Error renaming file from {} to {}", from, to);
        }
    }

    protected void deleteFile(File file) {
        LOGGER.trace("Deleting file {}", file);
        try {
            delete(file.toPath());
        } catch (IOException e) {
            throw new WebDriverManagerException(e);
        }
    }

    protected void deleteFolder(File folder) {
        assert folder.isDirectory();
        LOGGER.trace("Deleting folder {}", folder);
        try {
            deleteDirectory(folder);
        } catch (IOException e) {
            throw new WebDriverManagerException(e);
        }
    }

}
