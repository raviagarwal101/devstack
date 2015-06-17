# unzip define

define unzip($pkg_zip, $module, $install_dir) {

    file { "${install_dir}":
        ensure  => directory,
    }

    file { "${pkg_zip}":
        path    => "/tmp/${pkg_zip}",
        source  => "puppet:///modules/${module}/${pkg_zip}",
    }

    exec { "unzip ${pkg_zip}":
        command => "/bin/rm -rf ${install_dir}/*; unzip /tmp/${pkg_zip} -d ${install_dir}/",
        require => File["${pkg_zip}", "${install_dir}"],
    }
}
