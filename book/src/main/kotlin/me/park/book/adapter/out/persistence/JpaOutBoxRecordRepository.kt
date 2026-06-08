package me.park.book.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface JpaOutBoxRecordRepository : JpaRepository<OutBoxRecord, String> {
    fun findByStatusOrderByCreatedAtAsc(status: OutBoxRecordStatus): List<OutBoxRecord>
}
