
# Module info:
#   this module installs ant build tool
#
# Inputs:
#   ant_root_dir - path where ant should be installed

import "core_utils/tarball"

class custom-ant ($action = 'install') {

    $custom_ant_module = "custom-ant"
    $tarball = "apache-ant-1.9.4-bin.tar.gz"
    $custom_ant_home = "${custom_ant_root_dir}/apache-ant-1.9.4"

    if($action == "install") {

        notify { "Installing ${custom_ant_module} at ${custom_ant_root_dir}": }

        file { "/etc/profile.d/${custom_ant_module}.profile":
            content => template("${custom_ant_module}/profile.erb"),
            ensure  => file,
            mode    => 755,
            require => File["/etc/profile.d"],
        }

        tarball { "${tarball}":
            module => "${custom_ant_module}",
            install_dir => "${custom_ant_root_dir}",
            pkg_tgz     => "${tarball}",
            require     => Notify["Installing ${custom_ant_module} at ${custom_ant_root_dir}"];
        }

    }

}
