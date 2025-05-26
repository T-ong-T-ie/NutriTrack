package com.fit2081.hulongxi33555397.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questionnaires")
data class Questionnaire(
    @PrimaryKey val userId: String,
    val categories: String, // 用逗号分隔的类别
    val persona: String?,
    val biggestMealTime: String,
    val sleepTime: String,
    val wakeTime: String
)