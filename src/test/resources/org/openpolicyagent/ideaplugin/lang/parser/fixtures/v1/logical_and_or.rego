package play

import future.keywords.and
import future.keywords.or

allow if {
	input.method == "GET" or input.method == "HEAD"
}

deny if {
	input.role == "guest" and startswith(input.path, "/admin")
}

chained if {
	input.a or input.b or input.c
}

# "and" binds tighter than "or"
mixed if {
	input.a == 1 and input.b == 2 or input.c == 3
}

# "not" binds tighter than "and"
negated if {
	not input.banned and input.active
}

membership if {
	"admin" in input.roles or "root" in input.roles
}

# a trailing "with" applies to the whole expression
audited if {
	data.acl.allow and data.acl.audit with input as {"user": "alice"}
}

one_line if input.x == 1 or input.y == 2
