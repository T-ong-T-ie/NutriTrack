package com.fit2081.hulongxi33555397.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fit2081.hulongxi33555397.db.NutritrackRepository
import kotlinx.coroutines.launch

class UserSessionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NutritrackRepository(application)
    private val _userId = MutableLiveData<String>("Unknown")
    val userId: LiveData<String> = _userId

    private val _isLoggedIn = MutableLiveData<Boolean>(false)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    init {
        loadUserSession()
    }

    private fun loadUserSession() {
        val prefs = getApplication<Application>().getSharedPreferences("NutriTrackPrefs", Application.MODE_PRIVATE)
        _isLoggedIn.value = prefs.getBoolean("is_logged_in", false)
        _userId.value = prefs.getString("user_id", "Unknown") ?: "Unknown"
    }

    fun saveUserSession(userId: String, isLoggedIn: Boolean) {
        viewModelScope.launch {
            val prefs = getApplication<Application>().getSharedPreferences("NutriTrackPrefs", Application.MODE_PRIVATE)
            prefs.edit()
                .putString("user_id", userId)
                .putBoolean("is_logged_in", isLoggedIn)
                .apply()

            _userId.postValue(userId)
            _isLoggedIn.postValue(isLoggedIn)
        }
    }

    fun logout() {
        saveUserSession("Unknown", false)
    }
}