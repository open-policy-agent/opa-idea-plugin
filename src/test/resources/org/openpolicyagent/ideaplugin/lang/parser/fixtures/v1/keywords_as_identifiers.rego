package play

if := 1

every := 2

and := 3

or := 4

uses_if if {
	input.if == 1
}

uses_every if {
	input.every.woo != null
}

uses_and if {
	input.and == 1
}

uses_or if {
	input.or.woo != null
}

check_if(if) := true

check_every(every, foo) := every + foo

check_and(and) := true

check_or(or, foo) := or + foo
