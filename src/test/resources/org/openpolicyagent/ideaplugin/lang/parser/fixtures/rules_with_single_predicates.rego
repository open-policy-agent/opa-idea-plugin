package test

withTrueKeyword {
    true
}

withFalseKeyword {
    false
}

withFunction {
    isTrue
}

withTrivialComparison {
    1 == 1
}

withFunctionAllowingParam {
    isCorrect(input.something)
}

withEqOperator {
    input.something == 0
}

withGtOperator {
    input.something > 1
}

withGteOperator {
    input.something >= 1
}

withLtOperator {
    input.something < 1
}

withLteOperator {
    input.something <= 1
}

isTrue(){
 true
}

isCorrect(in){
    true
}
