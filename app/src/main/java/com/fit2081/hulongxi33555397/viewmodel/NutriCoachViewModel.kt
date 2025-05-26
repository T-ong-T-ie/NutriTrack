package com.fit2081.hulongxi33555397.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fit2081.hulongxi33555397.db.NutritrackRepository
import com.fit2081.hulongxi33555397.db.Patient
import com.fit2081.hulongxi33555397.models.FruitDetails
import com.fit2081.hulongxi33555397.models.NutriCoachTip as ModelNutriCoachTip
import kotlinx.coroutines.launch

class NutriCoachViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NutritrackRepository(application)

    // Fruit query related status
    private val _fruitNameQuery = MutableLiveData<String>("")
    val fruitNameQuery: LiveData<String> = _fruitNameQuery

    private val _fruitDetails = MutableLiveData<FruitDetails?>(null)
    val fruitDetails: LiveData<FruitDetails?> = _fruitDetails

    private val _isLoadingFruit = MutableLiveData<Boolean>(false)
    val isLoadingFruit: LiveData<Boolean> = _isLoadingFruit

    private val _fruitError = MutableLiveData<String?>(null)
    val fruitError: LiveData<String?> = _fruitError

    // AI related status
    private val _isLoadingGenAI = MutableLiveData<Boolean>(false)
    val isLoadingGenAI: LiveData<Boolean> = _isLoadingGenAI

    private val _genAIResponse = MutableLiveData<String>("")
    val genAIResponse: LiveData<String> = _genAIResponse

    private val _fruitScore = MutableLiveData<Float>(0f)
    val fruitScore: LiveData<Float> = _fruitScore

    private val _isOptimalFruitScore = MutableLiveData<Boolean>(false)
    val isOptimalFruitScore: LiveData<Boolean> = _isOptimalFruitScore

    // User Data
    private var currentUserId: String? = null
    private var patientData: Patient? = null

    // Tips for saving
    private val _savedTips = MutableLiveData<List<ModelNutriCoachTip>>(emptyList())
    val savedTips: LiveData<List<ModelNutriCoachTip>> = _savedTips

    private val _showTipsHistory = MutableLiveData<Boolean>(false)
    val showTipsHistory: LiveData<Boolean> = _showTipsHistory

    fun setFruitNameQuery(query: String) {
        _fruitNameQuery.value = query
    }

    fun setShowTipsHistory(show: Boolean) {
        _showTipsHistory.value = show
    }

    fun loadUserData(userId: String) {
        viewModelScope.launch {
            try {
                currentUserId = userId
                patientData = repository.getPatientById(userId)

                // Set fruit score
                val isMale = patientData?.sex == "Male"
                val fruitScore = if (isMale) {
                    patientData?.FruitHEIFAscoreMale ?: 0f
                } else {
                    patientData?.FruitHEIFAscoreFemale ?: 0f
                }
                _fruitScore.value = fruitScore
                _isOptimalFruitScore.value = fruitScore >= 8.0f
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun searchFruit() {
        val query = _fruitNameQuery.value?.trim() ?: ""
        if (query.isBlank()) {
            _fruitError.value = "Please enter the name of the fruit"
            return
        }

        _isLoadingFruit.value = true
        _fruitError.value = null

        viewModelScope.launch {
            try {
                // Get fruit data through the Repository layer
                val result = repository.searchFruit(query)
                _fruitDetails.value = result
                _fruitError.value = null
            } catch (e: Exception) {
                e.printStackTrace()
                _fruitError.value = "An error occurred while querying fruit details：${e.message}"
                _fruitDetails.value = null
            } finally {
                _isLoadingFruit.value = false
            }
        }
    }

    // Generate AI reply function
    fun generateAIResponse() {
        _isLoadingGenAI.value = true
        _genAIResponse.value = ""

        // Get the necessary data from the view model
        val userId = currentUserId ?: ""
        val fruit = _fruitDetails.value
        val isMale = patientData?.sex == "Male"
        val fruitScore = _fruitScore.value ?: 0f

        viewModelScope.launch {
            try {
                // Generate Response Using Gemini
                val response = repository.generateAIResponse(
                    userId = userId,
                    fruitName = fruit?.name,
                    fruitFamily = fruit?.family,
                    fruitNutrition = fruit?.nutritions,
                    isMale = isMale,
                    fruitScore = fruitScore
                )
                _genAIResponse.value = response

                // Save to database
                if (response.isNotEmpty()) {
                    val tip = ModelNutriCoachTip(
                        userId = userId,
                        content = response,
                        category = if (fruit != null) "Fruit Analysis" else "Health Tips",
                        timestamp = System.currentTimeMillis()
                    )
                    repository.saveTip(tip)
                    // Refresh History
                    loadSavedTips()
                }
            } catch (e: Exception) {
                _genAIResponse.value = "Error generating response: ${e.message}"
            } finally {
                _isLoadingGenAI.value = false
            }
        }
    }

    fun loadSavedTips() {
        viewModelScope.launch {
            currentUserId?.let { userId ->
                try {
                    // Get user hints and map to model objects
                    val dbTipsList = repository.getUserTips(userId)
                    val modelTipsList = dbTipsList.map { dbTip ->
                        ModelNutriCoachTip(
                            userId = dbTip.userId,
                            category = dbTip.category,
                            content = dbTip.content,
                            timestamp = dbTip.timestamp
                        )
                    }
                    _savedTips.value = modelTipsList
                } catch (e: Exception) {
                    e.printStackTrace()
                    _savedTips.value = emptyList()
                }
            } ?: run {
                _savedTips.value = emptyList()
            }
        }
    }
}