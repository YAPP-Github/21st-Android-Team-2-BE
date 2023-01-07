package com.yapp.itemfinder.domain.auth.dto

import com.yapp.itemfinder.domain.member.Gender
import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.member.Social
import com.yapp.itemfinder.domain.member.SocialType
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class SignUpRequest(
    @field:NotBlank(message = "소셜 아이디 값이 공백입니다.")
    var socialId: String,
    @field:NotNull
    var socialType: SocialType,
    @field:Length(min = 1, max = 50, message = "nickname은 1~50 글자이어야 합니다.")
    var nickname: String,
    @field:Email
    var email: String? = null,
    var gender: Gender? = null,
    var birthYear: Int? = null
) {
    fun toEntity(): MemberEntity {
        return MemberEntity(
            social = Social(socialType, socialId),
            email = email,
            name = nickname,
            gender = gender,
            birthYear = birthYear
        )
    }
}
