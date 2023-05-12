package dev.krafczyk.anagram

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class WordSignatureTest {

    @Test
    fun `should convert word to sorted character sequence`() {
        buildWordSignature("") shouldBeEqualTo ""
        buildWordSignature("carl") shouldBeEqualTo "aclr"
        buildWordSignature("ö") shouldBeEqualTo "ö"
        buildWordSignature("cfakqlvdrzsnjhomtwpbeuxigy") shouldBeEqualTo "abcdefghijklmnopqrstuvwxyz"
    }

    @Test
    fun `should discard letter case`() {
        buildWordSignature("AsdF") shouldBeEqualTo buildWordSignature("aSDf")
        buildWordSignature("äüö") shouldBeEqualTo buildWordSignature("ÜÖÄ")
        buildWordSignature("ø") shouldBeEqualTo buildWordSignature("Ø")
    }

    @Test
    fun `should discard non-letter characters`() {
        buildWordSignature("c,a-r:l ") shouldBeEqualTo "aclr"
    }

    @Test
    fun `should retain multiple instance of a letter`() {
        buildWordSignature("aaaa").toString() shouldBeEqualTo "aaaa"
        buildWordSignature("bob").toString() shouldBeEqualTo "bbo"
    }

    @Test
    fun `should correctly implement set subtraction`() {
        subtractWordSignature(buildWordSignature("krafczyk"), buildWordSignature("crazy")) shouldBeEqualTo
            buildWordSignature("fkk")
    }

    @Test
    fun `should correctly implement superset checks`() {
        isWordSuperset(buildWordSignature("asdf"), buildWordSignature("asdf")) shouldBeEqualTo true
        isWordSuperset(buildWordSignature("krafczyk"), buildWordSignature("crazy")) shouldBeEqualTo true
        isWordSuperset(buildWordSignature("krafczyk"), buildWordSignature("crazier")) shouldBeEqualTo false
        isWordSuperset(buildWordSignature("crazy"), buildWordSignature("krafczyk")) shouldBeEqualTo false
    }

    @Test
    fun `should correctly implement subset checks`() {
        isWordSubset(buildWordSignature("asdf"), buildWordSignature("asdf")) shouldBeEqualTo true
        isWordSubset(buildWordSignature("crazy"), buildWordSignature("krafczyk")) shouldBeEqualTo true
        isWordSubset(buildWordSignature("crazier"), buildWordSignature("krafczyk")) shouldBeEqualTo false
        isWordSubset(buildWordSignature("krafczyk"), buildWordSignature("crazy")) shouldBeEqualTo false
    }
}
