package play

default hello = false

hello {
#    <caret>m := input.message # nested comment
    m == "world"
}