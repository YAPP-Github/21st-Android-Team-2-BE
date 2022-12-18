package com.yapp.itemfinder.domain.entity.tag

import com.yapp.itemfinder.domain.entity.BaseEntity
import com.yapp.itemfinder.domain.entity.MemberEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Table(name = "tag")
@Entity
class TagEntity(
    id: Long = 0L,
    member: MemberEntity,
    name: String
): BaseEntity(id) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: MemberEntity = member
        protected set

    @Column(length = 255, nullable = false)
    var name: String = name
        protected set
}
