package test

a1 := {1,2,3}
a2 := {3}
a3 := {4}
array1:=[1, 2]
array2:=[3]



output1 := array.concat(array1, array2)

# issue #70
b := a1 | a2 | a3
c := a1 & a2 | a3 - b

d := 1+2 - array.concat(array1, array2)[2]
e := array.concat(array1, array2)[0] + array.concat(array1, array2)[1]