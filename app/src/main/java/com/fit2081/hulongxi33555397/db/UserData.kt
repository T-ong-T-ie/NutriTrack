package com.fit2081.hulongxi33555397.db

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

// Define the structure for user data, including basic user information and HEIFA scores for each category (separated by gender)
data class UserData(
    val userId: String, // User ID
    val phoneNumber: String, // User phone number
    val sex: String, // User gender
    val heifaTotalScoreMale: Float, // Total HEIFA score for males
    val heifaTotalScoreFemale: Float, // Total HEIFA score for females

    // Scores for each category, separated by gender
    val DiscretionaryHEIFAscoreMale: Float, // Discretionary food score for males
    val DiscretionaryHEIFAscoreFemale: Float, // Discretionary food score for females
    val VegetablesHEIFAscoreMale: Float, // Vegetables score for males
    val VegetablesHEIFAscoreFemale: Float, // Vegetables score for females
    val FruitHEIFAscoreMale: Float, // Fruit score for males
    val FruitHEIFAscoreFemale: Float, // Fruit score for females
    val GrainsandcerealsHEIFAscoreMale: Float, // Grains and cereals score for males
    val GrainsandcerealsHEIFAscoreFemale: Float, // Grains and cereals score for females
    val WholegrainsHEIFAscoreMale: Float, // Whole grains score for males
    val WholegrainsHEIFAscoreFemale: Float, // Whole grains score for females
    val MeatandalternativesHEIFAscoreMale: Float, // Meat and alternatives score for males
    val MeatandalternativesHEIFAscoreFemale: Float, // Meat and alternatives score for females
    val DairyandalternativesHEIFAscoreMale: Float, // Dairy and alternatives score for males
    val DairyandalternativesHEIFAscoreFemale: Float, // Dairy and alternatives score for females
    val SodiumHEIFAscoreMale: Float, // Sodium score for males
    val SodiumHEIFAscoreFemale: Float, // Sodium score for females
    val AlcoholHEIFAscoreMale: Float, // Alcohol score for males
    val AlcoholHEIFAscoreFemale: Float, // Alcohol score for females
    val WaterHEIFAscoreMale: Float, // Water score for males
    val WaterHEIFAscoreFemale: Float, // Water score for females
    val SugarHEIFAscoreMale: Float, // Added sugar score for males
    val SugarHEIFAscoreFemale: Float, // Added sugar score for females
    val SaturatedFatHEIFAscoreMale: Float, // Saturated fat score for males
    val SaturatedFatHEIFAscoreFemale: Float, // Saturated fat score for females
    val UnsaturatedFatHEIFAscoreMale: Float, // Unsaturated fat score for males
    val UnsaturatedFatHEIFAscoreFemale: Float // Unsaturated fat score for females
)

