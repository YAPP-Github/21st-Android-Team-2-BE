package com.yapp.itemfinder.sample.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
) {
    @CreatedDate
    @Column(updatable = false)
    var createdAt: Timestamp = Timestamp.from(Instant.now())

    @LastModifiedDate
    @Column(updatable = true)
    var lastModifiedAt: Timestamp = Timestamp.from(Instant.now())
}
