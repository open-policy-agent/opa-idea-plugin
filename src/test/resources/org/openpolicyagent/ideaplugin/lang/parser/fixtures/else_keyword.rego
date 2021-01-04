package main
a := []

# In order to handle manifest with one or many resources, we turns the input into an array, if it's not an array already.
as_array(x) = [x] {not is_array(x)} else = x {true}


authorize = "allow" {
    input.user == "superuser"           # allow 'superuser' to perform any operation.
} else = "deny" {
    input.path[0] == "admin"            # disallow 'admin' operations...
    input.source_network == "external"  # from external networks.
}


check_function_call_for_else(x) = x {
    x % 2 == 0
}else = abs(x) {
    true
}

check_infix_op_for_else(x) = x {
    x % 2 == 0
} else = x *2 - 1 {
    true
}


check_array_comprehension_for_else(x) = x {
    x % 2 == 0
} else = [y | some y; a[y] == x] {
    true
}

check_set_comprehension_for_else(x) = x {
    x % 2 == 0
} else = {y | some y; a[y]} {
    true
}

check_object_comprehension_for_else(x) = x {
    x % 2 == 0
} else = {y : "true" | some y;  a[y] } {
    true
}