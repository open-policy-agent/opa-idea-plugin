#!/usr/bin/env bash

########################################################################################################################
#              This script contains utility variables and functions for others scripts                                 #
########################################################################################################################

set -o errexit
set -o nounset
set -o pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"


########################################################################################################################
#                                             Utility variables                                                        #
########################################################################################################################

# path to the next release notes file
export NEXT_RELEASE_NOTES_FILE="${ROOT}/next-release-notes.md"


########################################################################################################################
#                                             Utility functions                                                        #
########################################################################################################################

# Test if a command exist
# example:
#   util::command_exists "ls" || util::fatal "ls is not installed"
function util::command_exists() {
	command -v "$1" >/dev/null 2>&1
}

# Print an error message and exit with code 1
# args: the error message
# example:
#  $ util::fatal "something wrong happens"
#  Error: something wrong happens
function util::fatal() {
	echo "Error: $1"
	exit 1
}

# Check "hub" command exist and exit with error message if does not.
function util::require-hub() {
  util::command_exists "hub" || util::fatal 'hub not found in path. please install it before running script. see instruction at https://hub.github.com/'
}
