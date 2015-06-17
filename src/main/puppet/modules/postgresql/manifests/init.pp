
# Module info:
#   this module installs postgresql
#
# Inputs:
#   postgresql_root_dir - path where postgresql should be installed

import "kodkast-utils/tarball"

class postgresql ($action = 'install') {

    $postgresql_module = "postgresql"
    $tarball = "postgresql-9.3.4.tar.gz"
    $postgresql_src = "${postgresql_root_dir}/postgresql-9.3.4"
    $postgresql_home = "/usr/local/postgresql"
    $postgresql_script = "postgresql.sh"

    file { "/tmp/${postgresql_script}":
        path    => "/tmp/${postgresql_script}",
        mode    => 755,
        source  => "puppet:///modules/${postgresql_module}/${postgresql_script}",
    }

    if($action == "install") {

        notify { "Installing ${postgresql_module} at ${postgresql_root_dir}": }

        file { "/etc/profile.d/${postgresql_module}.profile":
            content => template("${postgresql_module}/profile.erb"),
            ensure  => file,
            mode    => 755,
            require => File["/etc/profile.d"],
        }

        tarball { "${tarball}":
            module => "${postgresql_module}",
            install_dir => "${postgresql_root_dir}",
            pkg_tgz     => "${tarball}",
            require     => Notify["Installing ${postgresql_module} at ${postgresql_root_dir}"];
        }

        exec { "install-${postgresql_module}":
            command => "/tmp/${postgresql_script} ${postgresql_src} ${postgresql_home} ${postgresql_defaultdb} install > /tmp/postgresql.log 2>&1",
            require => [ Tarball["${tarball}"], File["/tmp/${postgresql_script}"] ];
        }

        group { "postgresql":
            ensure => present,
        }

        user { "postgresql":
            ensure  => present,
            groups  => ["postgresql"],
            require => Group["postgresql"];
        }

    }

    if ($action == "start") {
        exec { "start-postgresql":
          command => "/tmp/${postgresql_script} ${postgresql_src} ${postgresql_home} ${postgresql_defaultdb} start",
          require => File["/tmp/${postgresql_script}"];
        }
    }

    if ($action == "stop") {
        exec { "stop-postgresql":
          command => "/tmp/${postgresql_script} ${postgresql_src} ${postgresql_home} ${postgresql_defaultdb} stop",
          require => File["/tmp/${postgresql_script}"];
        }
    }

    if ($action == "status") {
        exec { "status-postgresql":
          command => "/tmp/${postgresql_script} ${postgresql_src} ${postgresql_home} status",
          require => File["/tmp/${postgresql_script}"];
        }
    }

}
