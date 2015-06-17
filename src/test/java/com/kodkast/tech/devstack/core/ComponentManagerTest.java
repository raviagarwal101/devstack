package com.kodkast.tech.devstack.core;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ComponentManagerTest {

    private static ComponentManager componentManager;

    @BeforeClass
    public static void setUp() {
        componentManager = new ComponentManager(new DevStackConfig());
        componentManager.setDebugMode();
    }

    @Test
    public void testGetConfigs() {

        ComponentConfig componentConfig = componentManager.getComponentConfigs("apache-httpd");

        System.out.println(componentConfig);
    }

    @Test
    public void testGetModulePath() {

        String modulePath = componentManager.getModulePath();

        System.out.println(modulePath);

        componentManager.setDebugMode();

        modulePath = componentManager.getModulePath();

        System.out.println(modulePath);
    }

    @Test
    public void testGetInvalidComponentConfigs() {

        ComponentConfig componentConfig = componentManager.getComponentConfigs("non-existing-component");

        System.out.println(componentConfig.getName());
        System.out.println(componentConfig.getDependencies());
    }

    @Test
    public void testGetInstallOrder() {

        String componentName = "oozie";
        List<ComponentConfig> installOrder = componentManager.getInstallOrder(componentName);

        System.out.println(installOrder);

        componentName = "hive";
        installOrder = componentManager.getInstallOrder(componentName);

        System.out.println(installOrder);
    }

    @Test
    public void testGetInstallOrderForMultipleComponents() {

        List<String> components = new ArrayList<String>();
        //components.add("hadoop");
        components.add("ant");
        //components.add("maven");

        List<ComponentConfig> installOrder = componentManager.getInstallOrder(components);

        System.out.println(installOrder);
    }

}



