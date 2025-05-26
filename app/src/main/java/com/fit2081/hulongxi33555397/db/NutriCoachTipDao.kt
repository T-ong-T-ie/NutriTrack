package com.fit2081.hulongxi33555397.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

// Entity Class
@Entity(tableName = "nutricoach_tips")
data class NutriCoachTip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String,
    val imageUrl: String? = null
)

// DAO Interface
@Dao
interface NutriCoachTipDao {
    @Insert
    suspend fun insertTip(tip: NutriCoachTip): Long

    @Query("SELECT * FROM nutricoach_tips WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getTipsForUser(userId: String): List<NutriCoachTip>
}