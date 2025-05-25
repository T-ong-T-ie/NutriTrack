package com.fit2081.hulongxi33555397.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FoodIntakeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(foodIntake: FoodIntake): Long

    @Query("SELECT * FROM food_intakes WHERE patientId = :patientId ORDER BY timestamp DESC")
    suspend fun getByPatientId(patientId: String): List<FoodIntake>

    @Query("SELECT * FROM food_intakes WHERE patientId = :patientId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestByPatientId(patientId: String): FoodIntake?
}
