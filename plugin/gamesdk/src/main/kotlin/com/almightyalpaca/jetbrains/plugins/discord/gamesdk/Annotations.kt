/*
 * Copyright 2017-2020 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almightyalpaca.jetbrains.plugins.discord.gamesdk

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import kotlin.properties.Delegates

@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
@Constraint(validatedBy = [StringLengthValidator::class])
annotation class StringLength(val min: Int = 0, val max: Int = 1 shl 20, val allowNull: Boolean = false)

class StringLengthValidator : ConstraintValidator<StringLength, String> {
    var min by Delegates.notNull<Int>()
    var max by Delegates.notNull<Int>()
    var allowNull by Delegates.notNull<Boolean>()

    override fun initialize(constraintAnnotation: StringLength) {
        super.initialize(constraintAnnotation)
        min = constraintAnnotation.min
        max = constraintAnnotation.max
        allowNull = constraintAnnotation.allowNull
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return allowNull
        return (value.length >= min) && (value.length < max)
    }
}
