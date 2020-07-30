package test

same_site[apps[k].name] {
    apps:={}
    sites:={}
    some i, j, k
    apps[i].name == "mysql"
    server := apps[i].servers[_]
    server == sites[j].servers[_].name
    other_server := sites[j].servers[_].name
    server != other_server
    other_server == apps[k].servers[_]
}

someRule {
    same_site[x]
}