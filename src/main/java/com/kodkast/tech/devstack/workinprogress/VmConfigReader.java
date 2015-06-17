package com.kodkast.tech.devstack.workinprogress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public enum VmConfigReader {

    INSTANCE;

    private VMConfig vmConfig;

    private static final String VM_NAME = "vm.name";
    private static final String HOSTNAME = "vm.hostname";
    private static final String NETWORK = "vm.network";
    private static final String DEVSTACK = "vm.devstack";
    private static final String PUPPET = "vm.puppet";
    private static final String TIMEZONE = "vm.timezone";
    private static final String OS = "vm.os";
    private static final String MEMORY = "vm.memory";
    private static final String CPUS = "vm.cpus";

    private static Logger log = LoggerFactory.getLogger(VmConfigReader.class);

    public void readConfig(String configFile) {

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(configFile));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Config file not found");
        }

        String hostname = properties.getProperty(HOSTNAME);
        if(hostname == null) {
            throw new IllegalStateException("vm hostname is required");
        }

        String vmName = properties.getProperty(VM_NAME);
        if(vmName == null) {
            throw new IllegalStateException("vm name is required");
        }

        VMConfig.Builder builder = new VMConfig.Builder(vmName, hostname);

        if(properties.getProperty(DEVSTACK) != null && properties.getProperty(DEVSTACK).equals("false")) {
            builder.withNoDevstack();
        }

        if(properties.getProperty(PUPPET) != null && properties.getProperty(PUPPET).equals("false")) {
            builder.withNoPuppet();
        }

        if(properties.getProperty(NETWORK) != null && !properties.getProperty(NETWORK).equals("dhcp")) {
            builder.withStaticIP();
        }

        if(properties.getProperty(OS) != null) {
            builder.withOS(VMConfig.OS.valueOf(properties.getProperty(OS)));
        }

        if(properties.getProperty(TIMEZONE) != null) {
            builder.withTimezone(properties.getProperty(TIMEZONE));
        }

        if(properties.getProperty(MEMORY) != null) {
            builder.withMemory(Integer.parseInt(properties.getProperty(MEMORY)));
        }

        if(properties.getProperty(CPUS) != null) {
            builder.withCpus(Integer.parseInt(properties.getProperty(CPUS)));
        }

        vmConfig = builder.build();
    }

    public VMConfig getVmConfig() {
        return vmConfig;
    }
}
