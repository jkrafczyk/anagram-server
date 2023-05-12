package dev.krafczyk.anagram

interface Dictionary {
    val name: String

    val allSignatures: Set<Signature>

    val signaturesByLength: Map<Int, Set<Signature>>

    val meanLength: Int

    val medianLength: Int

    operator fun get(signature: Signature): Set<String>
}
