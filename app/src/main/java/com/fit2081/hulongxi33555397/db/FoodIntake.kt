package com.fit2081.hulongxi33555397.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_intakes",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["userId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId")]  // 索引外键以提高查询效率
)
data class FoodIntake(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: String,  // 外键关联Patient表，确保类型为String
    val categories: String,
    val persona: String,
    val biggestMealTime: String,
    val sleepTime: String,
    val wakeTime: String,
    val timestamp: Long = System.currentTimeMillis()
)