// Function to load user data from the users.csv file in assets
fun loadUserDataFromCsv(context: Context, userId: String): UserData? {
    try {
        context.assets.open("users.csv").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                val headers = reader.readLine().split(",")
                val columnMap = headers.withIndex().associate { it.value to it.index }

                var line: String?
                // Read the file line by line
                while (reader.readLine().also { line = it } != null) {
                    val columns = line?.split(",") ?: continue
                    if (columns.size < columnMap.size) continue

                    val currentUserId = columns[columnMap["User_ID"] ?: 1].trim()

                    if (currentUserId == userId) {
                        return UserData(
                            userId = currentUserId,
                            phoneNumber = columns[columnMap["PhoneNumber"] ?: 0].trim(), // Phone number
                            sex = columns[columnMap["Sex"] ?: 2].trim(), // Gender
                            heifaTotalScoreMale = columns[columnMap["HEIFAtotalscoreMale"] ?: 3].toFloatOrNull() ?: 0f, // Total score for males
                            heifaTotalScoreFemale = columns[columnMap["HEIFAtotalscoreFemale"] ?: 4].toFloatOrNull() ?: 0f, // Total score for females

                            // Use the column mapping to get scores for each category, defaulting to 0f if parsing fails
                            DiscretionaryHEIFAscoreMale = columns[columnMap["DiscretionaryHEIFAscoreMale"] ?: 5].toFloatOrNull() ?: 0f,
                            DiscretionaryHEIFAscoreFemale = columns[columnMap["DiscretionaryHEIFAscoreFemale"] ?: 6].toFloatOrNull() ?: 0f,
                            VegetablesHEIFAscoreMale = columns[columnMap["VegetablesHEIFAscoreMale"] ?: 7].toFloatOrNull() ?: 0f,
                            VegetablesHEIFAscoreFemale = columns[columnMap["VegetablesHEIFAscoreFemale"] ?: 8].toFloatOrNull() ?: 0f,
                            FruitHEIFAscoreMale = columns[columnMap["FruitHEIFAscoreMale"] ?: 19].toFloatOrNull() ?: 0f,
                            FruitHEIFAscoreFemale = columns[columnMap["FruitHEIFAscoreFemale"] ?: 20].toFloatOrNull() ?: 0f,
                            GrainsandcerealsHEIFAscoreMale = columns[columnMap["GrainsandcerealsHEIFAscoreMale"] ?: 27].toFloatOrNull() ?: 0f,
                            GrainsandcerealsHEIFAscoreFemale = columns[columnMap["GrainsandcerealsHEIFAscoreFemale"] ?: 28].toFloatOrNull() ?: 0f,
                            WholegrainsHEIFAscoreMale = columns[columnMap["WholegrainsHEIFAscoreMale"] ?: 31].toFloatOrNull() ?: 0f,
                            WholegrainsHEIFAscoreFemale = columns[columnMap["WholegrainsHEIFAscoreFemale"] ?: 32].toFloatOrNull() ?: 0f,
                            MeatandalternativesHEIFAscoreMale = columns[columnMap["MeatandalternativesHEIFAscoreMale"] ?: 35].toFloatOrNull() ?: 0f,
                            MeatandalternativesHEIFAscoreFemale = columns[columnMap["MeatandalternativesHEIFAscoreFemale"] ?: 36].toFloatOrNull() ?: 0f,
                            DairyandalternativesHEIFAscoreMale = columns[columnMap["DairyandalternativesHEIFAscoreMale"] ?: 38].toFloatOrNull() ?: 0f,
                            DairyandalternativesHEIFAscoreFemale = columns[columnMap["DairyandalternativesHEIFAscoreFemale"] ?: 39].toFloatOrNull() ?: 0f,
                            SodiumHEIFAscoreMale = columns[columnMap["SodiumHEIFAscoreMale"] ?: 41].toFloatOrNull() ?: 0f,
                            SodiumHEIFAscoreFemale = columns[columnMap["SodiumHEIFAscoreFemale"] ?: 42].toFloatOrNull() ?: 0f,
                            AlcoholHEIFAscoreMale = columns[columnMap["AlcoholHEIFAscoreMale"] ?: 44].toFloatOrNull() ?: 0f,
                            AlcoholHEIFAscoreFemale = columns[columnMap["AlcoholHEIFAscoreFemale"] ?: 45].toFloatOrNull() ?: 0f,
                            WaterHEIFAscoreMale = columns[columnMap["WaterHEIFAscoreMale"] ?: 47].toFloatOrNull() ?: 0f,
                            WaterHEIFAscoreFemale = columns[columnMap["WaterHEIFAscoreFemale"] ?: 48].toFloatOrNull() ?: 0f,
                            SugarHEIFAscoreMale = columns[columnMap["SugarHEIFAscoreMale"] ?: 52].toFloatOrNull() ?: 0f,
                            SugarHEIFAscoreFemale = columns[columnMap["SugarHEIFAscoreFemale"] ?: 53].toFloatOrNull() ?: 0f,
                            SaturatedFatHEIFAscoreMale = columns[columnMap["SaturatedFatHEIFAscoreMale"] ?: 55].toFloatOrNull() ?: 0f,
                            SaturatedFatHEIFAscoreFemale = columns[columnMap["SaturatedFatHEIFAscoreFemale"] ?: 56].toFloatOrNull() ?: 0f,
                            UnsaturatedFatHEIFAscoreMale = columns[columnMap["UnsaturatedFatHEIFAscoreMale"] ?: 58].toFloatOrNull() ?: 0f,
                            UnsaturatedFatHEIFAscoreFemale = columns[columnMap["UnsaturatedFatHEIFAscoreFemale"] ?: 59].toFloatOrNull() ?: 0f
                        )
                    }
                }
            }
        }
    } catch (e: Exception) {
        // Print stack trace if an exception occurs while reading the file or parsing data
        e.printStackTrace()
    }
    // Return null if no matching user is found or an error occurs
    return null
}