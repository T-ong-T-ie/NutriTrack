// AdminViewModel.kt
package com.fit2081.hulongxi33555397.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fit2081.hulongxi33555397.BuildConfig
import com.fit2081.hulongxi33555397.db.NutritrackRepository
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NutritrackRepository(application)

    private val _maleAvgScore = MutableLiveData<Float>(0f)
    val maleAvgScore: LiveData<Float> = _maleAvgScore

    private val _femaleAvgScore = MutableLiveData<Float>(0f)
    val femaleAvgScore: LiveData<Float> = _femaleAvgScore

    private val _patientStats = MutableLiveData<Map<String, Any>>(emptyMap())
    val patientStats: LiveData<Map<String, Any>> = _patientStats

    private val _patterns = MutableLiveData<List<String>>(emptyList())
    val patterns: LiveData<List<String>> = _patterns

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isAnalyzing = MutableLiveData<Boolean>(false)
    val isAnalyzing: LiveData<Boolean> = _isAnalyzing

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-latest",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    fun loadData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Get all patient data
                val patients = repository.getAllPatients()
                val stats = mutableMapOf<String, Any>()

                // Calculate the average score for men
                val malePatients = patients.filter { it.sex.equals("Male", ignoreCase = true) }
                val maleAvg = if (malePatients.isNotEmpty()) {
                    malePatients.sumOf { it.heifaTotalScoreMale.toDouble() }.toFloat() / malePatients.size
                } else 0f
                _maleAvgScore.postValue(maleAvg)
                stats["Number of male users"] = malePatients.size

                // Calculate the average score for women
                val femalePatients = patients.filter { it.sex.equals("Female", ignoreCase = true) }
                val femaleAvg = if (femalePatients.isNotEmpty()) {
                    femalePatients.sumOf { it.heifaTotalScoreFemale.toDouble() }.toFloat() / femalePatients.size
                } else 0f
                _femaleAvgScore.postValue(femaleAvg)
                stats["Number of female users"] = femalePatients.size

                // Calculate other useful statistics
                if (malePatients.isNotEmpty()) {
                    stats["Average vegetable score for Men:"] = malePatients.sumOf { it.VegetablesHEIFAscoreMale.toDouble() }.toFloat() / malePatients.size
                    stats["Average fruit score for Men:"] = malePatients.sumOf { it.FruitHEIFAscoreMale.toDouble() }.toFloat() / malePatients.size
                    stats["Average Whole Grain Score for Men:"] = malePatients.sumOf { it.WholegrainsHEIFAscoreMale.toDouble() }.toFloat() / malePatients.size
                    stats["Average Sodium Score for Men:"] = malePatients.sumOf { it.SodiumHEIFAscoreMale.toDouble() }.toFloat() / malePatients.size
                    stats["Average Sugar Score for Men:"] = malePatients.sumOf { it.SugarHEIFAscoreMale.toDouble() }.toFloat() / malePatients.size
                }

                if (femalePatients.isNotEmpty()) {
                    stats["Average vegetable score for Women:"] = femalePatients.sumOf { it.VegetablesHEIFAscoreFemale.toDouble() }.toFloat() / femalePatients.size
                    stats["Average fruit score for Women:"] = femalePatients.sumOf { it.FruitHEIFAscoreFemale.toDouble() }.toFloat() / femalePatients.size
                    stats["Average Whole Grain Score for Women:"] = femalePatients.sumOf { it.WholegrainsHEIFAscoreFemale.toDouble() }.toFloat() / femalePatients.size
                    stats["Average Sodium Score for Women:"] = femalePatients.sumOf { it.SodiumHEIFAscoreFemale.toDouble() }.toFloat() / femalePatients.size
                    stats["Average Sugar Score for Women:"] = femalePatients.sumOf { it.SugarHEIFAscoreFemale.toDouble() }.toFloat() / femalePatients.size
                }

                _patientStats.postValue(stats)

                // Initialize analysis data
                analyzeDataWithGenAI()
            } catch (e: Exception) {
                e.printStackTrace()
                _patterns.postValue(listOf(
                    "An error occurred while loading data",
                    "Please check your database connection",
                    "If the problem persists, please contact technical support"
                ))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun analyzeDataWithGenAI() {
        if (_patientStats.value?.isEmpty() == true) return

        _isAnalyzing.value = true
        _patterns.value = emptyList()

        viewModelScope.launch {
            try {
                val prompt = buildString {
                    append("As a nutrition data analyst, please find 3 interesting patterns or insights based on the following data:\n")
                    append("- Average HEIFA score for male users:${String.format("%.2f", _maleAvgScore.value)}\n")
                    append("- Average HEIFA score for female users:${String.format("%.2f", _femaleAvgScore.value)}\n")

                    // Add more detailed statistics
                    _patientStats.value?.forEach { (key, value) ->
                        append("- $key: $value\n")
                    }

                    append("\nPlease provide 3 interesting findings based on data. Each finding should be a complete and concise sentence. Do not use Markdown format.")
                    append("Do not include serial numbers or prefixes. I will automatically add serial numbers on the interface.")
                    append("All replies must be in English.")
                }

                val response = generativeModel.generateContent(prompt)
                val analysisText = response.text ?: "Unable to generate analysis results"

                // Process the response text, breaking it into individual findings
                val newPatterns = analysisText
                    .split("\n")
                    .filter { it.isNotBlank() }
                    .map { it.trim().removePrefix("-").trim() }
                    .take(3)

                _patterns.postValue(if (newPatterns.isEmpty()) {
                    listOf("No obvious patterns can be discerned from the data")
                } else {
                    newPatterns
                })
            } catch (e: Exception) {
                _patterns.postValue(listOf(
                    "An error occurred while generating data analysis",
                    "Please check your network connection or try again",
                    "If the problem persists, please contact technical support"
                ))
            } finally {
                _isAnalyzing.postValue(false)
            }
        }
    }
}