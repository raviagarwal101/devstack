package com.kodkast.tech.devstack.core;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ComponentInstallerTest {

    private static ComponentInstaller componentInstaller;

    @BeforeClass
    public static void setup() {
        componentInstaller = new ComponentInstaller(new DevStackConfig());
    }

    @Test
    public void testInstallComponent() {

        List<ComponentConfig> componentConfigList = new ArrayList<ComponentConfig>();

        ComponentConfig config = new ComponentConfig("maven");
        config.setConfig("java_home", "abcd");
        componentConfigList.add(config);

        String file = componentInstaller.getPuppetManifestFile(componentConfigList, DevStackConfigs.INSTALL);

        System.out.println(file);
    }

    @Test
    public void testInstallDynamicDependencyModule() {

        ComponentManager componentManager = new ComponentManager(new DevStackConfig());
        ComponentConfig componentConfig = componentManager.getComponentConfigs("apache-httpd");

        System.out.println(componentConfig);

        List<ComponentConfig> componentConfigList = new ArrayList<ComponentConfig>();
        componentConfigList.add(componentConfig);

        String file = componentInstaller.getPuppetManifestFile(componentConfigList, DevStackConfigs.INSTALL);

        System.out.println(file);
    }

    @Test
    public void testInstallSomething() {

        List<ComponentConfig> componentConfigList = new ArrayList<ComponentConfig>();

        ComponentConfig config1 = new ComponentConfig("java");
        config1.setConfig("java_home", "abcd");
        componentConfigList.add(config1);

        ComponentConfig config2 = new ComponentConfig("hadoop");
        config2.setConfig("hadoop_root", "xyz");
        componentConfigList.add(config2);

        componentInstaller.getPuppetManifestFile(componentConfigList, DevStackConfigs.INSTALL);
    }
}
