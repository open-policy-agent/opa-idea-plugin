package play

allow if {
	every x in input.users {
		x.active == true
	}
}

valid_pairs if {
	every k, v in input.config {
		k != ""
		v != null
	}
}
