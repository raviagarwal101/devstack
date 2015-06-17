package com.kodkast.tech.devstack.core;

import org.junit.Test;

public class OSUtilsTest {

    @Test
    public void testGetMacOSName() {

        OSUtils.OS_NAME osName = OSUtils.getOSName("Mac OS X");

        System.out.println(osName);
    }

    @Test
    public void testGetLinuxOSName() {

        OSUtils.OS_NAME osName = OSUtils.getOSName("Cent OS");

        System.out.println(osName);
    }

    @Test
    public void testGetOSName() {

        OSUtils.OS_NAME osName = OSUtils.getOperatingSystemName();

        System.out.println(osName);
    }
}
