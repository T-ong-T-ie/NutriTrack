// QuestionnaireViewModel.kt
package com.fit2081.hulongxi33555397.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fit2081.hulongxi33555397.db.NutritrackRepository
import kotlinx.coroutines.launch

class QuestionnaireViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NutritrackRepository(application)

    private val _selectedCategories = MutableLiveData<List<String>>(emptyList())
    val selectedCategories: LiveData<List<String>> = _selectedCategories

    private val _selectedPersona = MutableLiveData<String?>(null)
    val selectedPersona: LiveData<String?> = _selectedPersona

    private val _selectedTimes = MutableLiveData<List<String>>(List(3) { "" })
    val selectedTimes: LiveData<List<String>> = _selectedTimes

    private val _isDataComplete = MutableLiveData<Boolean>(false)
    val isDataComplete: LiveData<Boolean> = _isDataComplete

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hasCompletedQuestionnaire = MutableLiveData<Boolean>(false)
    val hasCompletedQuestionnaire: LiveData<Boolean> = _hasCompletedQuestionnaire

    fun loadQuestionnaireData(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val categories = repository.getUserCategories(userId)
                val persona = repository.getUserPersona(userId)
                val times = repository.getUserTimes(userId)

                _selectedCategories.postValue(categories)
                _selectedPersona.postValue(persona)
                _selectedTimes.postValue(times)

                checkDataCompleteness()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun toggleCategorySelection(category: String) {
        val currentList = _selectedCategories.value?.toMutableList() ?: mutableListOf()
        if (currentList.contains(category)) {
            currentList.remove(category)
        } else {
            currentList.add(category)
        }
        _selectedCategories.value = currentList
        checkDataCompleteness()
    }

    fun selectPersona(persona: String) {
        _selectedPersona.value = persona
        checkDataCompleteness()
    }

    fun setTimeSelection(index: Int, time: String) {
        val currentTimes = _selectedTimes.value?.toMutableList() ?: mutableListOf("", "", "")
        currentTimes[index] = time
        _selectedTimes.value = currentTimes
        checkDataCompleteness()
    }

    fun saveQuestionnaireData(userId: String) {
        viewModelScope.launch {
            val categories = _selectedCategories.value ?: emptyList()
            val persona = _selectedPersona.value
            val times = _selectedTimes.value ?: List(3) { "" }

            if (categories.isNotEmpty() && persona != null && times.all { it.isNotEmpty() }) {
                repository.saveUserQuestionnaire(userId, categories, persona, times)
            }
        }
    }

    fun checkQuestionnaireStatus(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val questionnaire = repository.getQuestionnaireByUserId(userId)
                _hasCompletedQuestionnaire.postValue(questionnaire != null)

                if (questionnaire != null) {
                    _selectedCategories.postValue(questionnaire.categories.split(",").filter { it.isNotEmpty() })
                    _selectedPersona.postValue(questionnaire.persona)
                    _selectedTimes.postValue(listOf(
                        questionnaire.biggestMealTime,
                        questionnaire.sleepTime,
                        questionnaire.wakeTime
                    ))
                    checkDataCompleteness()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun checkDataCompleteness() {
        val categories = _selectedCategories.value ?: emptyList()
        val persona = _selectedPersona.value
        val times = _selectedTimes.value ?: List(3) { "" }

        _isDataComplete.value = categories.isNotEmpty() && persona != null && times.all { it.isNotEmpty() }
    }
}