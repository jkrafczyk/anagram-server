#!/usr/bin/env bash
set -euo pipefail

function usage() {
  cat <<EOF
Usage: $0 dictionary phrase [anagram prefix]

Examples:
To putput all english-language anagrams of "eleven plus two":
$0 english "eleven plus two"

To output all english-language anagrams of "eleven plus two" which start with "twelve":
$0 english "eleven plus two" "twelve"
EOF
  exit 0
}

if [[ "$#" -lt 2 || "$#" -gt 3 ]]; then
  usage
fi

dict="$1"
phrase="$2"
prefix="${3:-}"

curl \
  --get \
  --silent \
  http://localhost:7998/api/anagrams \
  --data-urlencode "dict=$dict" \
  --data-urlencode "q=$phrase" \
  --data-urlencode "prefix=$prefix" \
