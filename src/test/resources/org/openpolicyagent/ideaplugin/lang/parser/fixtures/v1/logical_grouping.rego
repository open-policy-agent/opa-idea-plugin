package play

import future.keywords.and
import future.keywords.not
import future.keywords.or

# explicit bodies as operands
allow if {
	{input.user == "alice"} or {input.role == "admin"; input.active}
}

leading_body if {
	{input.a; input.b} and input.c
}

# parentheses override the precedence of "and" over "or"
grouped if {
	(input.a or input.b) and input.c
}

nested if {
	((input.a or input.b) and input.c) or input.d
}

group_with if {
	(input.a and input.b with input.x as 1)
}

negated_group if {
	input.c and not (input.a or input.b)
}
