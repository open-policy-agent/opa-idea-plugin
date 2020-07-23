package main

apps:={}
sites:={}

app_to_hostnames := {app.name: hostnames |
    app := apps[_]
    hostnames := [hostname |
                    name := app.servers[_]
                    s := sites[_].servers[_]
                    s.name == name
                    hostname := s.hostname]
}
