package com.deyvidandrades.meusdias.data.model

import kotlinx.serialization.Serializable
import java.util.Calendar

@Serializable
data class Goal(var title: String, var currentStreak: Int, var currentRecord: Int) : Comparable<Goal> {
    val createdAt: Long = Calendar.getInstance().timeInMillis

    override fun compareTo(other: Goal): Int = createdAt.compareTo(other.createdAt)
}
