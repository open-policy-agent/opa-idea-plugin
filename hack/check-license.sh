#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail
 

LICENSE_HEADER="/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */"
 
file_with_no_lic=""
 
echo "Checking files contains license header..."
 
# we use git ls-files instead of find command because we don't care about non versionned filed (eg generated code)
for file in $(git ls-files | grep -E '\.kt$|\.java$'); do
    if ! grep -qF "$(tr -d '\n'  <<<"$LICENSE_HEADER")"  <<<"$( tr -d '\n'  <"$file" )"; then
      file_with_no_lic="${file_with_no_lic}\n${file}"
    fi
done
 

if [ -z "${file_with_no_lic}" ]; then
    echo "Check OK"
    exit 0
else
    echo "Error: the follwing files don't contains the license header"
    echo -e  "${file_with_no_lic}"
    exit 1
fi

