package com.yapp.itemfinder.domain.tag

import com.yapp.itemfinder.domain.BaseEntity
import com.yapp.itemfinder.domain.member.MemberEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Table(
    name = "tag",
    indexes = [
        Index(name = "idx_member_id", columnList = "member_id")
    ]
)
@Entity
class TagEntity(
    id: Long = 0L,
    member: MemberEntity,
    name: String
) : BaseEntity(id) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: MemberEntity = member
        protected set

    @Column(length = 255, nullable = false)
    var name: String = name
        protected set
}
