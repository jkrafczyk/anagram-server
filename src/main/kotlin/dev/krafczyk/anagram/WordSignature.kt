package dev.krafczyk.anagram

typealias Signature = String

fun buildWordSignature(word: String): Signature {
    return word.lowercase().filter { c -> c.isLetter() }.toList().sorted().joinToString("")
}

fun isWordSuperset(supersetCandidate: Signature, subsetCandidate: Signature): Boolean {
    val result = supersetCandidate.toMutableList()
    subsetCandidate.forEach { c ->
        if (result.contains(c)) {
            result.removeAt(result.indexOf(c))
        } else {
            return false
        }
    }
    return true
}

fun isWordSubset(subsetCandidate: Signature, supersetCandidate: Signature) =
    isWordSuperset(supersetCandidate, subsetCandidate)

fun subtractWordSignature(left: Signature, right: Signature): Signature {
    val result = left.toMutableList()
    right.forEach { c ->
        if (result.contains(c)) {
            result.removeAt(result.indexOf(c))
        }
    }
    return result.sorted().joinToString("")
}
