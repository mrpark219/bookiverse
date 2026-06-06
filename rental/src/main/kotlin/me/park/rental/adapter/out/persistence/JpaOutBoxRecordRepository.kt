package me.park.rental.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface JpaOutBoxRecordRepository : JpaRepository<OutBoxRecord, String>
