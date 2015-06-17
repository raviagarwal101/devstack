package com.kodkast.tech.devstack.workinprogress;

public class VMConfig {

    private final String vmName;
    private final String hostname;
    private final String timezone;
    private final OS os;
    private final boolean hasPuppet;
    private final boolean hasDevstack;
    private final boolean hasDynamicIp;
    private final int memory;
    private final int cpus;

    public enum OS {

        CENTOS("centos-base");

        private final String name;

        private OS(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public static class Builder {

        // required params
        private final String vmName;
        private final String hostname;

        // optional params
        private OS os = OS.CENTOS;
        private boolean hasPuppet = true;
        private boolean hasDevstack = true;
        private boolean hasDynamicIp = true;
        private String timezone = "America/New_York";
        private int memory = 1024;
        private int cpus = 1;

        public Builder(String vmName, String hostname) {
            this.vmName = vmName;
            this.hostname = hostname;
        }

        public Builder withOS(OS os) {
            this.os = os;
            return this;
        }

        public Builder withNoPuppet() {
            this.hasPuppet = false;
            return this;
        }

        public Builder withNoDevstack() {
            this.hasDevstack = false;
            return this;
        }

        public Builder withStaticIP() {
            this.hasDynamicIp = false;
            return this;
        }

        public Builder withTimezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        public Builder withMemory(int memory) {
            this.memory = memory;
            return this;
        }

        public Builder withCpus(int cpus) {
            this.cpus = cpus;
            return this;
        }

        public VMConfig build() {

            VMConfig vmConfig = new VMConfig(this);

            // validate params
            if(vmConfig.hostname == null) {
                throw new IllegalStateException("vm hostname required");
            }

            return vmConfig;
        }
    }

    private VMConfig(Builder builder) {
        os = builder.os;
        vmName = builder.vmName;
        hostname = builder.hostname;
        hasPuppet = builder.hasPuppet;
        hasDevstack = builder.hasDevstack;
        hasDynamicIp = builder.hasDynamicIp;
        timezone = builder.timezone;
        memory = builder.memory;
        cpus = builder.cpus;
    }

    public String getHostname() {
        return hostname;
    }

    public OS getOs() {
        return os;
    }

    public boolean hasPuppet() {
        return hasPuppet;
    }

    public boolean hasDevstack() {
        return hasDevstack;
    }

    public boolean hasDynamicIp() {
        return hasDynamicIp;
    }

    public String getTimezone() {
        return timezone;
    }

    public int getMemory() {
        return memory;
    }

    public int getCpus() {
        return cpus;
    }

    public String getVmName() {
        return vmName;
    }
}
