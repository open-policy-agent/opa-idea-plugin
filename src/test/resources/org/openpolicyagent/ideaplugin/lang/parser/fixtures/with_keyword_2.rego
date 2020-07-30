package test

allow {
    input.user == "bob"
}

rule_name(){
    allow with input as {"user": "bob", "method": "GET"}
}

rule_name_2(){
    not allow with input as {"user": "bob", "method": "GET"}
}

inner := [x, y] {
    x := input.foo
    y := input.bar
}

middle := [a, b] {
    a := inner with input.foo as 100
    b := input
}

outer := result {
    result := middle with input as {"foo": 200, "bar": 300}
}
