package test

#producing set
hostnames[name] {
    sites:={}

name := sites[_].servers[_].hostname }

#producing object
apps_by_hostname[hostname] = app {
    apps:={}
    sites:={}
    some i
    server := sites[_].servers[_]
    hostname := server.hostname
    apps[i].servers[_] = server.name
    app := apps[i].name
}

#incremental
instances[instance] {
    sites:={}
    server := sites[_].servers[_]
    instance := {"address": server.hostname, "name": server.name}
}

#incremental
instances[instance] {
    containers:={}
    container := containers[_]
    instance := {"address": container.ipaddress, "name": container.name}
}

#deprecated
instances[instance] {
    sites:={}
    server := sites[_].servers[_]
    instance := {"address": server.hostname, "name": server.name}
} {
    containers:={}
    container := containers[_]
    instance := {"address": container.ipaddress, "name": container.name}
}

#complete definitions
power_users := {"alice", "bob", "fred"}
restricted_users := {"bob", "kim"}

max_memory = 32 { power_users[user] }

max_memory = 4 { restricted_users[user] }