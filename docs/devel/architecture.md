# Table of Content
<!-- toc -->
- [Project structure](#project-structure)
  - [IDE specific features](#ide-specific-features)
- [Lexer](#lexer)
- [Parser](#parser)
- [Useful methods](#useful-methods)
- [Testing strategy](#testing-strategy)
  - [Tests resources](#tests-resources)
    - [Example](#example)
  - [Parsing tests](#parsing-tests)
  - [RunConfiguration tests](#runconfiguration-tests)
    - [TestRunConfiguration tests](#testrunconfiguration-tests)
<!-- /toc -->

*to update toc, please read [this page](../../hack/README.md).*

# Project structure

```
.
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties # gradle properties file
├── docs
│   ├── devel # technical documentation of the project
│   └── user
├── hack # set of useful script to manage the project
├── idea # source code of features only available for IntelliJ
│   └── src
│       ├── main
│       │   ├── kotlin
│       │   │
│       │   └── resources
│       │       └── META-INF
│       │           └── idea-only.xml # subset of the plugin.xml that contains features only available in IntelliJ
│       └── test
│           ├── kotlin
│           └── resources
│               └── META-INF
│                   └── plugin.xml
├── plugin # module to build/run/publish opa-ida-plugin plugin
│   └── src
│       └── main
│           └── resources
│               └── META-INF
│                   ├── plugin.xml
│                   └── pluginIcon.svg
└── src # source code common to all Ide 
    ├── main 
    │   ├── gen # lexer and parser generated code
    │   ├── grammar # grammar and lexer definition
    │   ├── kotlin
    │   │   └── org.openpolicyagent.ideaplugin
    │   │       ├── ide # code relative to ide action (eg run opa test, create a rego file...)
    │   │       │   ├── actions
    │   │       │   ├── colors
    │   │       │   ├── commenter
    │   │       │   ├── extensions
    │   │       │   ├── highlight
    │   │       │   ├── linemarkers
    │   │       │   ├── runconfig
    │   │       │   └── todo
    │   │       ├── lang # code relative to rego language
    │   │       │   ├── RegoFiletype.kt
    │   │       │   ├── RegoIcons.kt
    │   │       │   ├── RegoLanguage.kt
    │   │       │   ├── lexer
    │   │       │   ├── parser
    │   │       │   └── psi
    │   │       ├── opa
    │   │       │   └── tool
    │   │       └── openapiext # extension methods that could be in IDEA SDK
    │   └── resources
    │       └── META-INF
    │          └── core.xml # subset of the plugin.xml that contains features available in all IDE
    └── test
        ├── kotlin # test source code
        └── resources # assets needed by tests
```

The project is built using Gradle. We use the [gradle kotlin dsl](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
because it contains the tasks `generateRegoLexer` and `generateRegoParser` that automatically generate
lexer and parser code before compiling Kotlin code.

## IDE specific features
The plugin may be integrated with some features only available in certain IDE or certain plugin. For example, the creation of the `rego` project is only available in IntelliJ. To be able to build different "flavors" of the plugin, we
have split the project into several Gradle modules. Each module except `root` and `plugin` support some features only
available to a certain IDE or the integration with a plugin.  
It allows us to separate code only available for one IDE and avoid wrong dependencies

The modules are  
* `:`: the root/core module that contains all code common to every IDE
* `plugin`: module to build/run/publish the plugin
* `idea`: code specific to IntelliJ IDE

the module `plugin` contains the `plugin.xml` of the plugin. This file includes the plugin descriptor (ie plugin.xml) of
the other modules as optional dependencies. To avoid name conflict the other plugin descriptors are not named
`plugin.xml`. This module will build the plugin artifact (ie a zip)  that contains the `jar` of all modules. This `zip` can
be installed on any IDE, only the feature compatible with the IDE will be loaded.

*note: If you want to implement integration with another plugin/IDE, you should create a new Gradle module for that.*

# Lexer
The lexer is defined by the [RegoLexer.flex](../../src/main/grammar/RegoLexer.flex) file written in [JFlex](https://www.jflex.de/)
format. Code can be generated thanks to `generateRegoLexer` gradle tasks.

# Parser
The parser is defined by the [Rego.bnf](../../src/main/grammar/Rego.bnf) file written in [Grammar kit ](https://github.com/JetBrains/Grammar-Kit)
format. It looks like a `bnf` grammar. `Grammar kit` also generates the [PSI](https://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi.html)
(Program Structure Interface) which is a kind of AST but with more information. Lots of IDE features are plugged into the PSI.
Once the PSI is generated, some features like commenting code are really straightforward to implement.

Code can be generated thanks to `generateRegoParser` gradle tasks.

# Useful methods
Some useful extension methods has been coded under:
* [PsiElementExt.kt](../../src/main/kotlin/org/openpolicyagent/ideaplugin/lang/psi/PsiElementExt.kt)
* [PsiFileExt.kt](../../src/main/kotlin/org/openpolicyagent/ideaplugin/lang/psi/PsiFileExt.kt)
* [openapiext](../../src/main/kotlin/org/openpolicyagent/ideaplugin/openapiext)

Please take a look.

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

In your test class, you must override the `dataPath` field to indicate the folder containing the resources files. By 
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
