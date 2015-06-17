package com.kodkast.tech.devstack.downloader;

import com.kodkast.tech.common.CommandOutput;
import com.kodkast.tech.common.CommonUtils;
import com.kodkast.tech.devstack.core.DevStackConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SvnDownloader implements Downloader {

    private DevStackConfig devStackConfig;

    private static Logger logger = LoggerFactory.getLogger(SvnDownloader.class);

    public SvnDownloader() {

    }

    public SvnDownloader withDevstackConfig(DevStackConfig devStackConfig) {
        this.devStackConfig = devStackConfig;
        return this;
    }

    @Override
    public boolean downloadComponent(String componentName) {

        boolean download = false;
        String moduleSvnPath = devStackConfig.getModulesSvnPath() + "/" + componentName;
        String moduleLocalPath = devStackConfig.getModulesLocalPath() + "/" + componentName;

        String command = "svn checkout " + moduleSvnPath + " " + moduleLocalPath;

        long expireTime = getTimeInSeconds(devStackConfig.getModulesCacheExpire());
        if(expireTime == -1) {
            download = true;
        }
        else {
            File file = new File(moduleLocalPath);
            if (file.exists()) {
                command = "svn update " + moduleLocalPath;
                long lastModified = file.lastModified();
                long timeDiff = CommonUtils.getTimeDiffInSeconds(lastModified);
                if (timeDiff > expireTime) {
                    download = true;
                }
            }
            else {
                download = true;
            }
        }

        // this expire time logic doesn't work
        // for now hard-coding it to true
        download = true;

        // execute the command
        if(download) {
            logger.info("Downloading component " + componentName);
            logger.info("Running " + command);
            CommandOutput commandOutput = CommonUtils.executeCommand(command);
            if(commandOutput.executedSuccessfully()) {
                return true;
            }
        }
        else {
            logger.info("Component " + componentName + " recently downloaded");
            return true;
        }

        return false;
    }

    @Override
    public boolean downloadMetaInfo() {

        logger.info("Updating DevStack Meta Info");

        // prepare svn path
        String command = "svn checkout " + devStackConfig.getMetaInfoSvnPath() + " " + devStackConfig.getMetaInfoLocalPath();

        // execute the command
        CommonUtils.executeCommand(command);

        return true;
    }

    public static long getTimeInSeconds(String timeStr) {

        String timeStrEdited = timeStr.substring(0, timeStr.length() - 1);
        long time = Long.parseLong(timeStrEdited);

        if(timeStr.endsWith("m")) {
            return time * 60;
        }
        else if(timeStr.endsWith("h")) {
            return time * 60 * 60;
        }
        else if(timeStr.endsWith("d")) {
            return time * 60 * 60 * 24;
        }

        return -1;
    }
}
