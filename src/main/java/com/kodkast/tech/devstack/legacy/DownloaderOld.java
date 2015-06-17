package com.kodkast.tech.devstack.legacy;

import com.kodkast.tech.common.CommandOutput;
import com.kodkast.tech.common.CommonUtils;
import com.kodkast.tech.devstack.core.DevStackConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DownloaderOld {

    private static Logger logger = LoggerFactory.getLogger(DownloaderOld.class);

    public static boolean downloadComponent(DevStackConfig devStackConfig, String moduleName, String modulesCacheExpire) {

        boolean download = false;
        String moduleSvnPath = devStackConfig.getModulesSvnPath() + "/" + moduleName;
        String localModulePath = devStackConfig.getModulesLocalPath() + "/" + moduleName;

        String command = "svn checkout " + moduleSvnPath + " " + localModulePath;

        long expireTime = getTimeInSeconds(modulesCacheExpire);
        if(expireTime == -1) {
            download = true;
        }
        else {
            File file = new File(localModulePath);
            if (file.exists()) {
                command = "svn update " + localModulePath;
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

        // execute the command
        if(download) {
            logger.info("Downloading component " + moduleName);
            logger.info("Running " + command);
            CommandOutput commandOutput = CommonUtils.executeCommand(command);
            if(commandOutput.executedSuccessfully()) {
                return true;
            }
        }
        else {
            logger.info("Component " + moduleName + " recently downloaded");
            return true;
        }

        return false;
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

    public static void downloadMetaInfo(DevStackConfig devStackConfig) {

        logger.info("Updating DevStack Meta Info");

        // prepare svn path
        String command = "svn checkout " + devStackConfig.getMetaInfoSvnPath() + " " + devStackConfig.getMetaInfoLocalPath();

        // execute the command
        CommonUtils.executeCommand(command);
    }
}
