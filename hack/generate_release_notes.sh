#!/usr/bin/env bash

########################################################################################################################
# This script generate the release-notes for next version in ${NEXT_RELEASE_NOTES_FILE} ie
# repository_root/next-release-notes.md". It collect release notes from PR description merged into 'master' between the
# last github release and HEAD.
# For more information about the collection please read the documentation: docs/devel/releasing/release-notes-generation.md
#
# This script is intended to be run in github action but can run in local.
#
# The following tools must be installed and be in the PATH
#  * hub: the github cli (https://hub.github.com/)
#  * release-notes: the k8s release-notes-generator tools (https://github.com/kubernetes/release#release-notes)
#                   can be install thanks to go get command:
#                   GO111MODULE=on go get k8s.io/release/cmd/release-notes@<version>
#
# The following environment variables must be set (there are provided by github action context):
#   * GITHUB_REPOSITORY: The owner and repository name. For example, octocat/Hello-World
#   * GITHUB_TOKEN: github personal token with read right on the repository
#
# Example:
# $ export GITHUB_REPOSITORY='vgramer/release-note-test'
# $ export GITHUB_TOKEN='the-personnal-token'
# $ hack/generate_release_notes.sh
#
# org=vgramer repo=release-note-test
# last release found. it's tag 'v3.0.0'
# tag 'v3.0.0' point on commit '88ab66225555776afdbad09cd8424991852f82ae'
# INFO Using output format: markdown
# INFO Gathering release notes
# INFO Starting to process commit 1 of 4 (25.00%): 7b0505715801ef0c90c0729be1a35fe46482141e
# INFO Starting to process commit 2 of 4 (50.00%): d9489b298d66b6dbf2a97661e1cc847c53216822
# INFO Starting to process commit 3 of 4 (75.00%): d05bed5ba580ba7e0a7d5fd8f995f4a5c52a0a42
# INFO Starting to process commit 4 of 4 (100.00%): 88ab66225555776afdbad09cd8424991852f82ae
# INFO No PR found for commit d9489b298d66b6dbf2a97661e1cc847c53216822: no PR IDs found in the commit message
# INFO No PR found for commit 7b0505715801ef0c90c0729be1a35fe46482141e: no PR IDs found in the commit message
# INFO No matches found when parsing PR from commit SHA 7b0505715801ef0c90c0729be1a35fe46482141e
# INFO No matches found when parsing PR from commit SHA d9489b298d66b6dbf2a97661e1cc847c53216822
# INFO Got PR #8 for commit: 88ab66225555776afdbad09cd8424991852f82ae
# INFO Got PR #10 for commit: d05bed5ba580ba7e0a7d5fd8f995f4a5c52a0a42
# INFO PR #10 seems to contain a release note
# INFO Got 1 release notes, performing rendering
# INFO Release notes written to file: /Users/vince/workspace/perso/final/release-note-test/next-release-notes.md
########################################################################################################################

set -o errexit
set -o nounset
set -o pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"
source "${ROOT}/hack/util.sh"

function check_environment() {
  util::require-hub
  util::command_exists "release-notes" || util::fatal 'release-notes not found in path. Please install it before running script. "GO111MODULE=on go get k8s.io/release/cmd/release-notes@<version>"'

  set +o nounset
  if [[ -z "${GITHUB_TOKEN}" ]]; then
    util::fatal "'GITHUB_TOKEN' environment variable is not defined or empty"
  fi

  if [[ -z "${GITHUB_REPOSITORY}" ]]; then
    util::fatal "'GITHUB_REPOSITORY' environment variable is not defined or empty"
  fi
  set -o nounset

  [[ ${GITHUB_REPOSITORY} =~ ^[^/]+/[^/]+$ ]] || util::fatal "could not extract organization and repository from GITHUB_REPOSITORY env var. GITHUB_REPOSITORY='${GITHUB_REPOSITORY}'"
  org=$(echo "${GITHUB_REPOSITORY}" | cut -d '/' -f 1)
  repo=$(echo "${GITHUB_REPOSITORY}" | cut -d '/' -f 2)
}

check_environment

echo "org=${org} repo=${repo}"

# format '%pI %T%n' -> "publish_date_ISO_8601 tag\n". eg:2 021-03-24T20:10:00Z v3.0.0
last_release_tag="$(hub release -f '%pI %T%n' | sort -nr | head -1 | cut -d ' ' -f 2)"

if [[ -z "${last_release_tag}" ]]; then
  echo "no release found on repository. fallback to first commit"
  start_sha="$(git rev-list --max-parents=0 HEAD)"
  echo "first commit sha is '${start_sha}'"
else
  echo "last release found. it's tag '${last_release_tag}'"
  start_sha=$(git rev-parse "${last_release_tag}")
  echo "tag '${last_release_tag}' point on commit '${start_sha}'"
fi

end_sha=$(git rev-parse HEAD)

# generate release notes:
#  * for pr merged in master by any users (--required-author='').
#  * without go dependencies report (--dependencies=false)
release-notes --start-sha "${start_sha}" \
  --end-sha "${end_sha}" \
  --required-author='' \
  --branch master \
  --org "${org}" \
  --repo "${repo}" \
  --go-template=go-template:"${ROOT}/.github/release-notes.tmpl" \
  --output "${NEXT_RELEASE_NOTES_FILE}" \
  --dependencies=false
