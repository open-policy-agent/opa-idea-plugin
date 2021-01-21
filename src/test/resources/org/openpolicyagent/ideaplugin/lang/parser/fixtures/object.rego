package main

a := { }
c := 2
a2 := { "a": 1 }
a3 := { "a": 1, "b": c }
a4 := { "a": 1, "b": c, }


a5 := { "a": {"e": 123}, "b": c }

# test object value can be complex expression. Issue #77
a6 := {
    "a": 1 > 2,
    "b": {
        "c": object.get(a2, "a", "default") +  abs(c) - 2
    }
}