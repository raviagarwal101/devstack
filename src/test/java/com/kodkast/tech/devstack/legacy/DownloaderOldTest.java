package com.kodkast.tech.devstack.legacy;

import com.kodkast.tech.devstack.core.DevStackConfig;
import org.junit.BeforeClass;
import org.junit.Test;

public class DownloaderOldTest {

    private static DevStackConfig devStackConfig;

    @BeforeClass
    public static void setup() {
        devStackConfig = new DevStackConfig();
    }

    @Test
    public void testDownloadComponent() {
        String moduleName = "ant";
        DownloaderOld.downloadComponent(devStackConfig, moduleName, "5m");
    }

    @Test
    public void testDownloadNonExistingComponent() {
        String moduleName = "custom-ant";
        DownloaderOld.downloadComponent(devStackConfig, moduleName, "5m");
    }

    @Test
    public void testDownloadMetaInfo() {
        DownloaderOld.downloadMetaInfo(devStackConfig);
    }

    @Test
    public void testTimeParseForMinutes() {
        long timeInSecs = DownloaderOld.getTimeInSeconds("5m");
        System.out.println(timeInSecs);
    }

    @Test
    public void testTimeParseForHours() {
        long timeInSecs = DownloaderOld.getTimeInSeconds("1h");
        System.out.println(timeInSecs);
    }
}
