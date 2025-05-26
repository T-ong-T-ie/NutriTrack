package com.fit2081.hulongxi33555397.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fit2081.hulongxi33555397.models.Questionnaire

@Dao
interface QuestionnaireDao {
    @Query("SELECT * FROM questionnaires WHERE userId = :userId")
    suspend fun getQuestionnaireByUserId(userId: String): Questionnaire?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(questionnaire: Questionnaire)
}