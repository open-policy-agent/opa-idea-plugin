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

fun_obj(x) = h {
    h := {"a": {"b":  [x,2]}}
}

fun_array(x) = j {
    j:= [{"a": [x,2]}]
}
aRule {
    # testing we can acces to the function's return value without assigning it to a variable. Issue #57
    a = fun_obj(1).a
    b = fun_obj(1).a.b
    c = fun_obj(1).a.b[0]

    k = fun_array(1)[0]
    l = fun_array(1)[0].a
    m = fun_array(1)[0].a[0]
}

filterFunc(x) {
   true
}

testing_complex_function_args{
    # testing accectiing infix operation as arg. Issue #63
    x := count({"x"} & {"y"})

    # testing "complex" args
    myset := {1, 2, 3}
    array.slice([1, 2, 3, 4 ], count({x | myset[x]; filterFunc(x)} & {2}), 3)
}