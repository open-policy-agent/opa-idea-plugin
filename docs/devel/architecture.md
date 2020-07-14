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
│   └── devel #technical documentation of the project
├── hack # set of useful script to manage the project
└── src
    ├── main
    │   ├── gen # lexer and parser generated code
    │   ├── grammar # grammar and lexer definition
    │   ├── kotlin
    │   │   └── org.openpolicyagent.ideaplugin
    │   │       ├── ide # code relative to ide action (eg run opa test, create a rego file...)
    │   │       │   ├── actions
    │   │       │   ├── colors
    │   │       │   ├── commenter
    │   │       │   ├── highlight
    │   │       │   └── runconfig
    │   │       ├── lang # code relative to rego language
    │   │       │   ├── lexer
    │   │       │   ├── parser
    │   │       │   ├── psi
    │   │       │   ├── RegoFiletype.kt
    │   │       │   ├── RegoIcons.kt
    │   │       │   └── RegoLanguage.kt
    │   │       ├── opa
    │   │       │   └── tool
    │   │       └── openapiext # extension methods that could be in IDEA sdk
    │   └── resources # assets need by the plugin
    └── test
        ├── kotlin # test source code
        └── resources # assets needed by tests
```

The project is built using gradle. We use the [gradle kotlin dsl](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
because it contains the tasks `generateRegoLexer` and `generateRegoParser` that automatically generate
lexer and parser code before compiling Kotlin code.

# Lexer
The lexer is defined by the [RegoLexer.flex](../../src/main/grammar/RegoLexer.flex) file written in [JFlex](https://www.jflex.de/)
format. Code can be generated thanks to `generateRegoLexer` gradle tasks.

# Parser
The parser is defined by the [Rego.bnf](../../src/main/grammar/Rego.bnf) file written in [Grammar kit ](https://github.com/JetBrains/Grammar-Kit)
format. It looks like a `bnf` grammar. `Grammar kit` also generates the [PSI](https://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi.html)
(Program Structure Interface) which is a kind of AST but with more information. Lots of ide features are plugged to the PSI.
Once the PSI is generated, some features like commenting code are really straightforward to implement.

Code can be generated thanks to `generateRegoParser` gradle tasks.

# Testing strategy
As an introduction, we recommend reading the official [documentation](https://www.jetbrains.org/intellij/sdk/docs/basics/testing_plugins/testing_plugins.html).

For testing we use the following frameworks:
 * JUnit 4
 * [AssertJ](https://assertj.github.io/doc/#assertj-overview): a fluent assertion framework
 
Primary classes to extend for testing:
[OpaTestBase](../../src/test/kotlin/org/openpolicyagent/ideaplugin/OpaTestBase.kt): extends `BasePlatformTestCase` for light tests (uses an in memory FS).
[OpaWithRealProjectTestBase](../../src/test/kotlin/org/openpolicyagent/ideaplugin/OpaWithRealProjectTestBase.kt): extends `CodeInsightFixtureTestCase` for tests that need a real FS.


## Tests resources
Lots of tests compare an initial file and the resulting file after an action on it (eg. commenting code). A common pattern
in IntelliJ is to name these resources after the name of the test.

The logic to find and compare both files is implemented in base classes `OpaTestBase`, `OpaWithRealProjectTestBase`. 

In your test class you must override the `dataPath` field to indicate the folder containing the resources files. By 
convention the name of this folder follows the pattern `<package_of_the_tested_class>.fixtures`

### Example
Lets look at the [RegoCommenterTest](../../src/test/kotlin/org/openpolicyagent/ideaplugin/ide/commenter/RegoCommenterTest.kt).
The resources files for this test Class are located at `src/test/resources/org/openpolicyagent/ideaplugin/ide/commenter/fixtures`

**note: You can see that `dataPath` omits the prefix `src/test/resources/`. Because it's common to all the tests, the base test class (`OpaTestBase`) automatically adds it**

The resource files for the test `test single line` are:
* `single_line.rego`: the file before running the commenting action  
* `single_line_after.rego`: the expected result (ie the file with the commented code)

## Parsing tests
The parsing tests are very simple. They just check that rego code is well parsed (ie parsing does not return error). For
the moment it does not check the PSI. Eventually, support will be added for it.

## RunConfiguration tests                                                      
For RunConfiguration or TestRunConfiguration we do two kinds of test:
* testing the runConfiguration parameter validation
* testing the execution 
 
The tests extends from [RunConfigurationTestBase](../../src/test/kotlin/org/openpolicyagent/ideaplugin/ide/runconfig/RunConfigurationTestBase.kt)
This class extends from `OpaWithRealProjectTestBase`. We can not extend such tests from `OpaTestBase` because a "real" FS is needed to run such tests.


The class `RunConfigurationTestBase` offers some utility methods:
* `createConfiguration` to create the run configuration
* `executeAndGetOutput` to execute the configuration and get the output

Moreover, the [FileTree](../../src/test/kotlin/org/openpolicyagent/ideaplugin/FileTree.kt) module contains some DSL
to create files in the test project. This allows us to easily see what kind of project is being tested.

```kotlin
  buildProject {
            dir("src") {
                rego(
                    "all.rego", """
                        package main
                        import data.sec
                        
                        allow[msg] {
                            # using a rule defined in another package in order to be sure that bundle has loaded the file
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
 
### TestRunConfiguration tests
For testing a testRunConfiguration, there are several changes from the RunConfiguration. When we execute a test configuration
we want to check:

* the test tree structure: all rego tests are present in the tree and have the valid status (Pass, Failed)
* the output of each node in the tree (ie the output corresponding to one rego test)

To execute the configuration, we use the method `executeAndGetTestRoot`. This method returns the test tree.

To test the tree structure, we use the method `getFormattedTestTree` that returns the tree as a string for
easy comparison with an expected output.  
Example of the output of `getFormattedTestTree`
```
[root](-)
.data.test.main.test_rule_2_should_be_ko(-)
.data.test.main.test_rule1_should_be_ok(+)
.data.test.main.test_should_raise_error(-)
```


To test the output of each node, we use the method `checkTreeErrorMsg(root)`. This method walks the test tree
and checks whether the node output matches a regex defined in a file. By convention, this file must be named after the name of the node
and located in the folder `${dataPath}/${testName}`  

Please look at [TestRunConfigurationExecutionOpaTest](../../src/test/kotlin/org/openpolicyagent/ideaplugin/ide/runconfig/test/TestRunConfigurationExecutionOpaTest.kt)
for an example.
