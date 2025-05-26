package com.fit2081.hulongxi33555397.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.hulongxi33555397.CategoryScore
import com.fit2081.hulongxi33555397.db.NutritrackRepository
import com.fit2081.hulongxi33555397.db.Patient
import kotlinx.coroutines.launch

// UI state data class, which contains all the data required by the interface
data class InsightsUiState(
    val isLoading: Boolean = true,
    val categoryScores: List<CategoryScore> = emptyList(),
    val totalScore: Float = 0f,
    val maxTotalScore: Float = 100f,
    val error: String? = null,
    val isMale: Boolean = true
)

class InsightsViewModel(private val repository: NutritrackRepository) : ViewModel() {

    // Internal mutable LiveData
    private val _uiState = MutableLiveData(InsightsUiState())

    // Immutable LiveData exposed to the outside world
    val uiState: LiveData<InsightsUiState> = _uiState

    // Loading User Data
    fun loadUserData(userId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value?.copy(isLoading = true)
                val patientData = repository.getPatientById(userId)

                if (patientData != null) {
                    val isMale = patientData.sex == "Male"
                    val categoryScores = createCategoryScores(patientData, isMale)
                    val totalScore = if (isMale) patientData.heifaTotalScoreMale else patientData.heifaTotalScoreFemale

                    _uiState.postValue(_uiState.value?.copy(
                        isLoading = false,
                        categoryScores = categoryScores,
                        totalScore = totalScore,
                        isMale = isMale,
                        error = null
                    ))
                } else {
                    _uiState.postValue(_uiState.value?.copy(
                        isLoading = false,
                        error = "User data not found"
                    ))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.postValue(_uiState.value?.copy(
                    isLoading = false,
                    error = "Error loading data: ${e.message}"
                ))
            }
        }
    }

    // Create a categorized rating list
    private fun createCategoryScores(data: Patient, isMale: Boolean): List<CategoryScore> {
        // Keep the original code unchanged
        val scores = mutableListOf<CategoryScore>()

        // Add all food category ratings
        scores.add(CategoryScore(
            name = "Discretionary",
            score = if (isMale) data.DiscretionaryHEIFAscoreMale else data.DiscretionaryHEIFAscoreFemale,
            maxScore = 10f
        ))

        scores.add(CategoryScore(
            name = "Vegetables",
            score = if (isMale) data.VegetablesHEIFAscoreMale else data.VegetablesHEIFAscoreFemale,
            maxScore = 10f
        ))

        scores.add(CategoryScore(
            name = "Fruits",
            score = if (isMale) data.FruitHEIFAscoreMale else data.FruitHEIFAscoreFemale,
            maxScore = 10f
        ))

        scores.add(CategoryScore(
            name = "Grains & Cereals",
            score = if (isMale) data.GrainsandcerealsHEIFAscoreMale else data.GrainsandcerealsHEIFAscoreFemale,
            maxScore = 5f
        ))

        scores.add(CategoryScore(
            name = "Whole Grains",
            score = if (isMale) data.WholegrainsHEIFAscoreMale else data.WholegrainsHEIFAscoreFemale,
            maxScore = 5f
        ))

        scores.add(CategoryScore(
            name = "Meat & Alternatives",
            score = if (isMale) data.MeatandalternativesHEIFAscoreMale else data.MeatandalternativesHEIFAscoreFemale,
            maxScore = 10f
        ))

        scores.add(CategoryScore(
            name = "Dairy & Alternatives",
            score = if (isMale) data.DairyandalternativesHEIFAscoreMale else data.DairyandalternativesHEIFAscoreFemale,
            maxScore = 10f
        ))

        scores.add(CategoryScore(
            name = "Sodium",
            score = if (isMale) data.SodiumHEIFAscoreMale else data.SodiumHEIFAscoreFemale,
            maxScore = 10f
        ))

        scores.add(CategoryScore(
            name = "Alcohol",
            score = if (isMale) data.AlcoholHEIFAscoreMale else data.AlcoholHEIFAscoreFemale,
            maxScore = 5f
        ))

        scores.add(CategoryScore(
            name = "Water",
            score = if (isMale) data.WaterHEIFAscoreMale else data.WaterHEIFAscoreFemale,
            maxScore = 5f
        ))

        scores.add(CategoryScore(
            name = "Added Sugar",
            score = if (isMale) data.SugarHEIFAscoreMale else data.SugarHEIFAscoreFemale,
            maxScore = 10f
        ))

        scores.add(CategoryScore(
            name = "Saturated Fat",
            score = if (isMale) data.SaturatedFatHEIFAscoreMale else data.SaturatedFatHEIFAscoreFemale,
            maxScore = 5f
        ))

        scores.add(CategoryScore(
            name = "Unsaturated Fat",
            score = if (isMale) data.UnsaturatedFatHEIFAscoreMale else data.UnsaturatedFatHEIFAscoreFemale,
            maxScore = 5f
        ))

        return scores
    }

    // Factory class for creating ViewModel
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InsightsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return InsightsViewModel(NutritrackRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}