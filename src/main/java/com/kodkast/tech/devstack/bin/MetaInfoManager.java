package com.kodkast.tech.devstack.bin;

import com.kodkast.tech.common.CommonUtils;
import com.kodkast.tech.devstack.core.DevStackConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class MetaInfoManager {

    private TreeMap<String, Properties> moduleProperties = new TreeMap<String, Properties>();
    private static final String INDENT = " - ";
    private static final String NEW_LINE = "\n";
    private DevStackConfig devStackConfig;

    public MetaInfoManager(DevStackConfig devStackConfig) {
        this.devStackConfig = devStackConfig;
    }

    public void createMetaInfo() {
        scanModules();
        createMetaFile();
    }

    public void scanModules() {

        String path = devStackConfig.getModulesLocalRelativePath();

        File file = new File(path);
        if(file.exists()) {
            String[] files = file.list();
            for(String moduleName : files) {
                String metaFileName = path + "/" + moduleName + "/" + devStackConfig.getModuleMetaFilename();

                File metaFile = new File(metaFileName);
                if(metaFile.exists()) {

                    try {
                        String moduleSettingsFile = path + "/" + moduleName + "/" + devStackConfig.getModuleSettingsFile();
                        InputStream in = new FileInputStream(moduleSettingsFile);

                        Properties properties = new Properties();
                        properties.load(in);

                        moduleProperties.put(moduleName, properties);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void createMetaFile() {

        StringBuilder builder = new StringBuilder();

        for(Map.Entry<String, Properties> entry : moduleProperties.entrySet()) {

            String moduleName = entry.getKey();
            Properties properties = entry.getValue();

            builder.append(NEW_LINE).append(moduleName).append(NEW_LINE);

            Enumeration e = properties.propertyNames();

            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String val = properties.getProperty(key);
                builder.append(INDENT).append(key + " : " + val).append(NEW_LINE);
            }
        }

        CommonUtils.createFile(devStackConfig.getMetaInfoFile(), builder.toString(), "755");
    }

    public static void main(String args[]) {
        MetaInfoManager metaInfoManager = new MetaInfoManager(new DevStackConfig());
        metaInfoManager.createMetaInfo();
    }
}
