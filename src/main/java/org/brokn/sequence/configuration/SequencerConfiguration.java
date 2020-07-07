/*
 *     Copyright (C) 2020 rsouth (https://github.com/rsouth)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.brokn.sequence.configuration;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.brokn.sequence.rendering.Canvas;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SequencerConfiguration {

    private static final Logger log = Logger.getLogger(Canvas.class.getName());

    public static final String CONFIG_FILE_NAME = "config.properties";
    public static final String KEY_WINDOW_X = "window.x";
    public static final String KEY_WINDOW_Y = "window.y";
    public static final String KEY_WINDOW_WIDTH = "window.width";
    public static final String KEY_WINDOW_HEIGHT = "window.height";
    public static final String KEY_DIVIDER_LOCATION = "window.divider-location";

    private Configuration config;

    public void loadConfigurations() {
        Configurations configs = new Configurations();
        try {
            File file = new File(CONFIG_FILE_NAME);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    log.severe("failed to create config properties file");
                }
            }

            // load the configuration
            FileBasedConfigurationBuilder<PropertiesConfiguration> configBuilder = configs.propertiesBuilder(file);
            configBuilder.setAutoSave(true);
            this.config = configBuilder.getConfiguration();

            // quick & dirty validation
            removeInvalidKeys();

        } catch (ConfigurationException e) {
            log.info("No configuration file found");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (this.config == null) {
            throw new IllegalStateException("App config was not succesfully loaded");
        }
    }

    private void removeInvalidKeys() {
        List<String> configKeys = Arrays.stream(this.getClass().getDeclaredFields())
                .filter(field -> field.getName().startsWith("KEY_"))
                .map(field -> {
                    String value = null;
                    try {
                        value = (String) field.get(null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return value;
                })
                .collect(Collectors.toList());

        List<String> fileList = new ArrayList<>();
        this.config.getKeys().forEachRemaining(fileList::add);

        // get list of keys in file, not in class
        fileList.removeAll(configKeys);

        fileList.forEach(key -> {
            this.config.clearProperty(key);
        });
    }

    public boolean containsKey(String key) {
        return this.config.containsKey(key);
    }

    public float getFloat(String key) {
        return this.config.getFloat(key);
    }

    public int getInt(String key) {
        return this.config.getInt(key);
    }

    public void setProperty(String key, Object value) {
        this.config.setProperty(key, value);
    }

    public Map<String, Object> getAll() {
        Map<String, Object> data = new HashMap<>();
        this.config.getKeys().forEachRemaining(key -> {
            data.put(key, this.config.getProperty(key));
        });

        return data;
    }

    public double getDouble(String key) {
        return this.config.getDouble(key);
    }
}
