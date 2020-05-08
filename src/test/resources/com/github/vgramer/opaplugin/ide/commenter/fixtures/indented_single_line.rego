package play

default hello = false

hello {
    m := <caret>input.message
    m == "world"
}