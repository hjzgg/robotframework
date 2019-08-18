/*
 * (C) Copyright 2018 Boni Garcia (http://bonigarcia.github.io/)
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.BackingStoreException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.prefs.Preferences.userNodeForPackage;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Preferences class.
 *
 * @author Boni Garcia (boni.gg@gmail.com)
 * @since 3.0.0
 */
public class Preferences implements LoggerAdapter {

//    final Logger log = getLogger(lookup().lookupClass());

    static final String TTL = "-ttl";

    //windows系统注册表路径 HKEY_CURRENT_USER\Software\JavaSoft\Prefs
    java.util.prefs.Preferences prefs = userNodeForPackage(BrowserDriverManager.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Config config;

    public Preferences(Config config) {
        this.config = config;
    }

    public String getValueFromPreferences(String key) {
        return prefs.get(key, null);
    }

    private long getExpirationTimeFromPreferences(String key) {
        return prefs.getLong(getExpirationKey(key), 0);
    }

    public void putValueInPreferencesIfEmpty(String key, String value) {
        if (getValueFromPreferences(key) == null) {
            prefs.put(key, value);
            long expirationTime = new Date().getTime()
                    + SECONDS.toMillis(config.getTtl());
            prefs.putLong(getExpirationKey(key), expirationTime);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Storing preference {}={} (valid until {})", key,
                        value, formatTime(expirationTime));
            }
        }
    }

    private void clearFromPreferences(String key) {
        prefs.remove(key);
        prefs.remove(getExpirationKey(key));
    }

    public void clear() {
        try {
            LOGGER.info("Clearing WebDriverManager preferences");
            prefs.clear();
        } catch (BackingStoreException e) {
            LOGGER.warn("Exception clearing preferences", e);
        }
    }

    private boolean checkValidity(String key, String value,
                                  long expirationTime) {
        long now = new Date().getTime();
        boolean isValid = value != null && expirationTime != 0
                && expirationTime > now;
        if (!isValid) {
            String expirationDate = formatTime(expirationTime);
            LOGGER.debug("Removing preference {}={} (expired on {})", key, value,
                    expirationDate);
            clearFromPreferences(key);
        }
        return isValid;
    }

    private String formatTime(long time) {
        return dateFormat.format(new Date(time));
    }

    private String getExpirationKey(String key) {
        return key + TTL;
    }

    public boolean checkKeyInPreferences(String key) {
        String valueFromPreferences = getValueFromPreferences(key);
        boolean valueInPreferences = valueFromPreferences != null
                && !valueFromPreferences.isEmpty();
        if (valueInPreferences) {
            long expirationTime = getExpirationTimeFromPreferences(key);
            String expirationDate = formatTime(expirationTime);
            valueInPreferences &= checkValidity(key, valueFromPreferences,
                    expirationTime);
            if (valueInPreferences) {
                LOGGER.debug("Preference found {}={} (valid until {})", key,
                        valueFromPreferences, expirationDate);
            }
        }
        return valueInPreferences;
    }

}
