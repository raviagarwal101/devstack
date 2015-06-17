# kodkast main module

class kodkast-utils ($action = 'install') {

    $kodkast_home = "/opt/kodkast"

    if($action == "install") {

        Exec { path => [ "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }

        file { "/etc/profile.d":
            ensure  => directory,
        }

        file { "${kodkast_home}":
            ensure  => directory,
        }

        file { "${kodkast_home}/bin":
            ensure  => directory,
            require => File["${kodkast_home}"],
        }

        file { "/etc/profile":
            source  => "puppet:///modules/kodkast-utils/profile",
            ensure  => file,
        }

        file { "/etc/profile.d/utils.profile":
            content => template("kodkast-utils/profile.erb"),
            ensure  => file,
            mode    => 755,
            require => File["/etc/profile.d"],
        }

        file { "/opt/kodkast/bin/ff":
            source  => "puppet:///modules/kodkast-utils/ff",
            ensure  => file,
            require => File["/opt/kodkast/bin"],
        }

        file { "/opt/kodkast/bin/sm":
            source  => "puppet:///modules/kodkast-utils/sm",
            ensure  => file,
            require => File["/opt/kodkast/bin"],
        }

        file { "/opt/kodkast/bin/gg":
            source  => "puppet:///modules/kodkast-utils/gg",
            ensure  => file,
            require => File["/opt/kodkast/bin"],
        }
    }
}
