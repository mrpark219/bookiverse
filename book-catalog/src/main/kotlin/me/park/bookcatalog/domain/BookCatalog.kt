package me.park.bookcatalog.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "book_catalog")
open class BookCatalog(
    @Indexed(unique = true)
    val bookId: Long,

    var title: String,

    var author: String,

    var description: String? = null,

    var rentCount: Long = 0,
) {

    @Id
    var id: String? = null
        protected set
}
