package dev.krafczyk.anagram

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AnagramApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<AnagramApplication>(*args)
}
