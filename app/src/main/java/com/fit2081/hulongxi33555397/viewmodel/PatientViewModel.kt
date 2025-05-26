package com.fit2081.hulongxi33555397.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fit2081.hulongxi33555397.db.NutritrackRepository
import com.fit2081.hulongxi33555397.db.Patient
import kotlinx.coroutines.launch

class PatientViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NutritrackRepository(application)

    private val _patient = MutableLiveData<Patient?>()
    val patient: LiveData<Patient?> = _patient

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun getPatientById(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getPatientById(userId)
                _patient.postValue(result)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun createPatientFromCsv(userId: String, phoneNumber: String) {
        viewModelScope.launch {
            repository.createPatientFromCsv(userId, phoneNumber)
        }
    }

    fun getAllPatients(): LiveData<List<Patient>> {
        val patients = MutableLiveData<List<Patient>>()
        viewModelScope.launch {
            patients.postValue(repository.getAllPatients())
        }
        return patients
    }
}