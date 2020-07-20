package main

sites:={}
region:={}

names := [name | some i; sites[i].region == region; name := sites[i].name]
