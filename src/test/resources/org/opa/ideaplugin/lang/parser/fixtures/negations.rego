package test

t {
    greeting := "hello"
    not greeting == "goodbye"
}

apps_not_in_prod[name] {
    apps := {}
    apps_in_prod := {}
    name := apps[_].name
    not apps_in_prod[name]
}
