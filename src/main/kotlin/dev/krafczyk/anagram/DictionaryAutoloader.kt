package dev.krafczyk.anagram

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.tukaani.xz.XZInputStream
import java.io.InputStream
import java.io.SequenceInputStream
import java.util.concurrent.Callable
import java.util.concurrent.Executors

@Component
@Profile("!test")
class DictionaryAutoloader(private val dictionaryService: DictionaryService) {

    private fun loadDictionary(name: String, vararg filenames: String) {
        require(filenames.isNotEmpty())

        val inputStreams: List<InputStream> = filenames.map {
            fn ->
            XZInputStream(this::class.java.getResourceAsStream(fn))
        }

        val source = if (inputStreams.size > 1) inputStreams.reduce { left, right ->
            SequenceInputStream(left, right)
        } else inputStreams[0]

        dictionaryService.loadDictionary(name, source)
    }

    private fun loadDictionaryTask(name: String, vararg filenames: String): Callable<Unit> {
        return Callable {
            loadDictionary(name, *filenames)
        }
    }

    init {
        Executors.newCachedThreadPool().invokeAll(
            listOf(
                loadDictionaryTask("german", "/wordlist-german.txt.xz"),
                loadDictionaryTask("english", "/wordlist-english.txt.xz"),
                loadDictionaryTask("combined", "/wordlist-german.txt.xz", "/wordlist-english.txt.xz"),
            )
        ).forEach { it.get() }
    }
}
