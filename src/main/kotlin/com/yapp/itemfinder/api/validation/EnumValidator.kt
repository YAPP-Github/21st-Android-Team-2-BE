package com.yapp.itemfinder.api.validation

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class EnumValidator : ConstraintValidator<EnumType, String> {
    private lateinit var annotation: EnumType

    override fun initialize(constraintAnnotation: EnumType) {
        this.annotation = constraintAnnotation
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return this.annotation.enumClass.java.enumConstants.firstOrNull { it.name == value } != null
    }
}
