package com.wmh.robotframework.config;

import com.wmh.robotframework.browser.DriverManagerType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "wdm")
public class BrowserExportProperties {
    private String chromeDriverExport;
    private String geckoDriverExport;
    private String operaDriverExport;
    private String phantomjsDriverExport;
    private String edgeDriverExport;
    private String internetExplorerDriverExport;


    public String resovleExportPath(DriverManagerType driverManagerType) {
        switch (driverManagerType) {
            case CHROME:
                return this.chromeDriverExport;
            case EDGE:
                return this.edgeDriverExport;
            case OPERA:
                return this.operaDriverExport;
            case FIREFOX:
                return this.geckoDriverExport;
            case IEXPLORER:
                return this.internetExplorerDriverExport;
            case PHANTOMJS:
                return phantomjsDriverExport;
            default:
                return StringUtils.EMPTY;
        }
    }
}
