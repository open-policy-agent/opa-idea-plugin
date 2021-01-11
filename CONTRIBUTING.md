# Table of Content
<!-- toc -->
- [Introduction](#introduction)
- [Contribution](#contribution)
  - [Developer Certificate Of Origin](#developer-certificate-of-origin)
- [Helpful resources](#helpful-resources)
<!-- /toc -->
*to update toc, please read [this page](../../hack/README.md).*

# Introduction 
The project is code in [Kotlin](https://kotlinlang.org/) because handle `NullPointerException` is a nightmare. Moreover,
Kotlin brings lots of cool features. If you are a `Java` developer learning curve is really smooth. [JetBrains](https://www.jetbrains.org/) 
has published a really good [MOOC](https://www.coursera.org/learn/kotlin-for-java-developers)

You can setup your environment by following this [guide](docs/devel/setup_development_env.md). 
To learn more about the project architecture please read this [document](docs/devel/architecture.md)

# Contribution
If you are contributing code, please consider the following:
* code should be accompanied by test when possible
* all tests must passed
* add link to IntelliJ SDK documentation in JavaDoc, if the SDK documentation does not exist, please write a section in project architecture.

## Developer Certificate Of Origin

The OPA project requires that contributors sign off on changes submitted to OPA repositories. As opa-idea-plugin is now part of the OPA repositories, opa-idea-plugin requires contributors sign off on changes as well. The [Developer Certificate of Origin (DCO)](https://developercertificate.org/) is a simple way to certify that you wrote or have the right to submit the code you are contributing to the project.

The DCO is a standard requirement for Linux Foundation and CNCF projects.

You sign-off by adding the following to your commit messages:

    This is my commit message

    Signed-off-by: Random J Developer <random@developer.example.org>

Git has a `-s` command line option to do this automatically.

    git commit -s -m 'This is my commit message'

You can find the full text of the DCO here: https://developercertificate.org/

# Helpful resources
* [IntelliJ sdk documentation](https://www.jetbrains.org/intellij/sdk/docs/intro/welcome.html)
* [rust IntelliJ plugin](https://github.com/intellij-rust/intellij-rust)
* [IntelliJ Plugins by Alec Strong](https://www.youtube.com/watch?v=-l5ChzRiUHE)
* [kotlin-for-java-developers](https://www.coursera.org/learn/kotlin-for-java-developers)
