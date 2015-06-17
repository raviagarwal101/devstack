# tarball define

define tarball($pkg_tgz, $module, $install_dir) {

    file { "${install_dir}":
        ensure  => directory,
    }

    file { "${pkg_tgz}":
        path    => "/tmp/${pkg_tgz}",
        source  => "puppet:///modules/${module}/${pkg_tgz}",
    }

    exec { "untar ${pkg_tgz}":
        command => "/bin/rm -rf ${install_dir}/*; tar xzvf /tmp/${pkg_tgz} -C ${install_dir}/",
        require => File["${pkg_tgz}", "${install_dir}"],
    }
}
