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
    indices = [Index("patientId")]  // Index foreign keys
)
data class FoodIntake(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: String,  // The foreign key is associated with the Patient table and the type is String
    val categories: String,
    val persona: String,
    val biggestMealTime: String,
    val sleepTime: String,
    val wakeTime: String,
    val timestamp: Long = System.currentTimeMillis()
)
