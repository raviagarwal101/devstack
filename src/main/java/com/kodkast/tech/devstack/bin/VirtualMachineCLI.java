package com.kodkast.tech.devstack.bin;

import com.kodkast.tech.devstack.workinprogress.VMConfig;
import com.kodkast.tech.devstack.workinprogress.VagrantFileCreator;
import com.kodkast.tech.devstack.workinprogress.VagrantMachineManager;
import com.kodkast.tech.devstack.workinprogress.VirtualMachineManager;
import com.kodkast.tech.devstack.workinprogress.VmConfigReader;

public class VirtualMachineCLI {

    public static void main(String args[]) {

        if(args.length != 3) {
            System.out.println("usage : VirtualMachineCreator <action> <vm-config> <stack-config>");
            System.exit(1);
        }

        String action = args[0];
        String vmConfigFile = args[1];
        String stackConfigFile = args[2];

        // read vm config file
        VmConfigReader vmConfigReader = VmConfigReader.INSTANCE;
        vmConfigReader.readConfig(vmConfigFile);
        VMConfig vmConfig = vmConfigReader.getVmConfig();

        // create virtual machine manager
        VirtualMachineManager virtualMachineManager = new VagrantMachineManager()
            .withVagrantFileCreator(new VagrantFileCreator())
            .withDevstackConfigFile(stackConfigFile);

        // prepare and bootstrap machine
        virtualMachineManager.prepareVirtualMachine(vmConfig);
        virtualMachineManager.bootstrap();
    }
}
