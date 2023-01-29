package com.yapp.itemfinder.domain.space

import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.domain.BaseEntity
import com.yapp.itemfinder.domain.member.MemberEntity
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

    @Column(length = SPACE_NAME_LENGTH_LIMIT, nullable = false)
    var name: String = name
        protected set

    fun getCreatorId(): Long {
        return member.id
    }

    private fun validateName(name: String) {
        require(name.isNotBlank() && name.length <= SPACE_NAME_LENGTH_LIMIT) {
            throw BadRequestException(message = "1자 이상 ${SPACE_NAME_LENGTH_LIMIT}자 이내로 이름을 등록해 주세요.")
        }
    }

    companion object {
        const val SPACE_NAME_LENGTH_LIMIT = 9
    }
}
