package test

x:=1

sites := [
    {"name": "prod"},
    {"name": "smoke1"},
    {"name": "dev"}
]

q[name] { name := sites[_].name }

q[x]

someRule {
    q[x]
    q["dev"]
}
