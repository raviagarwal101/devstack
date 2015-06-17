package com.kodkast.tech.devstack.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DevStackConfigReader {

    private List<Component> components;
    private Map<String, Component> componentMap;

    private static final String DEVSTACK_MODULE = "devstack.module";

    public DevStackConfigReader() {
        components = new ArrayList<>();
        componentMap = new HashMap<>();
    }

    public void parseStackConfigurations(String configFile) {

        try {
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if(line.length() > 0) {
                    parseLine(line);
                }
            }
            reader.close();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("exception caught");
        }

    }

    private void parseLine(String line) {
        line = line.trim();
        if(!line.startsWith("#")) {
            String tokens[] = line.split("=", 2);
            if(tokens.length == 2) {
                String key = tokens[0].trim();
                String val = tokens[1].trim();
                if(key.equals(DEVSTACK_MODULE)) {
                    Component component = new Component(val);
                    componentMap.put(val, component);
                }
                else {
                    String subTokens[] = key.split("\\.", 2);
                    if(subTokens.length == 2) {
                        Component component = componentMap.get(subTokens[0]);
                        component.setConfig(subTokens[1], val);
                        componentMap.put(subTokens[0], component);
                    }
                }
            }
        }
    }

    public List<Component> getComponents() {
        components = new ArrayList<>(componentMap.values());
        return components;
    }

}
