package play

if := 1

every := 2

uses_if if {
	input.if == 1
}

uses_every if {
	input.every.woo != null
}

check_if(if) := true

check_every(every, foo) := every + foo
