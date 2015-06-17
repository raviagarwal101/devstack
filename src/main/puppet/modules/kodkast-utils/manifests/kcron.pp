# modular utility to deploy crons

define kcron($module_name) {

    $script_name = "${title}.sh"
    $log_file = "$kodkast_log/$module_name/${title}.log"
    $rotation_name = "${title}-log-rotation"

    file {"/usr/local/bin/${title}":
        source  => "puppet:///modules/$module_name/$script_name",
        mode    => 755,
    }

    cron { "$title":
        command => "/usr/local/bin/$title >> $log_file 2>&1",
        environment => ['PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/root/bin'],
        user => root,
        minute => "*/15",
    }

    include log_rotate
    log_rotate::file { "$rotation_name":
        log => "$log_file",
        options => [ 'missingok', 'compress', 'daily', 'rotate 1' ],
    }
}
