package me.park.rental.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDate

@Entity
class RentedItem(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id")
    var rental: Rental,

    @Column(name = "book_id", nullable = false)
    var bookId: Long,

    @Column(name = "rented_date", nullable = false)
    var rentedDate: LocalDate,

    @Column(name = "due_date", nullable = false)
    var dueDate: LocalDate,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null
}
