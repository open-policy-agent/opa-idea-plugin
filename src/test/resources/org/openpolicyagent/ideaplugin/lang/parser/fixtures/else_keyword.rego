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

check_only_querry_for_else = true {
 false
} else {
    true
}

check_only_equal_querry_for_else = true {
 false
} else = {
    true
}

check_else_with_colon_assignment_op {
    input.x < input.y
} else := true {
    input.y < input.x
} else := false

# issue 84
check_empty_query_else = true {
    input.x < input.y
} else = false


check_empty_query_else_with_number = 3 {
    input.x < input.y
} else = 2

check_empty_query_else_with_string = "ok" {
    input.x < input.y
} else = "ko"

check_empty_query_else_with_array= ["ok", "yes"] {
    input.x < input.y
} else = ["ko", "no"]

check_empty_query_else_with_set= { 0 ,2 ,3 } {
    input.x < input.y
} else = { 4, 5, 6 }


check_empty_query_else_with_infix_op= 0 {
    input.x < input.y
} else = abs(input.x) + 10 - input.y
