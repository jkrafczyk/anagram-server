# Anagram Search Server

An application to help in building anagrams from names or sentences.

## Requirements

* JDK 17

## Quick start

* `./gradlew bootRun`

The application should log a message like `Started AnagramApplicationKt in 14.842 seconds (process running for 14.996)`.
Afterwards, you can interact with the search server via HTTP on port 7998.

For more convenient usage, shell scripts are present in the repository:

* `anagrams.sh` for enumerating anagrams of a phrase
* `suggest-word.sh` for showing words that can be built from a phrase, even if some of the phrases letters remain unused.

Both scripts require a dictionary name as the first argument, followed by the phrase to build anagrams for.
Currently, three dictionaries are loaded at application startup: 'german', 'english', and 'combined'

```bash
# Show english-language anagrams of eleven plus two
$ ./anagrams.sh english "eleven plus two"
[...] #LOTS of output
twelve plus one
[...] #LOTS of output

# Show english-language anagrams of twelve plus one, restricted to those starting with "pole"
$ ./anagrams.sh english "twelve plus one" "pole"
[...]
pole Venus welt
[...]

# Suggest words for starting an anagram for "king charles of england"
./suggest-word.sh english "king charles of england
relief
```

## Where'd the data come from?

* german: https://gist.github.com/MarvinJWendt/2f4f4154b8ae218600eb091a5706b5f4
* english: cat /usr/share/dict/* in MacOS Ventura
* english-large: https://github.com/dwyl/english-words
* profanity: https://www.cs.cmu.edu/~biglou/resources/
