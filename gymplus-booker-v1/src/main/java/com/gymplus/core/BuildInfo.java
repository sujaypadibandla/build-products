package com.gymplus.core;

import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum BuildInfo {

    INSTANCE();

    public String APP_VERSION;

    public String APP_COMMIT_HASH;

    public String APP_BUILD_DATE;

    public String APP_REGION;

    BuildInfo() {
        try (InputStream is = BuildInfo.class.getClassLoader().getResourceAsStream("build-info.properties")) {
            Properties props = new Properties();

            props.load(is);

            APP_VERSION = props.getProperty("APP_VERSION");
            APP_COMMIT_HASH = props.getProperty("APP_COMMIT_HASH");
            APP_BUILD_DATE = props.getProperty("APP_BUILD_DATE");
            APP_REGION = props.getProperty("APP_REGION");

            LogManager.getLogger(BuildInfo.class).info("Build info loaded successfully from build-info.properties");
        } catch (IOException e) {
            LogManager.getLogger(BuildInfo.class).warn("Failed to load build-info.properties", e);
        }
    }

}
