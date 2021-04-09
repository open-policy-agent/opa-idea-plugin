# Table of Content
<!-- toc -->
- [Introduction](#introduction)
- [How to write release-note](#how-to-write-release-note)
- [How do you know release-notes belong to a version](#how-do-you-know-release-notes-belong-to-a-version)
- [Update the CHANGELOG](#update-the-changelog)
<!-- /toc -->

*to update toc, please read [this page](../../../hack/README.md).*

# Introduction

The release notes are collected thanks to k8s [release-notes-generator tools](https://github.com/kubernetes/release#release-notes).
The release notes look like this:
```markdown
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
```

The tool extracts the release notes information from the PR:
* `labels`: which categorize the PR (eg `kind`)
* `code-block` (in PR description): which contains the text of the release note formatted as markdown

The release notes may also be generated in json format
Example:
```json
{
  "49": {
    "commit": "be8457fbb5a11b831781f3fd7adc3b5685a004ee",
    "text": "add the script to check that versioned source files (`*.java`; `*.kt`) contains the license header and call it in github action",
    "markdown": "Add the script to check that versioned source files (`*.java`; `*.kt`) contains the license header and call it in github action ([#49](https://github.com/open-policy-agent/opa-idea-plugin/pull/49), [@vgramer](https://github.com/vgramer))",
    "author": "vgramer",
    "author_url": "https://github.com/vgramer",
    "pr_url": "https://github.com/open-policy-agent/opa-idea-plugin/pull/49",
    "pr_number": 49,
    "areas": [
      "CI"
    ],
    "kinds": [
      "feature"
    ],
    "feature": true
  },
  // other release note ...
}
```

# How to write release-note
The pr must be
1.labeled with
* kind/<the appropriate kind>: this will categorize the PR in the appropriate section (eg Refactoring, Feature, Bug ...)
* area/<the appropriate area>: this will categorize the kind of code (Grammar,  Highlighting...) impacted by this PR(currently not used in markdown template)
1. have `release-note` block

For pull requests with a release note:

    ```release-note
    Your release note here
    ```

For pull requests that require additional action from users switching to the new release, include the string "action required" (case insensitive) in the release note:

    ```release-note
    action required: your release note here
    ```

For pull requests that don't need to be mentioned at release time, use the `/release-note-none` Prow command to add the `release-note-none` label to the PR. You can also write the string "NONE" as a release note in your PR description:

    ```release-note
    NONE
    ```

# How do you know release-notes belong to a version
This tool looking for all commits from the last release on branch master and extracts PR number from the commit message.
If a commit does not contain the PR number it's ignored.

In this example, the PR number is `98`
```
grammar - string:  fix escape quote (#98)
```

In GitHub when we merge a PR with `create a merge commit` or `squash and merge` strategy, it automatically adds the PR
number in the commit message. So these strategies are preferred to `rebase` one.

# Update the CHANGELOG
Every time we merge a PR on `master` branch, the GitHub action `upset_release_notes_pr_on_push_on_master.yml` is triggered.
This workflow will create or update PR that generate the next release notes and append it to [CHANGELOG](../../CHANGELOG.md)

The branch used for the PR is `update-release-notes`.  
This pr must be merged last, just before releasing.

1. edit the title of last release notes to change `NEXT_VERSION`by the actual version (you can do it in GitHub interface)
1. merge pr using **squashing** strategy
1. tag the `master` branch with the actual version (this will create a Github release and publish the plugin on Jetbrains store)