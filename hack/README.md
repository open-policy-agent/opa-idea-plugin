# Utility scripts
<!-- toc -->
- [Verify and update table of content scripts](#verify-and-update-table-of-content-scripts)
  - [verify-toc.sh](#verify-tocsh)
  - [update-toc.sh](#update-tocsh)
<!-- /toc -->
## Verify and update table of content scripts

Prerequisites:
* curl
* internet access to download tools

### verify-toc.sh
This script checks if the `table of content` (a.k.a `toc`) of all documents in the repository must be updated. If any change is needed it will prompt the new toc.

*note: This script can be run from any folder within the repository*

Example with document that need update:
```bash
$ ./hack/verify-toc.sh
2020/06/01 22:28:53 ./hack/README.md: changes found:
- [Verify and update table of content scripts](#verify-and-update-table-of-content-scripts)
  - [verify-toc.sh](#verify-tocsh)
  - [update-toc.sh](#update-tocsh)
Cleaning...

```
Example with a document that does not need an update:

```bash
$ ./hack/verify-toc.sh
Cleaning...

```

### update-toc.sh
This script will update the  `table of content` (a.k.a `toc`) of all documents in the repo. The output and the return code are the same if documents are updated or not.

*note: This script can be run from any folder within the repository*

```bash
$ ./hack/update-toc.sh 
  Cleanning...
```

