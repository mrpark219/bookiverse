package me.park.rental.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Rental(
    @Column(nullable = false)
    var userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var rentalStatus: RentalStatus,

    @Column
    var lateFee: Long = 0L,

    @OneToMany(mappedBy = "rental", cascade = [CascadeType.ALL], orphanRemoval = true)
    var rentalItems: MutableList<RentalItem> = mutableListOf(),
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    var id: Long? = null
}
