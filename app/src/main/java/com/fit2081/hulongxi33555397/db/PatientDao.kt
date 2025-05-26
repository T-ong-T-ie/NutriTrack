package com.fit2081.hulongxi33555397.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patients: List<Patient>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(patient: Patient)

    @Update
    suspend fun updatePatient(patient: Patient): Int

    @Query("SELECT * FROM patients WHERE userId = :userId")
    suspend fun getPatientById(userId: String): Patient?

    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getPatientCount(): Int

    @Query("SELECT * FROM patients WHERE userId = :userId AND password = :password")
    suspend fun authenticateUser(userId: String, password: String): Patient?

    @Query("SELECT * FROM patients")
    suspend fun getAllPatients(): List<Patient>
}
