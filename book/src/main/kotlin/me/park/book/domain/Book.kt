package me.park.book.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Book(
    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var author: String,

    @Column
    var description: String? = null,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    var id: Long? = null
}
