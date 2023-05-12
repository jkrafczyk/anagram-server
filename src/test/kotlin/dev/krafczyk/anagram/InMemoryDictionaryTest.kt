package dev.krafczyk.anagram

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class InMemoryDictionaryTest {

    val words = setOf(
        "hello",
        "world",
        "one",
        "noe",
        "Oen",
        "supercalifragilisticexpialidocious"
    )
    val dict = InMemoryDictionary("test", words)

    @Test
    fun `should provide sensible word-length stats`() {
        dict.medianLength shouldBeEqualTo 5
        dict.meanLength shouldBeEqualTo 13
    }

    @Test
    fun `should provide sets of contained signatures`() {
        dict.allSignatures shouldBeEqualTo setOf(
            buildWordSignature("hello"),
            buildWordSignature("world"),
            buildWordSignature("one"),
            buildWordSignature("supercalifragilisticexpialidocious"),
        )

        dict.signaturesByLength[3] shouldBeEqualTo setOf(
            buildWordSignature("one")
        )
        dict.signaturesByLength[5] shouldBeEqualTo setOf(
            buildWordSignature("hello"),
            buildWordSignature("world")
        )
        dict.signaturesByLength[34] shouldBeEqualTo setOf(
            buildWordSignature("supercalifragilisticexpialidocious")
        )

        dict[buildWordSignature("one")] shouldBeEqualTo setOf("one", "noe", "Oen")
    }
}
