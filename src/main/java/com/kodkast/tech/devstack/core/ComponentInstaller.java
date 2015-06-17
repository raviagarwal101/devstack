package com.kodkast.tech.devstack.core;

import com.kodkast.tech.common.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ComponentInstaller {

    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String SEMI_COLON = ";";
    private static final String NEW_LINE = "\n";
    private static final String filePermissions = "777";

    // configurable values
    private String puppetSiteFile;

    private static Logger logger = LoggerFactory.getLogger(ComponentInstaller.class);

    public ComponentInstaller(DevStackConfig devStackConfig) {
        puppetSiteFile = devStackConfig.getPuppetSiteFilename();
    }

    public String getPuppetManifestFile(List<ComponentConfig> componentConfigs, String action) {

        // create puppet code
        String puppetCode = createPuppetCode(componentConfigs, action);

        // create manifest file
        CommonUtils.createFile(puppetSiteFile, puppetCode, filePermissions);

        return puppetSiteFile;
    }

    public String createPuppetCode(List<ComponentConfig> componentConfigs, String action) {

        StringBuilder builder = new StringBuilder();
        String prevComponent = null;

        if(componentConfigs.size() > 0) {
            ComponentConfig firstComponent = componentConfigs.get(0);
            String puppetCode = getModuleInvocationCode(firstComponent, action, null);
            builder.append(puppetCode);
            prevComponent = getComponentName(firstComponent);
        }

        for (int i = 1; i < componentConfigs.size(); i++) {
            ComponentConfig componentConfig = componentConfigs.get(i);
            String puppetCode = getModuleInvocationCode(componentConfig, action, prevComponent);
            builder.append(puppetCode);
            prevComponent = getComponentName(componentConfig);
        }

        return builder.toString();
    }

    private String getComponentName(ComponentConfig componentConfig) {

        // use dynamic component if set
        String componentName = componentConfig.getName();

        if(componentConfig.hasDynamicDependencies()) {
            componentName = componentConfig.getDynamicDependency();
        }

        return componentName;
    }

    private String getModuleInvocationCode(ComponentConfig componentConfig, String action, String serialDependency) {

        String componentName = getComponentName(componentConfig);

        // set all configs before invoking module class
        StringBuilder str = new StringBuilder("\n");
        Map<String, String> configs = componentConfig.getFinalConfigs();
        for(Map.Entry<String, String> mapEntry : configs.entrySet()) {
            String configKey = mapEntry.getKey();
            String configVal = mapEntry.getValue();
            str.append("$" + configKey + " = \"" + configVal + "\"").append("\n");
        }

        // add a blank line if there any configs set before
        if(configs.size() > 0) {
            str.append("\n");
        }

        // invoke module class
        str.append("class { '" + componentName + "'").append(COLON).append(NEW_LINE);
        str.append("    action      => '" + action + "'").append(COMMA).append(NEW_LINE);
        if(serialDependency != null) {
            str.append("    require     => Class['" + serialDependency + "']").append(SEMI_COLON).append(NEW_LINE);
        }
        str.append("}").append("\n");

        return str.toString();
    }
}
