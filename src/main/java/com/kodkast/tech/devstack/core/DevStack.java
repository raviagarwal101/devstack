package com.kodkast.tech.devstack.core;

import com.kodkast.tech.common.CommonUtils;
import com.kodkast.tech.devstack.downloader.Downloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DevStack {

    private ComponentManager componentManager;
    private ComponentInstaller componentInstaller;
    private DevStackConfigReader configReader;
    private DevStackConfig devStackConfig;
    private Downloader downloader;

    private boolean debugMode = false;
    private static final String filePermissions = "777";

    private static Logger logger = LoggerFactory.getLogger(DevStack.class);

    public DevStack(DevStackConfig devStackConfig) {
        this.devStackConfig = devStackConfig;
        configReader = new DevStackConfigReader();
    }

    public DevStack withComponentManager(ComponentManager componentManager) {
        this.componentManager = componentManager;
        return this;
    }

    public DevStack withComponentInstaller(ComponentInstaller componentInstaller) {
        this.componentInstaller = componentInstaller;
        return this;
    }

    public DevStack withDownloader(Downloader downloader) {
        this.downloader = downloader;
        return this;
    }

    public DevStack withDebugMode() {
        componentManager.setDebugMode();
        debugMode = true;
        return this;
    }

    public void process(String action, String configFile) {

        // get map of components
        Map<String, Component> componentMap = getComponentsMap(configFile);

        // get final configs for each component
        List<ComponentConfig> componentConfigs = getComponentConfigs(componentMap);

        // create actions scripts ready for execution
        createActionScripts(componentConfigs, action);
    }

    public Map<String, Component> getComponentsMap(String configFile) {

        // parse config file
        configReader.parseStackConfigurations(configFile);

        // get all components in the config
        List<Component> components = configReader.getComponents();

        // prepare map of components
        Map<String, Component> componentMap = new HashMap<>();
        for(Component component : components) {
            componentMap.put(component.getName(), component);
        }

        return componentMap;
    }

    public List<ComponentConfig> getComponentConfigs(Map<String, Component> componentsMap) {

        List<String> componentNames = new ArrayList<>(componentsMap.keySet());
        List<ComponentConfig> componentConfigs = componentManager.getInstallOrder(componentNames);

        for(ComponentConfig componentConfig : componentConfigs) {
            String componentName = componentConfig.getName();
            if(componentsMap.containsKey(componentName)) {
                Map<String, String> finalConfigs = getFinalConfigs(componentsMap.get(componentName), componentConfig);
                componentConfig.setFinalConfigs(finalConfigs);
            }
            else {
                componentConfig.setFinalConfigs(componentConfig.getOptionalConfigs());
            }
        }

        return componentConfigs;
    }

    public void createActionScripts(List<ComponentConfig> componentConfigs, String action) {

        String puppetManifestsFile = componentInstaller.getPuppetManifestFile(componentConfigs, action);

        createInstallerScript(puppetManifestsFile, action);
    }

    public void listInstalledComponents() {

        String path = devStackConfig.getModulesLocalRelativePath();
        if(!debugMode) {
            path = devStackConfig.getModulesLocalPath();
        }

        File file = new File(path);
        if(file.exists()) {
            String[] files = file.list();
            Collections.sort(Arrays.asList(files));
            for(String moduleName : files) {
                logger.info(moduleName);
            }
        }
    }

    public void listAllComponents() {

        // download meta info
        downloader.downloadMetaInfo();

        String metaFile = devStackConfig.getMetaInfoLocalPath() + "/" + devStackConfig.getMetaInfoFilename();
        String content = CommonUtils.readFileContents(metaFile);

        logger.info(content);
    }

    private Map<String, String> getFinalConfigs(Component component, ComponentConfig componentConfig) {

        Map<String, String> finalConfigs = new HashMap<String, String>();

        Set<String> requiredConfigs = componentConfig.getRequiredConfigs();
        Map<String, String> optionalConfigs = componentConfig.getOptionalConfigs();
        Set<String> givenConfigs = component.getGivenConfigs();

        //logger.info("Configs for " + component.getName());
        //logger.info("Required configs : " + requiredConfigs);
        //logger.info("Optional configs : " + optionalConfigs);
        //logger.info("Given configs : " + component.getConfigs());

        for(String requiredConfig : requiredConfigs) {
            if(!givenConfigs.contains(requiredConfig)) {
                throw new IllegalArgumentException("Missing required config " + requiredConfig);
            }
            else {
                String givenConfigValue = component.getConfig(requiredConfig);
                finalConfigs.put(requiredConfig, givenConfigValue);
                givenConfigs.remove(requiredConfig);
            }
        }

        for(Map.Entry<String, String> entry : optionalConfigs.entrySet()) {
            String optionalConfigName = entry.getKey();
            String optionalConfigDefaultValue = entry.getValue();
            if(givenConfigs.contains(optionalConfigName)) {
                String givenConfigValue = component.getConfig(optionalConfigName);
                finalConfigs.put(optionalConfigName, givenConfigValue);
            }
            else {
                finalConfigs.put(optionalConfigName, optionalConfigDefaultValue);
            }
            givenConfigs.remove(optionalConfigName);
        }

        for(String givenConfig : givenConfigs) {
            finalConfigs.put(givenConfig, component.getConfig(givenConfig));
        }

        return finalConfigs;
    }

    private void createInstallerScript(String puppetManifest, String action) {

        StringBuilder str = new StringBuilder();
        str.append("#!/bin/bash").append("\n");
        str.append("sudo /usr/bin/puppet apply ").append(puppetManifest);
        str.append(" --modulepath ").append(componentManager.getModulePath());
        str.append("\n");

        String content = str.toString();

        String file = "/tmp/" + action + "-modules.sh";
        CommonUtils.createFile(file, content, filePermissions);

        logger.info("Created run script at " + file);
    }
}
