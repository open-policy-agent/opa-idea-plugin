package main


app_to_hostnames := {app.name: hostnames |
    app := apps[_]
    hostnames := [hostname |
                    name := app.servers[_]
                    s := sites[_].servers[_]
                    s.name == name
                    hostname := s.hostname]
}



merge_objects(a, b) = c {
    ks := {k | some k; _ = a[k]} | {k | some k; _ = b[k]}
    c := {k: v | some k; ks[k]; v := pick_first(k, b, a)}
}