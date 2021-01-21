package main

location := []
index := 0

apps = []
sites = []
number := [1,2,3]


########################################################################################################################
#                                                   test inside rule                                                   #
########################################################################################################################
rule1{

    # check function call cascade
    a1:= location[minus(count(location),1)]

    # infix operation
    a2:= location[count(location) - 1 + abs(3)]

    #check array
    a3:= location[[1,2,3][1]]

    # check set
    a4:= location[{1,2,3}[1]]

    #check object
    a5:= location[{"a": 0}["a"]]

    # array comphention
    a6:= location[[name | some i; location[i].region == "region"; name := location[i].name][0]]

    # set comphention
    a7:= location[{ x | some i; number[i] > 1; x:=number[i]}[0]]

    # obeject comphention
    a8:= location[{x:y | some i; number[i] > 2; y := number[i]; x := sprintf("hello%v",[y])}.hello3]

}

########################################################################################################################
#                                                   test outside rule                                                  #
########################################################################################################################

# check function call cascade
b1:= location[minus(count(location),1)]

# infix operation
b2:= location[count(location) - 1 + abs(3)]

#check array
b3:= location[[1,2,3][1]]

# check set
b4:= location[{1,2,3}[1]]

#check object
b5:= location[{"a": 0}["a"]]

# array comphention
b6:= location[[name | some i; location[i].region == "region"; name := location[i].name][0]]

# set comphention
b7:= location[{ x | some i; number[i] > 1; x:=number[i]}[0]]

# obeject comphention
b8:= location[{x:y | some i; number[i] > 2; y := number[i]; x := sprintf("hello%v",[y])}.hello3]


########################################################################################################################
#                                                   test rule head                                                     #
########################################################################################################################
c1(l, x) = location[minus(count(location),1)] {
	true
}


# check function call cascade
c1(l, x) =  location[minus(count(location),1)] {
    true
}

# infix operation
c2(l, x) =  location[count(location) - 1 + abs(3)] {
    true
}

#check array
c3(l, x) =  location[[1,2,3][1]] {
    true
}

# check set
c4(l, x) =  location[{1,2,3}[1]] {
    true
}

#check object
c5(l, x) =  location[{"a": 0}["a"]] {
    true
}

# array comphention
c6(l, x) =  location[[name | some i; location[i].region == "region"; name := location[i].name][0]] {
    true
}

# set comphention
c7(l, x) =  location[{ x | some i; number[i] > 1; x:=number[i]}[0]] {
    true
}

# obeject comphention
c8(l, x) =  location[{x:y | some i; number[i] > 2; y := number[i]; x := sprintf("hello%v",[y])}.hello3] {
    true
}