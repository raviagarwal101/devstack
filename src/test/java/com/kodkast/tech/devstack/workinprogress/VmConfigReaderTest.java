package com.kodkast.tech.devstack.workinprogress;

import org.junit.BeforeClass;
import org.junit.Test;

public class VmConfigReaderTest {

    private static VmConfigReader vmConfigReader;

    @BeforeClass
    public static void setup() {
        vmConfigReader = VmConfigReader.INSTANCE;
    }

    @Test
    public void testReadConfig() {

        String configFile = "src/test/resources/vm.conf";

        vmConfigReader.readConfig(configFile);

        VMConfig vmConfig = vmConfigReader.getVmConfig();

        System.out.println("vm puppet : " + vmConfig.hasPuppet());
        System.out.println("vm os : " + vmConfig.getOs());
    }
}
