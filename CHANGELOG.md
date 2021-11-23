# Release notes for v0.6.0

## Changes by Kind

### Feature

- Make plugin compatible with platform version 203 and 211 ([#101](https://github.com/open-policy-agent/opa-idea-plugin/pull/101), [@asadali](https://github.com/asadali))


# Release notes for v0.5.0

## Changes by Kind

### Refactoring

- Grammar: rename rules:
  - `expr` to `literal-expr`
  - `expr2` to `expr` ([#93](https://github.com/open-policy-agent/opa-idea-plugin/pull/93), [@vgramer](https://github.com/vgramer))
- Remove references to old repo `vgramer/opa-idea-plugin`. The repository has been transferred to `open-policy-agent` organization ([#83](https://github.com/open-policy-agent/opa-idea-plugin/pull/83), [@vgramer](https://github.com/vgramer))

### Bug or Regression

- Grammar:  allow factor expression (ie parenthesis ).  These expressions were passed as an error:
  - `a4:= (1 - 3)  - (2 +5)`
  - `reverse(l) = [l[j] | _ = l[i]; j := (count(l) - 1) - i]` ([#92](https://github.com/open-policy-agent/opa-idea-plugin/pull/92), [@vgramer](https://github.com/vgramer))
- Grammar: allow complex expression as array index. These expressions were parsed as an error:
  - `x:= x[minus(count(x),1)]`
  - `index_last(l, x) = t[minus(count(t), 1)] { #something} else = -1 { # something}`
  - `rule1{  x:= x[count(x) - 1)] }` ([#91](https://github.com/open-policy-agent/opa-idea-plugin/pull/91), [@vgramer](https://github.com/vgramer))
- Grammar: allow complex expression for array and object value. these expressions were parsed as an error:
  - `arr7 := [1 + 2 - 4, count(arr6) / abs(arr5[0])`
  - `a6 := {  a: 1 > 2, b: { c: object.get(a2, a, default) +  abs(c) - 2 }}` ([#90](https://github.com/open-policy-agent/opa-idea-plugin/pull/90), [@vgramer](https://github.com/vgramer))
- Grammar: allow empty query in else clause. These expressions were reported as an error
  - `foo = true { input.x < input.y } else = false`
  - `foo = true { false } else = { true }` ([#85](https://github.com/open-policy-agent/opa-idea-plugin/pull/85), [@vgramer](https://github.com/vgramer))
  

# Release notes for v0.4.0

## Changes by Kind

### Feature

- Implement auto closing quote feature ([#69](https://github.com/open-policy-agent/opa-idea-plugin/pull/69), [@vgramer](https://github.com/vgramer))
- Run [intellij-plugin-verifier](https://github.com/JetBrains/intellij-plugin-verifier) to check plugin binary compatibility in github action

  1. update `org.jetbrains.intellij` Gradle plugin because the new version offers a task to run the plugin verifier. Consequently, remove the `ideaDependencyCachePath`customization which is not needed anymore.
  2. add a job to run plugin verifier ([#62](https://github.com/open-policy-agent/opa-idea-plugin/pull/62), [@vgramer](https://github.com/vgramer))

### Bug or Regression

- Fix tests for github action and macOS Big Sur ([#73](https://github.com/open-policy-agent/opa-idea-plugin/pull/73), [@vgramer](https://github.com/vgramer))
- Grammar: allow import between rules and complex expression in else statement ([#80](https://github.com/open-policy-agent/opa-idea-plugin/pull/80), [@vgramer](https://github.com/vgramer))
- Grammar: don't allow multi-line in strings ([#67](https://github.com/open-policy-agent/opa-idea-plugin/pull/67), [@vgramer](https://github.com/vgramer))
- Grammar: fix rule-head and expr-infix
  -  `a:= 1 + 2` was parsed as an error
  - assignment was valid inside affectation (eg `a := b =  1 + 2`) but should not.
  - `a := 1 + 2 +3` was parsed as an error ([#74](https://github.com/open-policy-agent/opa-idea-plugin/pull/74), [@vgramer](https://github.com/vgramer))
  

# Release notes for v0.3.0

## Changes by Kind

### Bug or Regression

- Grammar: allow to use of infix operation as argument of a function. This expression was parsed as an error:
  - `ARule(){ x := count({x} & {y}) }` ([#64](https://github.com/open-policy-agent/opa-idea-plugin/pull/64), [@vgramer](https://github.com/vgramer))
  
# Release notes for v0.2.0

## Changes by Kind

### Feature

- Implements auto-closing feature (brace, parenthesis and bracket ) ([#41](https://github.com/open-policy-agent/opa-idea-plugin/pull/41), [@vgramer](https://github.com/vgramer))
- Improve speed and security of CI Job:
  - Cache gradle dependencies between run.
  - check the signature of the Gradle wrapper. More info at [https://github.com/gradle/wrapper-validation-action](https://github.com/gradle/wrapper-validation-action) ([#55](https://github.com/open-policy-agent/opa-idea-plugin/pull/55), [@vgramer](https://github.com/vgramer))

### Documentation

- Fix typo in the documentation ([#54](https://github.com/open-policy-agent/opa-idea-plugin/pull/54), [@lukasz-kaminski](https://github.com/lukasz-kaminski))
- Update instruction du use stable channel when installing opa though Jetbrains market place ([#53](https://github.com/open-policy-agent/opa-idea-plugin/pull/53), [@vgramer](https://github.com/vgramer))

### Bug or Regression

- Grammar: access to function's return value without assigning it to a variable. These expressions were parsed as an error:
  - `fun_obj(x) = h { h := {"a": {"b":  [x,2]}} }`
  - `aRule {  a = fun_obj(1).a }` ([#59](https://github.com/open-policy-agent/opa-idea-plugin/pull/59), [@vgramer](https://github.com/vgramer))

# Release notes for v0.1.0

## Changes by Kind

### Refactoring

- Add tests on grammar and validate test files with `opa check` command ([#40](https://github.com/open-policy-agent/opa-idea-plugin/pull/40), [@irodzik](https://github.com/irodzik))
- Refactoring of Opa action codes and improve comment ([#38](https://github.com/open-policy-agent/opa-idea-plugin/pull/38), [@asadali](https://github.com/asadali))

### Feature

- Add the following opa action
  - trace of selected text to code
  - check ([#37](https://github.com/open-policy-agent/opa-idea-plugin/pull/37), [@frankiecerk](https://github.com/frankiecerk))
- Add the script to check that versioned source files (`*.java`; `*.kt`) contains the license header and call it in github action ([#49](https://github.com/open-policy-agent/opa-idea-plugin/pull/49), [@vgramer](https://github.com/vgramer))
- Build flavored ide. Some features like creating a rego project is only available on IDEA. This PR allows building the different flavors of the plugin for each ide version. This is transparent for the user (he see only one plugin market place) ([#44](https://github.com/open-policy-agent/opa-idea-plugin/pull/44), [@vgramer](https://github.com/vgramer))
- Grammar:  add `%` operator and tests for grammar ([#39](https://github.com/open-policy-agent/opa-idea-plugin/pull/39), [@irodzik](https://github.com/irodzik))
- Implements auto-closing feature (brace, parenthesis and bracket ) ([#41](https://github.com/open-policy-agent/opa-idea-plugin/pull/41), [@vgramer](https://github.com/vgramer))
- Index TOTO in rego comment ([#26](https://github.com/open-policy-agent/opa-idea-plugin/pull/26), [@vgramer](https://github.com/vgramer))
- OpaTestRunConfiguration: add the full output of the command to the root node ([#52](https://github.com/open-policy-agent/opa-idea-plugin/pull/52), [@vgramer](https://github.com/vgramer))
- Support for opa test, test coverage on project workspace and opa check on current open document, shows output in console window (all support for console window showing output is in extensions/OPAActionToolWindow) ([#30](https://github.com/open-policy-agent/opa-idea-plugin/pull/30), [@frankiecerk](https://github.com/frankiecerk))

### Documentation

- Add opa icon for plugin installation ([#36](https://github.com/open-policy-agent/opa-idea-plugin/pull/36), [@vgramer](https://github.com/vgramer))
- Add user and tech documentation ([#3](https://github.com/open-policy-agent/opa-idea-plugin/pull/3), [@vgramer](https://github.com/vgramer))
- Instructions for download plugin from Jetbrains market place ([#48](https://github.com/open-policy-agent/opa-idea-plugin/pull/48), [@frankiecerk](https://github.com/frankiecerk))

### Bug or Regression

- Fix test run configuration:
  - Don't discriminate test configurations based on the file name:
    - user can eval or test a package in any rego file
    - user can test any rule starting with `test_`in any rego file
    - user can evaluate any rule not starting with `test_` in any rego file
  - Make bundle dir parameter required to create a test configuration => avoid message `Testing Framework quit unexpectedly` when creating a test configuration from gutter
  - Generated name of the runConfiguration start with the name of the command to easily know which configuration type will be generated ([#50](https://github.com/open-policy-agent/opa-idea-plugin/pull/50), [@vgramer](https://github.com/vgramer))
- Grammar: fixed string regex
  highlighting for rule heads ([#19](https://github.com/open-policy-agent/opa-idea-plugin/pull/19), [@frankiecerk](https://github.com/frankiecerk))