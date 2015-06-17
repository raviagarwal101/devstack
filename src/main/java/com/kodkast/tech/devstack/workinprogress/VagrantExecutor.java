package com.kodkast.tech.devstack.workinprogress;

public class VagrantExecutor {

    private String vmDir;

    public VagrantExecutor(String vmDir) {
        this.vmDir = vmDir;
    }

    public void status() {

    }

    private String createScript() {

        StringBuilder builder = new StringBuilder();
        builder.append("#!/bin/bash\n\n");


        return builder.toString();
    }
}
