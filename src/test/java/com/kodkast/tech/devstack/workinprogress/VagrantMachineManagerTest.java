package com.kodkast.tech.devstack.workinprogress;

import org.junit.BeforeClass;
import org.junit.Test;

public class VagrantMachineManagerTest {

    private static VagrantMachineManager vagrantMachineManager;

    @BeforeClass
    public static void setup() {
        VagrantFileCreator vagrantFileCreator = new VagrantFileCreator();
        vagrantMachineManager = new VagrantMachineManager()
            .withVagrantFileCreator(vagrantFileCreator);
    }

    @Test
    public void testGetVMDir() {
        String dir = vagrantMachineManager.getVirtualMachineDir();
        System.out.println(dir);
    }

    @Test
    public void testCreateVM() {

        VMConfig config = new VMConfig.Builder("trial", "trial.devstack.com")
            .build();

        vagrantMachineManager.prepareVirtualMachine(config);
    }

    @Test
    public void testCreateVMFromConfigFile() {

        String configFile = "src/test/resources/vm.conf";
        String stackConfigFile = "src/test/resources/mystack.conf";

        VmConfigReader vmConfigReader = VmConfigReader.INSTANCE;
        vmConfigReader.readConfig(configFile);

        VMConfig vmConfig = vmConfigReader.getVmConfig();

        VirtualMachineManager virtualMachineManager = new VagrantMachineManager()
            .withVagrantFileCreator(new VagrantFileCreator())
            .withDevstackConfigFile(stackConfigFile);

        virtualMachineManager.prepareVirtualMachine(vmConfig);
        virtualMachineManager.bootstrap();
    }
}
