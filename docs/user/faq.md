# Table of Content
<!-- toc -->
- [Tests](#tests)
  - [Why test duration is equal to Zero.](#why-test-duration-is-equal-to-zero)
  - [Why i dont see pass tests](#why-i-dont-see-pass-tests)
<!-- /toc -->

*to update toc, please read [this page](../../hack/README.md).*

# Tests
##  Why test duration is equal to Zero.
If you don't make external call (eg http call), evaluate and test a policy is really quick (few hundreds of microseconds)
but `IntelliJ` store duration with millisecond precision. Consequently if your test take less than a millisecond to be
executed it will be show as `0` in the interface.

Never the less you can see the test duration in the output console.

## Why i dont see pass tests
By default IntelliJ do not show pass tests. To show them you have to click on the "check" icon.

![template editor](img/show_pass_tests.png)