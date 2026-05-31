package me.park.rental.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDate

@Entity
class RentalItem(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var rental: Rental,

    @Column(nullable = false)
    var bookId: Long,

    @Column(nullable = false)
    var rentedDate: LocalDate,

    @Column(nullable = false)
    var dueDate: LocalDate,

    @Column
    var returnedDate: LocalDate? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: RentalItemStatus,

    @Column(nullable = false)
    var lateFee: Long = 0L,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    var id: Long? = null
}
