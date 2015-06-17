package com.kodkast.tech.devstack.core;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class DevStackConfigReaderTest {

    private static DevStackConfigReader reader;

    @BeforeClass
    public static void setUp() {
        reader = new DevStackConfigReader();
    }

    @Test
    public void testParseConfig() {

        String conf = "src/test/resources/mystack.conf";
        reader.parseStackConfigurations(conf);

        List<Component> moduleList = reader.getComponents();
        for(Component component : moduleList) {
            System.out.println(component.getName());
        }
    }

}



