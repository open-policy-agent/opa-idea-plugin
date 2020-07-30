package test

trim_and_split(s) = x {
     t := trim(s, " ")
     x := split(t, ".")
}

someRule {
    trim_and_split("   foo.bar ")
}

foo([x, {"bar": y}]) = z {
    z := {x: y}
}

a:={}
z := foo([a,a])
x := foo(["5", {"bar": "hello"}])
y := foo(["5", {"bar": [1, 2, 3, ["foo", "bar"]]}])

f(x) {
    x == "foo"
}

f(x) = true {
    x == "foo"
}

q(1, x) = y {
    y := x
}

q(2, x) = y {
    y := x*4
}
