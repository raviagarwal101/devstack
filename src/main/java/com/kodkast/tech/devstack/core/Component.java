package com.kodkast.tech.devstack.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Component {

    private String name;
    private Map<String, String> configs;

    public Component(String name) {
        this.name = name;
        configs = new HashMap<String, String>();
    }

    public void setConfig(String configKey, String configVal) {
        configs.put(configKey, configVal);
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getConfigs() {
        return configs;
    }

    public Set<String> getGivenConfigs() {
        return configs.keySet();
    }

    public String getConfig(String configKey) {
        return configs.get(configKey);
    }
}
