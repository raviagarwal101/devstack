package com.kodkast.tech.devstack.bin;

import com.kodkast.tech.devstack.core.ComponentInstaller;
import com.kodkast.tech.devstack.core.ComponentManager;
import com.kodkast.tech.devstack.core.DevStack;
import com.kodkast.tech.devstack.core.DevStackConfig;
import com.kodkast.tech.devstack.core.DevStackConfigs;
import com.kodkast.tech.devstack.downloader.Downloader;
import com.kodkast.tech.devstack.downloader.NullDownloader;
import com.kodkast.tech.devstack.downloader.SvnDownloader;

public class DevStackCLI {

    /*
        puppet hacks - http://www.slideshare.net/PuppetLabs/11-ways-to-hack-puppet-for-fun-and-productivity-luke-kanies-velocity-2012
    */

    public static void main(String args[]) {

        String configFile = null;
        String devstackConfigFile = null;
        String action = null;
        String mode = null;

        if(args.length >= 1) {
            action = args[0];
        }
        if(args.length >= 2) {
            configFile = args[1];
        }
        if(args.length >= 3) {
            devstackConfigFile = args[2];
        }
        if(args.length == 4) {
            mode = args[3];
        }

        if(args.length < 1 || args.length > 4) {
            System.out.println("usage : devstack <action> [<stack-config-file-path>] [<devstack-config-file>] [<mode>]");
            System.exit(1);
        }

        // read devstack configs
        DevStackConfig devStackConfig;
        if(devstackConfigFile != null && !devstackConfigFile.equals("default")) {
            devStackConfig = new DevStackConfig(devstackConfigFile);
        }
        else {
            devStackConfig = new DevStackConfig();
        }

        // create downloader of components
        Downloader downloader = null;
        if(action != null && action.equals(DevStackConfigs.INSTALL)) {
            downloader = new SvnDownloader().withDevstackConfig(devStackConfig);
        }
        else {
            downloader = new NullDownloader();
        }

        // create component manager
        ComponentManager componentManager = new ComponentManager(devStackConfig)
            .withDownloader(downloader);

        // create component installer
        ComponentInstaller componentInstaller = new ComponentInstaller(devStackConfig);

        // create Devstack core object
        DevStack devStack = new DevStack(devStackConfig)
            .withComponentManager(componentManager)
            .withComponentInstaller(componentInstaller)
            .withDownloader(downloader);

        if(mode != null && mode.equals(DevStackConfigs.DEBUG_MODE)) {
            devStack = devStack.withDebugMode();
        }

        if(action != null && action.equals(DevStackConfigs.LIST_ALL)) {
            devStack.listAllComponents();
        }
        else if(action != null && action.equals(DevStackConfigs.LIST)) {
            devStack.listInstalledComponents();
        }
        else {
            devStack.process(action, configFile);
        }
    }
}