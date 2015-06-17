# kodkast-webapp module

import "kodkast-utils/tarball"

class kodkast-webapp ($action = 'install') {

    $vhosts_file = "${apache_conf_dir}/other/httpd-webapp.conf"
    $php_index = "index.php"
    $tarball = "webapp.tgz"

    $webapp_module = "kodkast-webapp"

    if($action == "install") {

        file { "${vhosts_file}":
            content => template("kodkast-webapp/httpd-webapp.conf.erb"),
            ensure  => file,
            notify  => Exec["restart-apache"];
        }

        file { "${webapp_document_root}/${php_index}":
            source  => "puppet:///modules/${webapp_module}/${php_index}",
            ensure  => file,
        }

        tarball { "${tarball}":
            module => "${webapp_module}",
            install_dir => "${webapp_document_root}",
            pkg_tgz     => "${tarball}",
        }

    }

}
