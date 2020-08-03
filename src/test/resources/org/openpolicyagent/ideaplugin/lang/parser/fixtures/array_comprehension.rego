package main

region := "some region"
sites := []
names := [name | some i; sites[i].region == region; name := sites[i].name]
