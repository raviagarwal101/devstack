package com.kodkast.tech.devstack.core;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class DevStackTest {

    private static DevStack devStack;

    @BeforeClass
    public static void setup() {
        devStack = new DevStack(new DevStackConfig()).withDebugMode();
    }

    @Test
    public void testListComponents() {
        devStack.listInstalledComponents();
    }

    @Test
    public void testGetComponentsMap() {

        String configFile = "src/test/resources/mystack.conf";

        Map<String, Component> componentMap = devStack.getComponentsMap(configFile);

        for(Map.Entry<String, Component> entry : componentMap.entrySet()) {
            System.out.println(entry.getValue().getName());
            System.out.println(entry.getValue().getConfigs());
        }
    }

    @Test
    public void testGetComponentsConfig() {

        String configFile = "src/main/resources/example-stacks/ant.conf";

        Map<String, Component> componentMap = devStack.getComponentsMap(configFile);

        List<ComponentConfig> componentConfigs = devStack.getComponentConfigs(componentMap);

        for(ComponentConfig config : componentConfigs) {
            System.out.println(config.getFinalConfigs());
        }
    }

    @Test
    public void testCreateActionScripts() {

        String configFile = "src/main/resources/example-stacks/ant.conf";

        Map<String, Component> componentMap = devStack.getComponentsMap(configFile);
        List<ComponentConfig> componentConfigs = devStack.getComponentConfigs(componentMap);

        devStack.createActionScripts(componentConfigs, DevStackConfigs.INSTALL);
    }

    @Test
    public void testCreateActionScriptsForStart() {

        String configFile = "src/test/resources/mystack.conf";

        Map<String, Component> componentMap = devStack.getComponentsMap(configFile);
        List<ComponentConfig> componentConfigs = devStack.getComponentConfigs(componentMap);

        devStack.createActionScripts(componentConfigs, DevStackConfigs.START);
    }

    @Test
    public void testProcessForInstall() {

        String configFile = "src/test/resources/mystack.conf";

        devStack.process(DevStackConfigs.INSTALL, configFile);
    }

    @Test
    public void testProcessForStop() {

        String configFile = "src/test/resources/mystack.conf";

        devStack.process(DevStackConfigs.STOP, configFile);
    }
}
