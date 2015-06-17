package com.kodkast.tech.devstack.downloader;

import com.kodkast.tech.devstack.core.DevStackConfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DownloaderTest {

    @BeforeClass
    public static void setup() {

    }

    @Test
    public void testDownloadWithSvnDownloader() {

        DevStackConfig devStackConfig = new DevStackConfig("src/test/resources/devstack-test.conf");

        Downloader downloader = new SvnDownloader()
            .withDevstackConfig(devStackConfig);

        String componentName = "demo";

        boolean downloaded = downloader.downloadComponent(componentName);

        Assert.assertTrue(downloaded);
    }

    @Test
    public void testDownloadWithNullDownloader() {

        Downloader downloader = new NullDownloader();

        String componentName = "demo";

        boolean downloaded = downloader.downloadComponent(componentName);

        Assert.assertTrue(downloaded);
    }
}
