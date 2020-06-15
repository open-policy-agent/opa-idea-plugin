package main

# In order to handle manifest with one or many resources, we turns the input into an array, if it's not an array already.
as_array(x) = [x] {not is_array(x)} else = x {true}