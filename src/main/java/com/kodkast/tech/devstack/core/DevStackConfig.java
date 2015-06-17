package com.kodkast.tech.devstack.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DevStackConfig {

    private InputStream inputStream;
    private static final String defaultConfigFile = "devstack.conf";
    private Map<String, String> configMap = new HashMap<>();

    private String baseSvnPath;
    private String modulesSvnPath;
    private String metaInfoSvnPath;

    private String customModulesPath;
    private String modulesCacheExpire;
    private String puppetSiteFilename;

    private String modulesLocalPath;
    private String metaInfoLocalPath;
    private String moduleSettingsFile;
    private String moduleMetaFilename;

    private String modulesLocalRelativePath;
    private String metaInfoLocalRelativePath;
    private String metaInfoFilename;
    private String metaInfoFile;

    public DevStackConfig() {
        readDefaultConfigFile();
        initializeConfigs();
    }

    public DevStackConfig(String configFile) {
        readDefaultConfigFile();
        readGivenConfigFile(configFile);
        initializeConfigs();
    }

    private void readDefaultConfigFile() {

        try {

            inputStream = DevStackConfig.class.getClassLoader().getResourceAsStream(defaultConfigFile);
            if (inputStream == null) {
                throw new FileNotFoundException("Missing devstack config file " + defaultConfigFile);
            }

            createConfigMap(inputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void readGivenConfigFile(String configFile) {

        try {

            inputStream = new FileInputStream(configFile);

            createConfigMap(inputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initializeConfigs() {

        // set tmp workspace dir
        String tmpWorkspaceDir = getRequiredConfig("tmp-workspace-dir");

        // set modules local path
        modulesLocalPath = tmpWorkspaceDir + "/components";

        // set meta info local path
        metaInfoLocalPath = tmpWorkspaceDir + "/meta";

        // set arkisto path
        baseSvnPath = getRequiredConfig("arkisto-path");

        // set modules svn path
        modulesSvnPath = baseSvnPath + "/components";

        // set meta files svn path
        metaInfoSvnPath = baseSvnPath + "/meta";

        // set custom modules path
        customModulesPath = getOptionalConfig("custom-modules-path");

        // set modules cache expire time
        modulesCacheExpire = getRequiredConfig("modules-cache-expire");

        // set puppet site filename
        puppetSiteFilename = getRequiredConfig("puppet-site-filename");

        // set modules local relative path
        modulesLocalRelativePath = "arkisto/components";

        // set meta info local relative path
        metaInfoLocalRelativePath = "arkisto/meta";

        // set meta info filename
        metaInfoFilename = "devstack.json";

        // set meta info file
        metaInfoFile = metaInfoLocalRelativePath + "/" + metaInfoFilename;

        // set module settings file
        moduleSettingsFile = "settings.conf";

        // set module meta file name
        moduleMetaFilename = "meta.conf";
    }

    private void createConfigMap(InputStream inputStream) {

        try {
            Properties properties = new Properties();
            properties.load(inputStream);

            Enumeration e = properties.propertyNames();

            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String val = properties.getProperty(key);
                if (val != null && val.length() > 0) {
                    configMap.put(key, val);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getOptionalConfig(String configName) {
        return configMap.get(configName);
    }

    private String getRequiredConfig(String configName) {
        String configValue = configMap.get(configName);
        if(configValue == null || configValue.trim().length() == 0) {
            throw new IllegalArgumentException("Missing " + configName + " devstack config");
        }
        else {
            return replaceEnvVariables(configValue);
        }
    }

    private String replaceEnvVariables(String str) {

        String finalValue = str;

        if(finalValue.contains("$HOME")) {
            String home = System.getenv("HOME");
            finalValue = finalValue.replaceAll("\\$HOME", home);
        }

        return finalValue;
    }

    public String getCustomModulesPath() {
        return customModulesPath;
    }

    public String getModulesCacheExpire() {
        return modulesCacheExpire;
    }

    public String getPuppetSiteFilename() {
        return puppetSiteFilename;
    }

    public String getModulesLocalPath() {
        return modulesLocalPath;
    }

    public String getMetaInfoLocalPath() {
        return metaInfoLocalPath;
    }

    public String getModulesSvnPath() {
        return modulesSvnPath;
    }

    public String getMetaInfoSvnPath() {
        return metaInfoSvnPath;
    }

    public String getModulesLocalRelativePath() {
        return modulesLocalRelativePath;
    }

    public String getMetaInfoLocalRelativePath() {
        return metaInfoLocalRelativePath;
    }

    public String getMetaInfoFilename() {
        return metaInfoFilename;
    }

    public String getMetaInfoFile() {
        return metaInfoFile;
    }

    public String getModuleSettingsFile() {
        return moduleSettingsFile;
    }

    public String getModuleMetaFilename() {
        return moduleMetaFilename;
    }
}
