package dev.krafczyk.anagram

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Controller
class AnagramController(
    private val dictionaryService: DictionaryService,
    private val objectMapper: ObjectMapper
) {
    private val executorService: ExecutorService = Executors.newCachedThreadPool()

    @GetMapping("/api/dictionaries")
    @ResponseBody
    fun dictionaries(): Set<String> {
        return dictionaryService.listDictionaryNames()
    }

    @GetMapping("/api/anagrams")
    fun anagrams(
        @RequestParam("dict") dictionary: String,
        @RequestParam("q") input: String,
        @RequestParam("prefix", required = false) prefix: String?,
    ): ResponseBodyEmitter {
        val ang = AnagramFinder(dictionaryService[dictionary])

        val emitter = ResponseBodyEmitter()
        executorService.execute {
            try {
                ang.buildAnagrams(input, prefix ?: "").forEach {
                    emitter.send(
                        it + "\n"
                    )
                }
            } catch (ex: IOException) {
                emitter.completeWithError(ex)
            } catch (ex: Exception) {
                emitter.send("ERROR: ${ex.message}")
            } finally {
                emitter.complete()
            }
        }
        return emitter
    }

    @GetMapping("/api/complete")
    fun complete(
        @RequestParam("dict") dictionary: String,
        @RequestParam("q") input: String,
        @RequestParam("prefix", required = false) prefix: String?,
        @RequestParam("partialWord", required = false) partialWord: String?
    ): ResponseBodyEmitter {
        val ang = AnagramFinder(dictionaryService[dictionary])

        val emitter = ResponseBodyEmitter()
        executorService.execute {
            try {
                ang.suggestCompletions(input, prefix ?: "", partialWord ?: "").forEach {
                    emitter.send(
                        objectMapper.writeValueAsString(it) + "\n"
                    )
                }
            } catch (ex: IOException) {
                emitter.completeWithError(ex)
            } catch (ex: Exception) {
                emitter.send("ERROR: ${ex.message}")
            } finally {
                emitter.complete()
            }
        }
        return emitter
    }

    @GetMapping("/api/complete_safe")
    fun completeSafe(
        @RequestParam("dict") dictionary: String,
        @RequestParam("q") input: String,
        @RequestParam("prefix", required = false) prefix: String?,
    ): ResponseBodyEmitter {
        val ang = AnagramFinder(dictionaryService[dictionary])
        val seen = mutableSetOf<String>()
        val emitter = ResponseBodyEmitter()
        val unprefixStartOffset = (prefix ?: "").length

        executorService.execute {
            try {
                ang.buildAnagrams(input, prefix ?: "").forEach {
                    val unprefixed = it.substring(unprefixStartOffset).trim()
                    val newWord = unprefixed.split(' ').getOrNull(0)
                    if (newWord != null && newWord !in seen) {
                        emitter.send(
                            newWord + "\n"
                        )
                        seen.add(newWord)
                    }
                }
            } catch (ex: IOException) {
                emitter.completeWithError(ex)
            } catch (ex: Exception) {
                emitter.send("ERROR: ${ex.message}")
            } finally {
                emitter.complete()
            }
        }
        return emitter
    }
}
