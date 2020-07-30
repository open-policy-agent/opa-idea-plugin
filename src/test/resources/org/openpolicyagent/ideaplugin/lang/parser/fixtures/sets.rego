package test

cube := {"width": 3, "height": 4, "depth": 5}
s := {cube.width, cube.height, cube.depth}
x := null

inRule {
    s := {cube.width, cube.height, cube.depth}
    {1,2,3} == {3,1,2}
    {1,2,3} == {3,x,2}
#    count(set()) < 1
}
