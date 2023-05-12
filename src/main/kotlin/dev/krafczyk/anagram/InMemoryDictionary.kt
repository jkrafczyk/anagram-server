package dev.krafczyk.anagram

import mu.KotlinLogging

class InMemoryDictionary(override val name: String, words: Set<String>) : Dictionary {

    companion object {
        val logger = KotlinLogging.logger { }
    }

    private val signatureToWords: Map<Signature, Set<String>>
    override val allSignatures: Set<Signature>
    override val signaturesByLength: Map<Int, Set<Signature>>
    override val meanLength: Int
    override val medianLength: Int
    init {
        require(words.isNotEmpty()) { "Empty dictionaries are not allowed" }
        require(name.isNotEmpty()) { "Nameless dictionaries are not allowed" }

        val signatureToWordsTmp = mutableMapOf<Signature, MutableSet<String>>()
        val signaturesLengthSet = mutableSetOf<Int>()
        val signatureLengthHistogram = mutableMapOf<Int, Int>()
        var signatureLengthSum = 0L

        logger.info { "Calculating signatures for ${words.size} words..." }
        val wordToSignature = words.associateWith { buildWordSignature(it) }

        logger.info { "Constructing signature set..." }
        val signaturesOnly = HashSet<Signature>(wordToSignature.size)
        signaturesOnly.addAll(wordToSignature.values)
        signaturesOnly.forEach {
            signatureToWordsTmp[it] = mutableSetOf()
        }

        logger.info { "Building signature/word mapping" }
        words.forEach { word ->
            val sig = wordToSignature[word]!!
            signatureToWordsTmp[sig]!!.add(word)
            signaturesLengthSet.add(sig.length)
            signatureLengthHistogram[sig.length] = signatureLengthHistogram.getOrDefault(sig.length, 0) + 1
            signatureLengthSum += sig.length
        }

        logger.info { "Freezing mapping" }
        signatureToWords = signatureToWordsTmp
        allSignatures = signatureToWords.keys

        logger.info { "Calculating statistics" }
        signaturesByLength = signaturesLengthSet.associateWith {
            length ->
            allSignatures.filter { it.length == length }.toSet()
        }

        medianLength = calculateMedianFromHistogram(signatureLengthHistogram)

        meanLength = (signatureLengthSum / allSignatures.size).toInt()
    }

    private fun calculateMedianFromHistogram(signatureLengthHistogram: MutableMap<Int, Int>): Int {
        var nSignaturesUntilCurrentLength = 0
        val targetNSignatures = allSignatures.size / 2
        for (length in signatureLengthHistogram.keys.sorted()) {
            if (nSignaturesUntilCurrentLength >= targetNSignatures) {
                return length
            }
            nSignaturesUntilCurrentLength += signatureLengthHistogram[length]!!
        }
        return 0
    }

    override fun get(signature: Signature): Set<String> = signatureToWords[signature] ?: emptySet()
}
