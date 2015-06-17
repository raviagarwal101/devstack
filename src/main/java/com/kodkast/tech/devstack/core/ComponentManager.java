package com.kodkast.tech.devstack.core;

import com.kodkast.tech.devstack.downloader.Downloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/*
    gets request for installing a module
    from the module definition, detects it has a dynamic dependency on os
    downloads os specific dependency along with main module

 */

public class ComponentManager {

    private Set<String> componentInstallSet = new HashSet<String>();
    private Set<String> componentsWaitingForDependencies = new HashSet<String>();
    private List<ComponentConfig> componentInstallOrder = new ArrayList<ComponentConfig>();
    private Set<String> finalInstallSet = new HashSet<String>();
    private List<ComponentConfig> finalInstallList = new ArrayList<ComponentConfig>();
    private boolean debugMode = false;
    private Set<String> moduleDownloaded = new HashSet<String>();
    private String modulePath;
    private DevStackConfig devStackConfig;
    private Downloader downloader;

    // configurable values
    private String moduleCacheExpire;
    private String customModulesPath;

    private static Logger logger = LoggerFactory.getLogger(ComponentManager.class);

    public ComponentManager(DevStackConfig devStackConfig) {

        this.devStackConfig = devStackConfig;

        modulePath = devStackConfig.getModulesLocalPath();

        moduleCacheExpire = devStackConfig.getModulesCacheExpire();

        customModulesPath = devStackConfig.getCustomModulesPath();
        addCustomModulesPath();
    }

    public ComponentManager withDownloader(Downloader downloader) {
        this.downloader = downloader;
        return this;
    }

    public void setDebugMode() {
        debugMode = true;
        modulePath = new File(devStackConfig.getModulesLocalRelativePath()).getAbsolutePath();
        addCustomModulesPath();
    }

    public void addCustomModulesPath() {
        if(customModulesPath != null) {
            modulePath = modulePath + ":" + new File(customModulesPath).getAbsolutePath();
        }
    }

    public String getModulePath() {
        return modulePath;
    }

