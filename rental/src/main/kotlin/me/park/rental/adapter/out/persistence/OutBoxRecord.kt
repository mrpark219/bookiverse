package me.park.rental.adapter.out.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "outbox_records")
class OutBoxRecord(
    @Id
    @Column(nullable = false, length = 100)
    var id: String,

    @Column(nullable = false, length = 100)
    var eventType: String,

    @Column(nullable = false, length = 100)
    var topic: String,

    @Column(length = 100)
    var messageKey: String? = null,

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    var payload: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: OutBoxRecordStatus = OutBoxRecordStatus.PENDING,

    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column
    var publishedAt: LocalDateTime? = null,
) {

    fun markPublished(publishedAt: LocalDateTime = LocalDateTime.now()) {
        status = OutBoxRecordStatus.PUBLISHED
        this.publishedAt = publishedAt
    }
}
