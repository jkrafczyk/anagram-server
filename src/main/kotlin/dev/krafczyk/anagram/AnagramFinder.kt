package dev.krafczyk.anagram

import kotlin.math.abs

class AnagramFinder(private val dictionary: Dictionary) {

    fun buildAnagrams(
        original: String,
        staticPrefix: String
    ): Sequence<String> {
        val completionsSequence = suggestCompletions(original, staticPrefix, "")

        return sequence {
            completionsSequence.forEach {
                if (it.remainder == "") {
                    yield(it.output)
                } else {
                    buildAnagrams(original, it.output).forEach { anagram ->
                        yield(anagram)
                    }
                }
            }
        }
    }

    fun suggestCompletions(
        original: String,
        staticPrefix: String,
        partialWord: String
    ): Sequence<Completion> {
        val sigOrig = buildWordSignature(original)
        val sigStaticPrefix = buildWordSignature(staticPrefix)
        val sigPartialWord = buildWordSignature(partialWord)
        val sigForCompletions = subtractWordSignature(sigOrig, sigStaticPrefix)

        require(isWordSubset(sigStaticPrefix, sigOrig)) { "Requested prefix must be (partial) anagram of input word" }

        val signatureSequence = suggestAnagramSubsetSignatures(sigForCompletions, sigPartialWord)

        return sequence {
            for (completionSig in signatureSequence) {
                if (completionSig.length < 3) { continue }
                val remainder = subtractWordSignature(sigForCompletions, completionSig)
                for (word in dictionary[completionSig]) {
                    if (!word.startsWith(partialWord)) {
                        continue
                    }
                    val full = staticPrefix + (if (staticPrefix > "") " " else "") + word
                    yield(Completion(original, staticPrefix, partialWord, word, full, remainder))
                }
            }
        }
    }

    private fun suggestAnagramSubsetSignatures(superset: Signature, requiredSubset: Signature):
        Sequence<Signature> {
        require(isWordSubset(requiredSubset, superset)) {
            "Requested word-start must be (partial) anagram of input word"
        }

        val preferredLength = dictionary.medianLength * 2 / 3
        return sequence {
            for (length in dictionary.signaturesByLength.keys.sortedBy { abs(it - preferredLength) }) {
                if (length > superset.length) {
                    continue
                }
                for (sigCandidate in dictionary.signaturesByLength[length]!!) {
                    if (!isWordSuperset(sigCandidate, requiredSubset) || !isWordSubset(sigCandidate, superset)) {
                        continue
                    }
                    yield(sigCandidate)
                }
            }
        }
    }
}

data class Completion(
    val original: String,
    val staticPrefix: String,
    val partialWord: String,
    val completedWord: String,
    val output: String,
    val remainder: Signature
)
