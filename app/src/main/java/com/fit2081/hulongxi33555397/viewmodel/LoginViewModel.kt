package com.fit2081.hulongxi33555397.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fit2081.hulongxi33555397.db.NutritrackRepository
import com.fit2081.hulongxi33555397.db.Patient
import com.fit2081.hulongxi33555397.db.FoodIntake
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NutritrackRepository(application)

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isProcessing = MutableLiveData<Boolean>()
    val isProcessing: LiveData<Boolean> = _isProcessing

    private val _loginResult = MutableLiveData<Patient?>()
    val loginResult: LiveData<Patient?> = _loginResult

    private val _foodIntake = MutableLiveData<FoodIntake?>()
    val foodIntake: LiveData<FoodIntake?> = _foodIntake

    private val _isUserRegistered = MutableLiveData<Boolean>()
    val isUserRegistered: LiveData<Boolean> = _isUserRegistered

    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    private val _hasCompletedQuestionnaire = MutableLiveData<Boolean>()
    val hasCompletedQuestionnaire: LiveData<Boolean> = _hasCompletedQuestionnaire

    fun authenticateUser(userId: String, password: String) {
        _isProcessing.value = true
        _errorMessage.value = ""

        viewModelScope.launch {
            try {
                val result = repository.authenticateUser(userId, password)
                _loginResult.value = result

                if (result != null) {
                    // Get the user's food intake record
                    val intake = repository.getLatestFoodIntake(userId)
                    _foodIntake.value = intake

                    // Check questionnaire status
                    val questionnaire = repository.getQuestionnaireByUserId(userId)
                    _hasCompletedQuestionnaire.postValue(questionnaire != null)

                    // Set the login success status
                    _registrationSuccess.postValue(true)
                } else {
                    _errorMessage.value = "Incorrect User ID or Password"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error logging in：${e.message}"
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun checkQuestionnaireStatus(userId: String) {
        viewModelScope.launch {
            try {
                val questionnaire = repository.getQuestionnaireByUserId(userId)
                _hasCompletedQuestionnaire.postValue(questionnaire != null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun checkIfUserExists(userId: String) {
        _isProcessing.value = true
        _errorMessage.value = ""

        viewModelScope.launch {
            try {
                val patient = repository.getPatientById(userId)

                if (patient == null) {
                    _errorMessage.value = "User ID does not exist, please check your input"
                    _loginResult.value = null
                } else {
                    // Determine whether the user has registered (set a password)
                    val isRegistered = !patient.password.isNullOrBlank()
                    _loginResult.value = patient

                    // Add additional status tags to pass user registration status
                    _isUserRegistered.value = isRegistered
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error checking user：${e.message}"
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun setFirstTimeUserInfo(userId: String, name: String, password: String, phoneNumber: String) {
        _isProcessing.value = true
        _errorMessage.value = ""

        android.util.Log.d("LoginViewModel", "Start Registration，userId=$userId, name=$name")

        viewModelScope.launch {
            try {
                val patient = repository.getPatientById(userId)
                android.util.Log.d("LoginViewModel", "Obtain patient information：${patient != null}")

                if (patient != null) {
                    // Check if the phone number matches
                    android.util.Log.d("LoginViewModel", "Database Phone：${patient.phoneNumber}，Enter phone number：$phoneNumber")

                    if (patient.phoneNumber == phoneNumber) {
                        // Update User Information
                        patient.name = name
                        patient.password = password
                        repository.updatePatient(patient)
                        android.util.Log.d("LoginViewModel", "User updated successfully, password set")

                        // Use postValue to ensure updates are made on the main thread
                        _loginResult.postValue(patient)

                        // Check if the questionnaire has been filled out
                        val intake = repository.getLatestFoodIntake(userId)
                        _foodIntake.postValue(intake)
                        android.util.Log.d("LoginViewModel", "Complete the registration process，foodIntake=${intake != null}")
                        _registrationSuccess.postValue(true)
                    } else {
                        android.util.Log.d("LoginViewModel", "Phone number does not match")
                        _errorMessage.postValue("Phone number does not match, please try again")
                        _registrationSuccess.postValue(false)
                    }
                } else {
                    android.util.Log.d("LoginViewModel", "User ID does not exist")
                    _errorMessage.postValue("User ID does not exist")
                }
            } catch (e: Exception) {
                android.util.Log.e("LoginViewModel", "Error in setting user information", e)
                _errorMessage.postValue("Error setting user information：${e.message}")
            } finally {
                _isProcessing.postValue(false)
            }
        }
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }
}