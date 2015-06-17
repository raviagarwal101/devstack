package com.kodkast.tech.devstack.workinprogress;

import com.kodkast.tech.common.CommonUtils;

/*
    Base vagrant files does following:
    - 1. centos - make centos base as desired os for the vm
    - 2. hostname - sets hostname for machine as given by user
    - 3. timezone - sets timezone on the machine
    - 4. puppet - installs puppet on the machine
    - 5. network - sets dynamic ip address for the machine
    - 6. memory, cpus - sets desired memory and cpus for the vm
    - 7. devstack - installs devstack on the machine
 */

public class VagrantFileCreator {

    private String fileContents;

    private String header;
    private String blockStart;
    private String blockEnd;

    private static String puppetInstallScriptPath = "https://kodkast.googlecode.com/svn/trunk/projects/virtual-machines/bin/install-puppet.sh";
    private static String devstackInstallScriptPath = "https://kodkast.googlecode.com/svn/trunk/projects/virtual-machines/bin/install-devstack.sh";
    private static String timezoneSetupScript = "https://kodkast.googlecode.com/svn/trunk/projects/virtual-machines/bin/setup-timezone";
    private static String defaultTimezone = "America/New_York";

    public VagrantFileCreator() {

        header = "# -*- mode: ruby -*-\n" +
                "# vi: set ft=ruby :\n" +
                "\n" +
                "# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!\n" +
                "VAGRANTFILE_API_VERSION = \"2\"\n";

        blockStart = "Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|\n" +
                "  # All Vagrant configuration is done here. The most common configuration\n" +
                "  # options are documented and commented below. For a complete reference,\n" +
                "  # please see the online documentation at vagrantup.com.\n";

        blockEnd = "\nend\n";
    }

    public String getVagrantFileContents(VMConfig config) {

        StringBuilder builder = new StringBuilder();

        builder.append(header);
        builder.append(blockStart);

        // set operating system
        builder.append(getVmBox(config.getOs()));

        // set hostname
        if (config.getHostname() != null) {
            builder.append(getVmHost(config.getHostname()));
        }

        // set timezone
        if(config.getTimezone() != null) {
            builder.append(getVmTimezone(config.getTimezone()));
        }
        else {
            builder.append(getVmTimezone(defaultTimezone));
        }

        // set memory and cpus
        if(config.getMemory() > 0 && config.getCpus() > 0) {
            builder.append(getVmMemory(config.getMemory(), config.getCpus()));
        }

        // set dynamic ip address
        if(config.hasDynamicIp()) {
            builder.append(getVmNetwork());
        }

        // set puppet
        if(config.hasPuppet()) {
            builder.append(getPuppetInstallScript());
        }

        // set devstack
        if(config.hasDevstack()) {
            builder.append(getDevstackInstallScript());
        }

        builder.append(blockEnd);

        fileContents = builder.toString();

        return fileContents;
    }

    private String getVmBox(VMConfig.OS osName) {

        String vmBox = "\n  # Every Vagrant virtual environment requires a box to build off of.\n" +
                "  config.vm.box = \"" + osName + "\"\n";

        return vmBox;
    }

    private String getVmHost(String hostname) {

        String vmHost = "\n  # set a hostname for the vm\n" +
                "  config.vm.host_name = \"" + hostname + "\"\n";

        return vmHost;
    }

    private String getVmTimezone(String timezone) {

        String vmTimezone = "\n  config.vm.provision \"shell\" do |s|\n" +
            "    s.path = \"" + timezoneSetupScript + "\"\n" +
            "    s.args = [\"" + timezone + "\"]\n" +
            "  end\n";

        return vmTimezone;
    }

    private String getPuppetInstallScript() {

        String puppetInstallScript = "\n  # install puppet\n" +
            "  config.vm.provision :shell, :path => \"" + puppetInstallScriptPath + "\"\n";

        return puppetInstallScript;
    }

    private String getDevstackInstallScript() {

        String devstackInstallScript = "\n  # install devstack\n" +
            "  config.vm.provision :shell, :path => \"" + devstackInstallScriptPath + "\"\n" +
            "\n" +
            "  # copy devstack config file\n" +
            "  config.vm.provision :file, :source => \"stack.conf\", :destination => \"/tmp/stack.conf\"\n" +
            "\n" +
            "  # run devstack on stack.conf\n" +
            "  config.vm.provision :shell, :inline => \"devstack -i -f /tmp/stack.conf\"\n";

        return devstackInstallScript;
    }

    private String getVmNetwork() {

        String vmIPAddress = "\n  # Create a private network, which allows host-only access to the machine\n" +
                "  # using dynamic IP.\n" +
                "  config.vm.network \"private_network\", type: \"dhcp\"\n";

        return vmIPAddress;
    }

    private String getVmMemory(int memory, int cpus) {

        String vmMemory = "\n  # Assign Memory and CPUs to the VM\n" +
            "  config.vm.provider \"virtualbox\" do |vb|\n" +
            "      vb.memory = " + memory + "\n" +
            "      vb.cpus = " + cpus + "\n" +
            "  end\n";

        return vmMemory;
    }

    public void setHostname(String hostname) {

        String existingWord = "config.vm.host_name = \"SET_ME\"";
        String newWord = "config.vm.host_name = \"" + hostname + "\"";

        fileContents = fileContents.replaceAll(existingWord, newWord);
    }

    public void setIPAddress(String ipAddress) {

        String existingWord = "config.vm.network \"private_network\", ip: \"SET_ME\"";
        String newWord = "config.vm.network \"private_network\", ip: \"" + ipAddress + "\"";

        fileContents = fileContents.replaceAll(existingWord, newWord);
    }

    public void readBaseFileContents() {

        String file = "src/main/resources/vagrant/Vagrantfile";

        fileContents = CommonUtils.readFileContents(file);
    }

    public String getFileContents() {
        return fileContents;
    }
}
