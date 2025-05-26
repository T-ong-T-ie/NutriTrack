package com.fit2081.hulongxi33555397.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fit2081.hulongxi33555397.db.NutritrackRepository
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NutritrackRepository(application)

    private val _name = MutableLiveData<String>("Loading...")
    val name: LiveData<String> = _name

    private val _phoneNumber = MutableLiveData<String>("Loading...")
    val phoneNumber: LiveData<String> = _phoneNumber

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadUserData(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val patient = repository.getPatientById(userId)
                if (patient != null) {
                    _name.postValue(patient.name ?: "Unknown")
                    _phoneNumber.postValue(patient.phoneNumber ?: "Unknown")
                } else {
                    _name.postValue("User not found")
                    _phoneNumber.postValue("User not found")
                }
            } catch (e: Exception) {
                _name.postValue("Loading Error")
                _phoneNumber.postValue("Loading Error")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}