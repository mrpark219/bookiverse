package me.park.rental.adapter.out.book

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "book")
interface BookFeignClient {

    @GetMapping("/books/{bookId}")
    fun getBook(@PathVariable bookId: Long): BookResponse
}
