package test

apps_and_hostnames[[name, hostname]] {
    apps:={}
    sites:={}
    some i, j, k
    name := apps[i].name
    server := apps[i].servers[_]
    sites[j].servers[k].name == server
    hostname := sites[j].servers[k].hostname
}

someRule {
    apps_and_hostnames[x]
}
