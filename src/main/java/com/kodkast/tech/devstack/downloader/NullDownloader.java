package com.kodkast.tech.devstack.downloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NullDownloader implements Downloader {

    private static Logger logger = LoggerFactory.getLogger(NullDownloader.class);

    public NullDownloader() {

    }

    @Override
    public boolean downloadComponent(String componentName) {
        logger.info("Ignoring download of " + componentName);
        return true;
    }

    @Override
    public boolean downloadMetaInfo() {
        logger.info("Ignoring download of DevStack Meta Info");
        return true;
    }
}
