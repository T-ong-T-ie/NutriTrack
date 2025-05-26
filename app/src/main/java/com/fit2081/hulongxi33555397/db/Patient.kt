package com.fit2081.hulongxi33555397.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey val userId: String,
    val phoneNumber: String,
    var name: String?,
    var password: String?,
    val sex: String,
    var fruitScore: Float = 0f,
    val heifaTotalScoreMale: Float,
    val heifaTotalScoreFemale: Float,
    val DiscretionaryHEIFAscoreMale: Float,
    val DiscretionaryHEIFAscoreFemale: Float,
    val VegetablesHEIFAscoreMale: Float,
    val VegetablesHEIFAscoreFemale: Float,
    val FruitHEIFAscoreMale: Float,
    val FruitHEIFAscoreFemale: Float,
    val GrainsandcerealsHEIFAscoreMale: Float,
    val GrainsandcerealsHEIFAscoreFemale: Float,
    val WholegrainsHEIFAscoreMale: Float,
    val WholegrainsHEIFAscoreFemale: Float,
    val MeatandalternativesHEIFAscoreMale: Float,
    val MeatandalternativesHEIFAscoreFemale: Float,
    val DairyandalternativesHEIFAscoreMale: Float,
    val DairyandalternativesHEIFAscoreFemale: Float,
    val SodiumHEIFAscoreMale: Float,
    val SodiumHEIFAscoreFemale: Float,
    val AlcoholHEIFAscoreMale: Float,
    val AlcoholHEIFAscoreFemale: Float,
    val WaterHEIFAscoreMale: Float,
    val WaterHEIFAscoreFemale: Float,
    val SugarHEIFAscoreMale: Float,
    val SugarHEIFAscoreFemale: Float,
    val SaturatedFatHEIFAscoreMale: Float,
    val SaturatedFatHEIFAscoreFemale: Float,
    val UnsaturatedFatHEIFAscoreMale: Float,
    val UnsaturatedFatHEIFAscoreFemale: Float
)
