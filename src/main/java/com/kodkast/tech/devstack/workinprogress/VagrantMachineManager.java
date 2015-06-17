package com.kodkast.tech.devstack.workinprogress;

import com.kodkast.tech.common.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class VagrantMachineManager implements VirtualMachineManager {

    private VagrantFileCreator vagrantFileCreator;
    private final String VAGRANT_MACHINE_DIR;
    private String devstackConfigFile;
    private String vmName;

    private static Logger log = LoggerFactory.getLogger(VagrantMachineManager.class);

    public VagrantMachineManager() {
        String userHome = System.getProperty("user.home");
        VAGRANT_MACHINE_DIR = userHome + "/.devstack/virtual-machines";
    }

    public VagrantMachineManager withVagrantFileCreator(VagrantFileCreator vagrantFileCreator) {
        this.vagrantFileCreator = vagrantFileCreator;
        return this;
    }

    public VagrantMachineManager withDevstackConfigFile(String devstackConfigFile) {
        this.devstackConfigFile = devstackConfigFile;
        File file = new File(devstackConfigFile);
        if(!file.exists()) {
            throw new IllegalArgumentException("Devstack config file " + devstackConfigFile + " not found");
        }
        return this;
    }

    public void initialize() {
        try {
            File vmDir = new File(getVmDir(vmName));
            if(vmDir.exists()) {
                CommonUtils.deleteDir(getVmDir(vmName));
            }
            CommonUtils.createDir(getVmDir(vmName));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public String getVmDir(String vmName) {
        return VAGRANT_MACHINE_DIR + "/" + vmName;
    }

    public String getVirtualMachineDir() {
        return VAGRANT_MACHINE_DIR;
    }

    @Override
    public void prepareVirtualMachine(VMConfig vmConfig) {

        // set vm name
        vmName = vmConfig.getVmName();

        // get vagrant file contents
        String fileContents = vagrantFileCreator.getVagrantFileContents(vmConfig);

        // create vm dir
        initialize();

        // create vagrant file
        String vagrantFile = getVmDir(vmName) + "/Vagrantfile";
        CommonUtils.createFile(vagrantFile, fileContents, "755");
    }

    @Override
    public void bootstrap() {

        String stackConfigFile = getVmDir(vmName) + "/stack.conf";
        CommonUtils.copyFile(devstackConfigFile, stackConfigFile);

        log.info("Created virtual machine spec in " + getVmDir(vmName));
    }
}
