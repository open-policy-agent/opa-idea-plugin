package play

default hello = false

hello {
#    m := input.message # nested comment
    m<caret> == "world"
}