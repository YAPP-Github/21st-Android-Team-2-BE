package com.yapp.itemfinder.domain.entity.space

import com.yapp.itemfinder.domain.entity.BaseEntity
import com.yapp.itemfinder.domain.entity.member.MemberEntity
import javax.persistence.*

@Entity
@Table(name = "space",
    indexes = [
        Index(name = "idx_member_id", columnList = "member_id")
    ]
)
class SpaceEntity(
    member: MemberEntity,
    name: String,
    id: Long = 0L
) : BaseEntity(id) {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: MemberEntity = member
        protected set

    @Column(length = 30, nullable = false)
    var name: String = name
        protected set
}
