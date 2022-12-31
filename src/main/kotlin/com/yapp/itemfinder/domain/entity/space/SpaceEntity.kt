package com.yapp.itemfinder.domain.entity.space

import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.domain.entity.BaseEntity
import com.yapp.itemfinder.domain.entity.member.MemberEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(
    name = "space",
    indexes = [
        Index(name = "idx_member_id", columnList = "member_id")
    ]
)
class SpaceEntity(
    member: MemberEntity,
    name: String,
    id: Long = 0L
) : BaseEntity(id) {
    init {
        validateName(name)
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: MemberEntity = member
        protected set

    @Column(length = 30, nullable = false)
    var name: String = name
        protected set

    private fun validateName(name: String) {
        require(name.isNotBlank() && name.length <= 30) {
            throw BadRequestException(message = "1자 이상 30자 이내로 이름을 등록해 주세요.")
        }
    }
}
