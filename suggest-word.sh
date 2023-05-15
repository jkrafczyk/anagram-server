#!/usr/bin/env bash
set -euo pipefail

function usage() {
  cat <<EOF
Usage: $0 dictionary phrase [anagram prefix [partial word]]

Examples:
To suggest a starting word that can be used for anagrams for "eleven plus two":
$0 english "eleven plus two"

To suggest a next word for an anagram of "even plus two", given the start "twelve":
$0 english "eleven plus two" "twelve"

To suggest a next word for an anagram of "eleven plus two",
given the start "twelve", filtering for words starting with "pl"
$0 english "eleven plus two" "twelve" "pl"
EOF
  exit 0
}

if [[ "$#" -lt 2 || "$#" -gt 4 ]]; then
  usage
fi

dict="$1"
phrase="$2"
prefix="${3:-}"
partial="${4:-}"

curl \
  --get \
  --silent \
  http://localhost:7998/api/complete_safe \
  --data-urlencode "dict=$dict" \
  --data-urlencode "q=$phrase" \
  --data-urlencode "prefix=$prefix" \
  --data-urlencode "partialWord=$partial"

  #| jq -r '(.output + " (Remainder: " + .remainder + ")")'
