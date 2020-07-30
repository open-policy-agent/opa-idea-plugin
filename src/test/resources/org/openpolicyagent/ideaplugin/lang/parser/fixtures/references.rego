package test

sites := [
    {"name": "prod", "servers": [{"hostname": "laptop"}, {"hostname": "rpi"}]},
    {"name": "smoke1", "servers": [{"hostname": "potato"}, {"hostname": "smartFridge"}]},
    {"name": "dev", "servers": [{"hostname": "supercomputer"}, {"hostname": "SpaceXControlCenter"}]}
]

a := sites[0].servers[1].hostname
b := sites[0]["servers"][1]["hostname"]

inRule {
    c := sites[0].servers[1].hostname
    d := sites[0]["servers"][1]["hostname"]
    sites[0].servers[1].hostname == "abc"
    sites[0]["servers"][1]["hostname"] == "abc"
    sites[i].servers[j].hostname
    sites[_].servers[_].hostname
}
