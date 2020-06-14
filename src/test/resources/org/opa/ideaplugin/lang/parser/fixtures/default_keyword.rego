package main

default allow = false

allow {
    input.user == "bob"
    input.method == "GET"
}

allow {
    input.user == "alice"
}