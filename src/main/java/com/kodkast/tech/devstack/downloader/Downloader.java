package com.kodkast.tech.devstack.downloader;

public interface Downloader {

    public enum Type {
        SvnDownloader,
        HttpDownloader
    }

    public boolean downloadComponent(String componentName);

    public boolean downloadMetaInfo();
}
