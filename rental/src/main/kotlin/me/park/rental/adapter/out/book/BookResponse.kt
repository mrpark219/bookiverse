package me.park.rental.adapter.out.book

data class BookResponse(
    val id: Long?,
    val title: String,
    val author: String? = null,
    val description: String? = null,
)
