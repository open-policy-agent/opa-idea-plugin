package t

allow := input.yes == true

test_me {
	allow with input.yes as true == true
}
