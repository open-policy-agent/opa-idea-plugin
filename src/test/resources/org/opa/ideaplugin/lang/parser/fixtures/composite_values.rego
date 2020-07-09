package test

cube := {"width": 3, "height": 4, "depth": 5}

inRule {
    cube2 := {"width": 3, "height": 4, "depth": 5}
    cube2.width >= 3
}

a := 42
b := false
c := null
d := {"a": a, "x": [b, c]}

inOtherRule {
    a2 := 42
    b2 := false
    c2 := null
    d2 := {"a": a, "x": [b, c]}
}