    public ComponentConfig getComponentConfigs(String componentName) {

        InputStream metaFileStream = null;
        InputStream settingsFileStream = null;
        String moduleMetaFile = null;
        String moduleSettingFile = null;
        String modulesPath = null;

        if(debugMode) {
            modulesPath = devStackConfig.getModulesLocalRelativePath();
            File file = new File(modulesPath + "/" + componentName);
            if(!file.exists()) {
                logger.info("Component " + componentName + " not found in Devstack local repo");
                if(customModulesPath != null) {
                    logger.info("Searching in custom modules");
                    modulesPath = customModulesPath;
                }
            }
        }
        else {
            modulesPath = devStackConfig.getModulesLocalPath();

            boolean moduleDownloaded = downloadModuleIfNeeded(componentName);
            if (!moduleDownloaded) {
                logger.info("Component " + componentName + " not found in Devstack online repo");
                if(customModulesPath != null) {
                    logger.info("Searching in custom modules");
                    modulesPath = customModulesPath;
                }
            }
        }

        moduleMetaFile = modulesPath + "/" + componentName + "/" + devStackConfig.getModuleMetaFilename();
        moduleSettingFile = modulesPath + "/" + componentName + "/" + devStackConfig.getModuleSettingsFile();

        try {
            metaFileStream = new FileInputStream(moduleMetaFile);
            settingsFileStream = new FileInputStream(moduleSettingFile);
        } catch (FileNotFoundException e) {
            exitWithError(componentName);
        }

        Properties metaProperties = new Properties();
        Properties settingsProperties = new Properties();

        try {
            metaProperties.load(metaFileStream);
            settingsProperties.load(settingsFileStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return createComponentConfig(componentName, metaProperties, settingsProperties);
    }

    private void exitWithError(String componentName) {
        logger.error("Component " + componentName + " not found");
        System.exit(1);
    }

    private boolean downloadModuleIfNeeded(String componentName) {

        if(!moduleDownloaded.contains(componentName)) {
            boolean downloaded = downloader.downloadComponent(componentName);
            if(downloaded) {
                moduleDownloaded.add(componentName);
            }
            else {
                return false;
            }
        }

        return true;
    }

    private ComponentConfig createComponentConfig(String componentName, Properties metaProperties, Properties settingsProperties) {

        ComponentConfig componentConfig = new ComponentConfig(componentName);

        // add default dependencies
        if(!componentName.equals(DevStackConfigs.COMPONENT_CORE_UTILS)) {
            componentConfig.addDependency(DevStackConfigs.COMPONENT_CORE_UTILS);
        }

        String dependenciesProperty = metaProperties.getProperty("dependencies");
        if(dependenciesProperty != null) {
            String[] dependencyList = dependenciesProperty.split(",");
            for(String dependency : dependencyList) {
                if(dependency.trim().length() > 0) {
                    componentConfig.addDependency(dependency.trim());
                }
            }
        }

        String dynamicDependenciesProperty = metaProperties.getProperty("dynamic-dependencies");
        if(dynamicDependenciesProperty != null) {
            OSUtils.OS_NAME osName = OSUtils.getOperatingSystemName();
            logger.info("Detected OS as " + osName);
            String osSpecificModuleName = componentName + "_" + osName;
            logger.info("Adding " + osSpecificModuleName + " as dynamic dependency");
            String[] dependencyList = dynamicDependenciesProperty.split(",");
            for(String dependency : dependencyList) {
                if(dependency.trim().length() > 0 && osSpecificModuleName.equals(dependency.trim())) {
                    downloadModuleIfNeeded(osSpecificModuleName);
                    componentConfig.setDynamicDependency(osSpecificModuleName);
                }
            }
        }

        Enumeration e = settingsProperties.propertyNames();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String val = settingsProperties.getProperty(key);
            componentConfig.setConfig(key, val);
        }

        return componentConfig;
    }

    public List<ComponentConfig> getInstallOrder(List<String> components) {

        finalInstallSet = new HashSet<String>();
        finalInstallList = new ArrayList<ComponentConfig>();

        for (String component : components) {
            List<ComponentConfig> installOrder = getInstallOrder(component);
            for (ComponentConfig comp : installOrder) {
                addToFinalList(comp);
            }
        }

        return finalInstallList;
    }

    public List<ComponentConfig> getInstallOrder(String componentName) {

        createInstallOrder(componentName);

        List<ComponentConfig> installOrder = new ArrayList<>(componentInstallOrder);

        componentInstallOrder = new ArrayList<>();
        componentInstallSet = new HashSet<>();

        return installOrder;
    }

    private void addToFinalList(ComponentConfig componentConfig) {

        if(!finalInstallSet.contains(componentConfig.getName())) {
            finalInstallList.add(componentConfig);
            finalInstallSet.add(componentConfig.getName());
        }
    }

    private void createInstallOrder(String componentName) {

        ComponentConfig componentConfig = getComponentConfigs(componentName);

        // check if component is already in install list
        if(!componentInstallSet.contains(componentName)) {

            // mark this component as waiting for dependencies
            if(!componentsWaitingForDependencies.contains(componentName)) {
                componentsWaitingForDependencies.add(componentName);
            }

            // first install all its dependencies
            for(String dependencyName : componentConfig.getDependencies()) {

                if (!componentsWaitingForDependencies.contains(dependencyName)) {
                    createInstallOrder(dependencyName);
                }
                else {
                    throw new IllegalStateException("Detected cyclic dependencies");
                }
            }

            // now install the component
            componentInstallOrder.add(componentConfig);

            // add this component to install set
            componentInstallSet.add(componentName);

            // remove marked component as it is not in install list
            componentsWaitingForDependencies.remove(componentName);
        }
    }
}
