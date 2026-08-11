package play

allow if {
	"admin" in input.roles
}

deny if {
	not "guest" in input.roles
}

has_pair if {
	["a", 1] in input.entries
}

is_admin if {
	x := "admin" in input.roles
	x
}

log_membership if {
	print("admin" in input.roles)
}
