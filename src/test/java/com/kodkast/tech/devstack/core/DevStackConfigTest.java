package com.kodkast.tech.devstack.core;

import org.junit.Test;

public class DevStackConfigTest {

    @Test
    public void testGetDefaultConfigs() {

        DevStackConfig devStackConfig = new DevStackConfig();

        String puppetSiteFilename = devStackConfig.getPuppetSiteFilename();
        System.out.println(puppetSiteFilename);
    }

    @Test
    public void testGetFinalConfigs() {

        String configFile = "/tmp/devstack.conf";
        DevStackConfig devStackConfig = new DevStackConfig(configFile);

        String customModulesPath = devStackConfig.getCustomModulesPath();
        System.out.println(customModulesPath);
    }
}
