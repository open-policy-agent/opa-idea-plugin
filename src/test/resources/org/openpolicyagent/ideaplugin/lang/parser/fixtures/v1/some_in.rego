package play

allow if {
	some x in input.users
	x.active == true
}

has_admin if {
	some k, v in input.roles
	v == "admin"
}
