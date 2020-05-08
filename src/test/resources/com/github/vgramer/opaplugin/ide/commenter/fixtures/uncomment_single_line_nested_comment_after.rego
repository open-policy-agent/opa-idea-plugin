package play

default hello = false

hello {
    m := input.message # nested comment
    <caret>m == "world"
}