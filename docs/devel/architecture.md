# Table of Content
<!-- toc -->
- [Project structure](#project-structure)
- [Lexer](#lexer)
- [Parser](#parser)
- [Testing strategy](#testing-strategy)
  - [Tests resources](#tests-resources)
    - [Example](#example)
  - [Parsing tests](#parsing-tests)
  - [RunConfiguration tests](#runconfiguration-tests)
    - [TestRunConfutation tests](#testrunconfutation-tests)
<!-- /toc -->

*to update toc, please read [this page](../../hack/README.md).*

# Project structure

```
.
├── build.gradle.kts
├── gradle.properties
├── docs
│   └── devel #technical documentation of the project
├── hack # set of usefull script to manage the project
└── src
    ├── main
    │   ├── gen # lexer and and parser generated code
    │   ├── grammar # grammar and lexer definition
    │   ├── kotlin
    │   │   └── org.openpolicyagent.ideaplugin
    │   │       ├── ide # code relative to ide action (eg run opa test, create a rego file...)
    │   │       │   ├── actions
    │   │       │   ├── colors
    │   │       │   ├── commenter
    │   │       │   ├── highlight
    │   │       │   └── runconfig
    │   │       ├── lang # code relative to rego language
    │   │       │   ├── lexer
    │   │       │   ├── parser
    │   │       │   ├── psi
    │   │       │   ├── RegoFiletype.kt
    │   │       │   ├── RegoIcons.kt
    │   │       │   └── RegoLanguage.kt
    │   │       ├── opa
    │   │       │   └── tool
    │   │       └── openapiext # extention methods that could be in IDEA sdk
    │   └── resources # assets need by the plugin
    └── test
        ├── kotlin # test source code
        └── resources # assets needed by tests
```

The project is build thanks to gradle. We use the [gradle kotlin dsl](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
because it's allow use to use the task `generateRegoLexer` and `generateRegoParser` to tasks automatically generate
lexer and parser code before compiling kotlin code.

# Lexer
The lexer is defined by the [RegoLexer.lex](../../src/main/grammar/RegoLexer.flex) file written in [JFlex](https://www.jflex.de/)
format. Code can be generated thanks to `generateRegoLexer` gradle tasks.

# Parser
The parser is defined by the [Rego.bnf](../../src/main/grammar/Rego.bnf) file written in [Grammar kit ](https://github.com/JetBrains/Grammar-Kit)
format. It's looks like a `bnf` grammar. `Grammar kit` also generate the [PSI](https://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi.html)
(Program Structure Interface) which a kind of AST but with more information. Lots of ide features are plugged to the PSI
so once the PSI is generated, some feature like commenting code are really straightforward to implement.

Code can be generated thanks to `generateRegoParser` gradle tasks.

# Testing strategy
As an introduction we recommend to read the official [documentation](https://www.jetbrains.org/intellij/sdk/docs/basics/testing_plugins/testing_plugins.html)

For testing we use the following frameworks:
 * Junit 4
 * [assertJ](https://assertj.github.io/doc/#assertj-overview): a fluent assertion framework
 
Main classes to extends for testing:
[OpaTestBase](../../src/test/kotlin/org/openpolicyagent/ideaplugin/OpaTestBase.kt): extends `BasePlatformTestCase` for light tests (use an in memory FS)
[OpaWithRealProjectTestBase](../../src/test/kotlin/org/openpolicyagent/ideaplugin/OpaWithRealProjectTestBase.kt): extends `CodeInsightFixtureTestCase` for test that need a real FS


## Tests resources
Lots of tests compare an initial file and a the resulting file after an action on it (eg commenting code). A common pattern
in intelliJ is to name these resources after the name of the test.

The logic to find and compare both files are implemented in base classes `OpaTestBase`, `OpaWithRealProjectTestBase`. 

In your test class you must override = `dataPath` field to indicate the folder containing the resources files. By 
convention this name of this folder follow the pattern `<package_of_the_tested_class>.fixtures`

### Example
lets look at the [RegoCommenterTest](../../src/test/kotlin/org/openpolicyagent/ideaplugin/ide/commenter/RegoCommenterTest.kt)
The resources files for this test Class are located at `src/test/resources/org/openpolicyagent/ideaplugin/ide/commenter/fixtures`

**note: you can see that `dataPath` omit the prefix `src/test/resources/`. Because it's common to all the tests, the base test class (`OpaTestBase`) automatically add it**

The resource file for the test `test single line` are:
* `single_line.rego`: the file before running the commenting action  
* `single_line_after.rego`: the expected result (ie the file with the commented code)

## Parsing tests
The parsing test are very simple. They just check that rego code is well parsed (ie parsing does not return error). For
the moment it's does not check the PSI. Eventually we will have to do it.

## RunConfiguration tests                                                      
For RunConfiguration or TestRunConfiguration we do two kinds of test:
* testing the runConfiguration parameter validation
* testing the execution 
 
The tests extends from [RunConfigurationTestBase](../../src/test/kotlin/org/openpolicyagent/ideaplugin/ide/runconfig/RunConfigurationTestBase.kt)
This class extends from `OpaWithRealProjectTestBase`. We can not extends from `OpaTestBase` because we need a "real" FS
to run test.


The class `RunConfigurationTestBase` offer some utility methods:
* `createConfiguration` to create the run configuration
* `executeAndGetOutput` to execute the configuration and get the output

Moreover the [FileTree](../../src/test/kotlin/org/openpolicyagent/ideaplugin/FileTree.kt) module contains a lille dsl
to create file in the test project. This allow to easily saw what kind of project we test.

```kotlin
  buildProject {
            dir("src") {
                rego(
                    "all.rego", """
                        package main
                        import data.sec
                        
                        allow[msg] {
                            # using a rule define din another package in order to be sure that bundle has load file
                            sec.allow
                            msg:= "allowed by sec"
                        }
                    """.trimIndent()
                )
                rego(
                    "sec.rego", """
                        package sec
    
                        allow {
                            input.sec == true
                        }
                    """.trimIndent()
                )

                json(
                    "input.json", """
                        {
                            "sec": true
                        }
                    """.trimIndent()
                )
            }
        }
```
 
### TestRunConfutation tests
For testing a testRunConfiguration, there are several changes from the RunConfiguration. When we execute a test configuration
we want to check:

* the test tree structure: all rego tests are present in the tree and have the valid status (Pass, Failed)
* the output of each node in the tree (ie the output corresponding to one rego test)

To execute the configuration we use the method `executeAndGetTestRoot`. This method return the test tree.


In order ot *test the  tree structure* we use the method `getFormattedTestTree` that return the tree has a string for
easy comparison with an expected output

example of output of `getFormattedTestTree`
```
[root](-)
.data.test.main.test_rule_2_should_be_ko(-)
.data.test.main.test_rule1_should_be_ok(+)
.data.test.main.test_should_raise_error(-)
```


In order ot *test the output of each node* we use the method `checkTreeErrorMsg(root)`. This method walk the test tree
and check the node output match a regex defined in a file. By convention this file must name after the name of the node
and located in the folder `${dataPath}/${testName}`

please look at [TestRunConfigurationExecutionOpaTest](../../src/test/kotlin/org/openpolicyagent/ideaplugin/ide/runconfig/test/TestRunConfigurationExecutionOpaTest.kt)
for an example.
