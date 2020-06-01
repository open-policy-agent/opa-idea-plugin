#!/usr/bin/env bash

# widely inspired from https://github.com/kubernetes/enhancements/blob/master/hack/update-toc.sh but use the curl instead
# of go to get binary. Thansk to the kubernetes authors

# This scirpt update toc of all md file in the repo

set -o errexit
set -o nounset
set -o pipefail

# version of mtdodc
VERSION="v1.0.0"

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"
cd "${ROOT}"

# create a temporary directory where mtdoc is dowload
TMP_DIR=$(mktemp -d)
cd "${TMP_DIR}"

exit_hook(){
 echo "Cleanning..."
 rm -rf "${TMP_DIR}"
}

trap exit_hook EXIT

#Download mtdoc and add it to path
curl -s "https://github.com/tallclair/mdtoc/releases/download/${VERSION}/mdtoc" -o mdtoc
export PATH="${TMP_DIR}:${PATH}"

cd "${ROOT}"

# Update tables of contents if necessary.
grep --include='*.md' -rl . -e '<!-- toc -->' | xargs mdtoc --inplace