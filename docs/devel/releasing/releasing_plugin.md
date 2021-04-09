# Table of Content
<!-- toc -->
- [How to release the plugin](#how-to-release-the-plugin)
<!-- /toc -->

*to update toc, please read [this page](../../../hack/README.md).*

# How to release the plugin
1. check all tests are passing on master
1. ensure release-notes PR looks good. Edit the corresponding PR(s) if needed.
1. edit version in release-notes PR and merge it:
   1. edit the title of last release notes to change `NEXT_VERSION` by the actual version (must matches `v[0-9]+.[0-9]+.[0-9]+)`), you can do it in GitHub interface.
   1. merge pr using **squashing** strategy
1. tag the `master` branch with the actual version (this will create a Github release and publish the plugin on Jetbrains store)
