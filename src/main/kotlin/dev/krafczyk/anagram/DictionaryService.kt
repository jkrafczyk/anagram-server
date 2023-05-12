package dev.krafczyk.anagram

import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class DictionaryService : Iterable<Dictionary> {
    private val dictionaries: MutableMap<String, Dictionary> = mutableMapOf()
    companion object {
        private val WORD_REGEX = Regex("""\p{L}+""")
        private val logger = KotlinLogging.logger { }
    }

    fun loadDictionary(name: String, source: InputStream): Dictionary {
        logger.info {
            (if (name in dictionaries) "reloading" else "loading") +
                " Dictionary $name..."
        }
        val words = mutableSetOf<String>()

        source
            .reader()
            .readLines()
            .map { it.trim() }
            .filter { !it.startsWith('#') }
            .forEach { line ->
                words.addAll(WORD_REGEX.findAll(line).map { it.value })
            }

        val dict = InMemoryDictionary(name.lowercase(), words)
        dictionaries[name.lowercase()] = dict
        logger.info { "Dictionary $name loaded and ready." }
        return dict
    }

    operator fun get(name: String): Dictionary {
        return dictionaries[name]!!
    }

    fun listDictionaryNames(): Set<String> {
        return dictionaries.keys
    }

    override fun iterator(): Iterator<Dictionary> {
        return dictionaries.values.iterator()
    }
}
