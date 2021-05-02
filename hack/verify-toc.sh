#!/usr/bin/env bash

# widely inspired from https://github.com/kubernetes/enhancements/blob/master/hack/verify-toc.sh but uses curl instead
# of go to get binary. Thanks to the kubernetes authors

# This script verify toc of all md file in the repo are up to date

set -o errexit
set -o nounset
set -o pipefail

# version of mdtoc
VERSION="v1.0.0"

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"
cd "${ROOT}"

# create a temporary directory where mdtoc is dowloaded
TMP_DIR=$(mktemp -d)
cd "${TMP_DIR}"

exit_hook(){
 echo "Cleaning..."
}

trap exit_hook EXIT

#Download mdtoc and add it to path
curl -s "https://github.com/tallclair/mdtoc/releases/download/${VERSION}/mdtoc" -o mdtoc
export PATH="${TMP_DIR}:${PATH}"

cd "${ROOT}"

# Update tables of contents if necessary.
grep --include='*.md' -rl . -e '<!-- toc -->' | xargs mdtoc --inplace --dryrun