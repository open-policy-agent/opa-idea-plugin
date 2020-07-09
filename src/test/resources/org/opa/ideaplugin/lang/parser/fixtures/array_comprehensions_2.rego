package test

app_to_hostnames[app_name] = hostnames {
    app := apps[_]
    app_name := app.name
    hostnames := [hostname | name := app.servers[_]
                            s := sites[_].servers[_]
                            s.name == name
                            hostname := s.hostname]
}

aRule {
    app_to_hostnames[app]
}
