package me.park.bookcatalog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookCatalogApplication

fun main(args: Array<String>) {
    runApplication<BookCatalogApplication>(*args)
}
