package com.kodkast.tech.devstack.core;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComponentConfig {

    private String name;
    private List<String> dependencies;
    private String dynamicDependency;
    private Set<String> dependencySet;
    private Map<String, String> optionalConfigs;
    private Set<String> requiredConfigs;
    private Map<String, String> finalConfigs;
    private Set<String> supportedOperatingSystems;

    private static final String SET_ME = "SET_ME";

    public ComponentConfig(String name) {
        this.name = name;
        this.dependencies = new ArrayList<String>();
        this.dependencySet = new HashSet<String>();
        this.requiredConfigs = new HashSet<String>();
        this.optionalConfigs = new HashMap<String, String>();
        this.finalConfigs = new HashMap<String, String>();
        this.supportedOperatingSystems = new HashSet<String>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public String getDynamicDependency() {
        return dynamicDependency;
    }

    public void setDynamicDependency(String dynamicDependency) {
        this.dynamicDependency = dynamicDependency;
    }

    public void addDependency(String dependency) {
        if(!dependencySet.contains(dependency)) {
            dependencies.add(dependency);
            dependencySet.add(dependency);
        }
    }

    public boolean hasDependencies() {
        if(dependencies.size() > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean hasDynamicDependencies() {
        if(dynamicDependency != null) {
            return true;
        }
        else {
            return false;
        }
    }

    public void setConfig(String configName, String configValue) {
        if(configValue.equals(SET_ME) || configValue.length() == 0) {
            requiredConfigs.add(configName);
        }
        else {
            optionalConfigs.put(configName, configValue);
        }
    }

    public Map<String, String> getOptionalConfigs() {
        return optionalConfigs;
    }

    public Set<String> getRequiredConfigs() {
        return requiredConfigs;
    }

    public void setFinalConfigs(Map<String, String> finalConfigs) {

        for(Map.Entry<String, String> entry : finalConfigs.entrySet()) {
            String configName = entry.getKey();
            String configValue = entry.getValue();
            this.finalConfigs.put(configName, replaceEnvVariables(configValue));
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

    public Map<String, String> getFinalConfigs() {
        return finalConfigs;
    }

    public void addSupportedOperatingSystem(String os) {
        supportedOperatingSystems.add(os);
    }

    public boolean isOperatingSystemSupported(String os) {
        return supportedOperatingSystems.contains(os);
    }

    public Set<String> getSupportedOperatingSystems() {
        return supportedOperatingSystems;
    }

    public void setSupportedOperatingSystems(Set<String> supportedOperatingSystems) {
        this.supportedOperatingSystems = supportedOperatingSystems;
    }

    public String toString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
