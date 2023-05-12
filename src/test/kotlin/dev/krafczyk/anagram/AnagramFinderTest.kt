package dev.krafczyk.anagram

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.Test

class AnagramFinderTest {
    val words = setOf(
        "crazy",
        "fkk",
        "krafczyk",
        "blorb"
    )
    val dictionary: Dictionary = InMemoryDictionary("test", words)
    val anagramFinder = AnagramFinder(dictionary)

    @Test
    fun `should offer sensible suggestions for the first word of an anagram`() {
        val completions = anagramFinder.suggestCompletions("krafczyk", "", "").toList()

        completions.size shouldBeEqualTo 3
        completions shouldContain Completion(
            "krafczyk",
            "",
            "",
            "crazy",
            "crazy",
            buildWordSignature("fkk")
        )
        completions shouldContain Completion(
            "krafczyk",
            "",
            "",
            "fkk",
            "fkk",
            buildWordSignature("crazy")
        )
        completions shouldContain Completion(
            "krafczyk",
            "",
            "",
            "krafczyk",
            "krafczyk",
            buildWordSignature("")
        )
    }

    @Test
    fun `should offer sensible suggestions for later words of an anagram`() {
        val completions = anagramFinder.suggestCompletions("krafczyk", "crazy", "").toList()

        completions.size shouldBeEqualTo 1
        completions shouldContain Completion(
            "krafczyk",
            "crazy",
            "",
            "fkk",
            "crazy fkk",
            buildWordSignature("")
        )
    }

    @Test
    fun `should offer sensible suggestions for partially pre-defined words`() {
        val completions = anagramFinder.suggestCompletions("krafczyk", "", "cra").toList()

        completions.size shouldBeEqualTo 1
        completions shouldContain Completion(
            "krafczyk",
            "",
            "cra",
            "crazy",
            "crazy",
            buildWordSignature("fkk")
        )
    }
}
