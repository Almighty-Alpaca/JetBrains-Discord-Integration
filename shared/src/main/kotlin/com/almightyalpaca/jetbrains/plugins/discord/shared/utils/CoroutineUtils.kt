package com.almightyalpaca.jetbrains.plugins.discord.shared.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

fun <T> Deferred<T>.getCompletedOrNull() = when (isCompleted) {
    true -> getCompleted()
    false -> null
}

fun <T> CoroutineScope.retryAsync(policy: RetryPolicy = RetryPolicy.Exponential(), initial: Boolean = false, block: suspend () -> T) = async {
    retry(policy, initial, block)
}

suspend fun <T> retry(policy: RetryPolicy = RetryPolicy.Exponential(), initial: Boolean = false, block: suspend () -> T): T {
    try {
        if (!initial) {
            return block()
        }
    } catch (ignored: Exception) {
    }

    policy.wait()
    return retry(policy.next, false, block)
}

sealed class RetryPolicy(value: Long, val maxValue: Long) {
    val value = value.coerceAtMost(maxValue)

    abstract val next: RetryPolicy

    suspend fun wait(): RetryPolicy {
        delay(value * 1000)

        return next
    }

    class Constant(value: Long = 10) : RetryPolicy(value, value) {
        override val next: Constant
            get() = this
    }

    class Linear(value: Long = 5, maxValue: Long = 60, private val factor: Int = 2) : RetryPolicy(value, maxValue) {
        override val next: Linear
            get() = Linear(value + factor, maxValue, factor)
    }

    class Exponential(value: Long = 2, maxValue: Long = 60, private val factor: Int = 2) : RetryPolicy(value, maxValue) {
        override val next: Exponential
            get() = Exponential(value * factor, maxValue, factor)

    }
}
