package com.kodkast.tech.devstack.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OSUtils {

    public static enum OS_NAME {
        MAC,
        LINUX,
        WINDOWS;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private static OS_NAME currentOsName;
    private static Map<OS_NAME, Set<String>> osAliases = new HashMap<OS_NAME, Set<String>>();
    private static Map<String, OS_NAME> osMap = new HashMap<String, OS_NAME>();

    private static boolean initialized = false;

    private static void initialize() {
        initialized = true;
        setAliases();
        createOSMap();
        setCurrentOSName();
    }

    private static void setAliases() {

        Set<String> macAliases = new HashSet<String>();
        macAliases.add("mac");
        macAliases.add("mac os");
        macAliases.add("mac os x");
        macAliases.add("darwin");

        osAliases.put(OS_NAME.MAC, macAliases);

        Set<String> linuxAliases = new HashSet<String>();
        linuxAliases.add("linux");
        linuxAliases.add("centos");
        linuxAliases.add("cent os");
        linuxAliases.add("redhat");
        linuxAliases.add("ubuntu");

        osAliases.put(OS_NAME.LINUX, linuxAliases);

        Set<String> windowsAliases = new HashSet<String>();
        windowsAliases.add("windows");

        osAliases.put(OS_NAME.WINDOWS, windowsAliases);
    }

    private static void createOSMap() {

        for(Map.Entry<OS_NAME, Set<String>> entry : osAliases.entrySet()) {
            OS_NAME osName = entry.getKey();
            Set<String> osAliases = entry.getValue();
            for(String alias : osAliases) {
                osMap.put(alias, osName);
            }
        }
    }

    private static void setCurrentOSName() {
        String osNameSystemProp = System.getProperty("os.name");
        currentOsName = getOSName(osNameSystemProp);
    }

    public static OS_NAME getOSName(String name) {

        if(!initialized) {
            initialize();
        }

        String simpleName = name.toLowerCase().trim();
        return osMap.get(simpleName);
    }

    public static OS_NAME getOperatingSystemName() {

        if(!initialized) {
            initialize();
        }

        return currentOsName;
    }
}
