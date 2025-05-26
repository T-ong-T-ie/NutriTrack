package com.fit2081.hulongxi33555397.db

import android.content.Context
import com.fit2081.hulongxi33555397.BuildConfig
import com.fit2081.hulongxi33555397.models.Questionnaire
import com.fit2081.hulongxi33555397.models.FruitDetails
import com.fit2081.hulongxi33555397.models.NutriCoachTip as ModelNutriCoachTip
import com.fit2081.hulongxi33555397.db.NutriCoachTip as DbNutriCoachTip
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader

class NutritrackRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val patientDao = database.patientDao()
    private val foodIntakeDao = database.foodIntakeDao()
    private val nutriCoachTipDao = database.nutriCoachTipDao()
    private val questionnaireDao = database.questionnaireDao()

    // Import patient data from a CSV file
    suspend fun importPatientsFromCsv(): Int {
        return withContext(Dispatchers.IO) {
            try {
                val patients = loadPatientsFromCsv()
                if (patients.isNotEmpty()) {
                    patientDao.insertAll(patients)
                }
                patients.size
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }
    }

    // Add a method that explicitly checks if the user is registered
    suspend fun isUserRegistered(userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val patient = patientDao.getPatientById(userId)
            patient != null && !patient.password.isNullOrBlank()
        }
    }

    // Check if the database is empty
    suspend fun patientCount(): Int {
        return withContext(Dispatchers.IO) {
            patientDao.getPatientCount()
        }
    }

    // Get all the patients
    suspend fun getAllPatients(): List<Patient> {
        return withContext(Dispatchers.IO) {
            patientDao.getAllPatients()
        }
    }

    // Saving the questionnaire responses
    suspend fun saveFoodIntake(foodIntake: FoodIntake): Long {
        return withContext(Dispatchers.IO) {
            foodIntakeDao.insert(foodIntake)
        }
    }

    // The latest questionnaire responses from the patients were obtained
    suspend fun getLatestFoodIntake(patientId: String): FoodIntake? {
        return withContext(Dispatchers.IO) {
            foodIntakeDao.getLatestByPatientId(patientId)
        }
    }

    // Getting patient information
    suspend fun getPatientById(userId: String): Patient? {
        return withContext(Dispatchers.IO) {
            patientDao.getPatientById(userId)
        }
    }

    // User authentication
    suspend fun authenticateUser(userId: String, password: String): Patient? {
        return withContext(Dispatchers.IO) {
            patientDao.authenticateUser(userId, password)
        }
    }

    // Updating user information
    suspend fun updatePatient(patient: Patient) {
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("NutritrackRepository", "Update User：${patient.userId}, Password=${patient.password?.isNotBlank()}")
                patientDao.updatePatient(patient)
                android.util.Log.d("NutritrackRepository", "User updated successfully")
            } catch (e: Exception) {
                android.util.Log.e("NutritrackRepository", "Update user failed", e)
                throw e
            }
        }
    }

    suspend fun getQuestionnaireByUserId(userId: String): Questionnaire? {
        return withContext(Dispatchers.IO) {
            questionnaireDao.getQuestionnaireByUserId(userId)
        }
    }

    // Add Nutrition Tips
    suspend fun saveTip(tip: ModelNutriCoachTip): Long {
        return withContext(Dispatchers.IO) {
            val dbTip = DbNutriCoachTip(
                userId = tip.userId,
                category = tip.category,
                content = tip.content,
                timestamp = tip.timestamp
            )
            nutriCoachTipDao.insertTip(dbTip)
        }
    }

    // Get all prompts for a user
    suspend fun getUserTips(userId: String): List<ModelNutriCoachTip> {
        return withContext(Dispatchers.IO) {
            nutriCoachTipDao.getTipsForUser(userId).map { dbTip ->
                ModelNutriCoachTip(
                    userId = dbTip.userId,
                    category = dbTip.category,
                    content = dbTip.content,
                    timestamp = dbTip.timestamp
                )
            }
        }
    }

    // Questionnaire-related methods
    suspend fun getUserCategories(userId: String): List<String> {
        return withContext(Dispatchers.IO) {
            val questionnaire = questionnaireDao.getQuestionnaireByUserId(userId)
            questionnaire?.categories?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
        }
    }

    suspend fun getUserPersona(userId: String): String? {
        return withContext(Dispatchers.IO) {
            questionnaireDao.getQuestionnaireByUserId(userId)?.persona
        }
    }

    suspend fun getUserTimes(userId: String): List<String> {
        return withContext(Dispatchers.IO) {
            val questionnaire = questionnaireDao.getQuestionnaireByUserId(userId)
            if (questionnaire != null) {
                listOf(
                    questionnaire.biggestMealTime,
                    questionnaire.sleepTime,
                    questionnaire.wakeTime
                )
            } else {
                List(3) { "" }
            }
        }
    }

    suspend fun saveUserQuestionnaire(userId: String, categories: List<String>, persona: String, times: List<String>) {
        withContext<Unit>(Dispatchers.IO) {
            val questionnaire = Questionnaire(
                userId = userId,
                categories = categories.joinToString(","),
                persona = persona,
                biggestMealTime = times[0],
                sleepTime = times[1],
                wakeTime = times[2]
            )
            questionnaireDao.insert(questionnaire)
        }
    }

    suspend fun initializeDatabase() {
        // Check if the database is empty
        if (patientCount() == 0) {
            // Read all user data from users.csv
            withContext(Dispatchers.IO) {
                try {
                    // Get all user IDs
                    val userIds = mutableListOf<String>()
                    context.assets.open("users.csv").use { inputStream ->
                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
                            // Skip header row
                            val headers = reader.readLine()
                            var line: String?
                            while (reader.readLine().also { line = it } != null) {
                                val columns = line?.split(",") ?: continue
                                if (columns.size > 1) {
                                    val currentUserId = columns[1].trim()
                                    userIds.add(currentUserId)
                                }
                            }
                        }
                    }

                    // Load detailed data for each user ID and create a Patient record
                    for (userId in userIds) {
                        val userData = loadUserDataFromCsv(context, userId)
                        if (userData != null) {
                            createPatientFromUserData(userData)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Search for fruit information
    suspend fun searchFruit(fruitName: String): FruitDetails {
        return withContext(Dispatchers.IO) {
            val url = URL("https://www.fruityvice.com/api/fruit/$fruitName")
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val inputStream = connection.inputStream
                val jsonResponse = inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(jsonResponse)

                val nutritionsObject = jsonObject.getJSONObject("nutritions")
                val nutritions = FruitDetails.Nutritions(
                    calories = nutritionsObject.getDouble("calories").toFloat(),
                    fat = nutritionsObject.getDouble("fat").toFloat(),
                    sugar = nutritionsObject.getDouble("sugar").toFloat(),
                    carbohydrates = nutritionsObject.getDouble("carbohydrates").toFloat(),
                    protein = nutritionsObject.getDouble("protein").toFloat()
                )

                FruitDetails(
                    name = jsonObject.getString("name"),
                    family = jsonObject.getString("family"),
                    nutritions = nutritions
                )
            } finally {
                connection.disconnect()
            }
        }
    }

    // Generate AI reply function
    suspend fun generateAIResponse(
        userId: String,
        fruitName: String?,
        fruitFamily: String?,
        fruitNutrition: FruitDetails.Nutritions?,
        isMale: Boolean,
        fruitScore: Float
    ): String {
        return withContext(Dispatchers.IO) {
            try {
                val questionnaire = questionnaireDao.getQuestionnaireByUserId(userId)
                val prompt = buildString {
                    append("Generate a short English motivational message based on the following user information to help people improve their fruit intake, without using Markdown formatting:\n")
                    append("Sex: ${if (isMale) "Male" else "Female"}\n")
                    append("Fruit intake score: $fruitScore/10\n")

                    if (questionnaire != null) {
                        append("Dietary preferences: ${questionnaire.categories}\n")
                        append("User Type: ${questionnaire.persona}\n")
                        append("Maximum meal time: ${questionnaire.biggestMealTime}\n")
                    }

                    if (fruitName != null) {
                        append("Recently searched fruits: $fruitName\n")
                        append("Fruit family: $fruitFamily\n")

                        if (fruitNutrition != null) {
                            append("Fruit Nutrition Facts:\n")
                            append("- Calories: ${fruitNutrition.calories}\n")
                            append("- Fat: ${fruitNutrition.fat}g\n")
                            append("- Sugar: ${fruitNutrition.sugar}g\n")
                            append("- Carbohydrate: ${fruitNutrition.carbohydrates}g\n")
                            append("- Protein: ${fruitNutrition.protein}g\n")
                        }
                    }

                    append("Please provide personalized nutritional advice, including how to improve eating habits and fruit intake.")
                }

                // Generate AI responses using GenerativeModel
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash-latest",
                    apiKey = BuildConfig.GEMINI_API_KEY
                )
                val response = generativeModel.generateContent(prompt)
                response.text ?: "Unable to generate suggestions, please try again later"
            } catch (e: Exception) {
                e.printStackTrace()
                "Problem generating suggestions: ${e.message}"
            }
        }
    }

    // Creating a Patient from UserData
    suspend fun createPatientFromUserData(userData: UserData): Patient? {
        return withContext(Dispatchers.IO) {
            try {
                val patient = Patient(
                    userId = userData.userId,
                    phoneNumber = userData.phoneNumber,
                    name = null, // Will be set by the user
                    password = null, // Will be set by the user
                    sex = userData.sex,
                    heifaTotalScoreMale = userData.heifaTotalScoreMale,
                    heifaTotalScoreFemale = userData.heifaTotalScoreFemale,
                    DiscretionaryHEIFAscoreMale = userData.DiscretionaryHEIFAscoreMale,
                    DiscretionaryHEIFAscoreFemale = userData.DiscretionaryHEIFAscoreFemale,
                    VegetablesHEIFAscoreMale = userData.VegetablesHEIFAscoreMale,
                    VegetablesHEIFAscoreFemale = userData.VegetablesHEIFAscoreFemale,
                    FruitHEIFAscoreMale = userData.FruitHEIFAscoreMale,
                    FruitHEIFAscoreFemale = userData.FruitHEIFAscoreFemale,
                    GrainsandcerealsHEIFAscoreMale = userData.GrainsandcerealsHEIFAscoreMale,
                    GrainsandcerealsHEIFAscoreFemale = userData.GrainsandcerealsHEIFAscoreFemale,
                    WholegrainsHEIFAscoreMale = userData.WholegrainsHEIFAscoreMale,
                    WholegrainsHEIFAscoreFemale = userData.WholegrainsHEIFAscoreFemale,
                    MeatandalternativesHEIFAscoreMale = userData.MeatandalternativesHEIFAscoreMale,
                    MeatandalternativesHEIFAscoreFemale = userData.MeatandalternativesHEIFAscoreFemale,
                    DairyandalternativesHEIFAscoreMale = userData.DairyandalternativesHEIFAscoreMale,
                    DairyandalternativesHEIFAscoreFemale = userData.DairyandalternativesHEIFAscoreFemale,
                    SodiumHEIFAscoreMale = userData.SodiumHEIFAscoreMale,
                    SodiumHEIFAscoreFemale = userData.SodiumHEIFAscoreFemale,
                    AlcoholHEIFAscoreMale = userData.AlcoholHEIFAscoreMale,
                    AlcoholHEIFAscoreFemale = userData.AlcoholHEIFAscoreFemale,
                    WaterHEIFAscoreMale = userData.WaterHEIFAscoreMale,
                    WaterHEIFAscoreFemale = userData.WaterHEIFAscoreFemale,
                    SugarHEIFAscoreMale = userData.SugarHEIFAscoreMale,
                    SugarHEIFAscoreFemale = userData.SugarHEIFAscoreFemale,
                    SaturatedFatHEIFAscoreMale = userData.SaturatedFatHEIFAscoreMale,
                    SaturatedFatHEIFAscoreFemale = userData.SaturatedFatHEIFAscoreFemale,
                    UnsaturatedFatHEIFAscoreMale = userData.UnsaturatedFatHEIFAscoreMale,
                    UnsaturatedFatHEIFAscoreFemale = userData.UnsaturatedFatHEIFAscoreFemale
                )
                patientDao.insert(patient)
                patient
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun createPatientFromCsv(userId: String, phoneNumber: String): Patient? {
        return withContext(Dispatchers.IO) {
            try {
                val existingPatient = patientDao.getPatientById(userId)
                if (existingPatient != null) {
                    return@withContext existingPatient
                }

                val newPatient = Patient(
                    userId = userId,
                    phoneNumber = phoneNumber,
                    password = "password",
                    name = "Unnamed User",
                    sex = "not specified",
                    heifaTotalScoreMale = 0f,
                    heifaTotalScoreFemale = 0f,
                    DiscretionaryHEIFAscoreMale = 0f,
                    DiscretionaryHEIFAscoreFemale = 0f,
                    VegetablesHEIFAscoreMale = 0f,
                    VegetablesHEIFAscoreFemale = 0f,
                    FruitHEIFAscoreMale = 0f,
                    FruitHEIFAscoreFemale = 0f,
                    GrainsandcerealsHEIFAscoreMale = 0f,
                    GrainsandcerealsHEIFAscoreFemale = 0f,
                    WholegrainsHEIFAscoreMale = 0f,
                    WholegrainsHEIFAscoreFemale = 0f,
                    MeatandalternativesHEIFAscoreMale = 0f,
                    MeatandalternativesHEIFAscoreFemale = 0f,
                    DairyandalternativesHEIFAscoreMale = 0f,
                    DairyandalternativesHEIFAscoreFemale = 0f,
                    SodiumHEIFAscoreMale = 0f,
                    SodiumHEIFAscoreFemale = 0f,
                    AlcoholHEIFAscoreMale = 0f,
                    AlcoholHEIFAscoreFemale = 0f,
                    WaterHEIFAscoreMale = 0f,
                    WaterHEIFAscoreFemale = 0f,
                    SugarHEIFAscoreMale = 0f,
                    SugarHEIFAscoreFemale = 0f,
                    SaturatedFatHEIFAscoreMale = 0f,
                    SaturatedFatHEIFAscoreFemale = 0f,
                    UnsaturatedFatHEIFAscoreMale = 0f,
                    UnsaturatedFatHEIFAscoreFemale = 0f
                )
                patientDao.insert(newPatient)
                newPatient
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun loadPatientsFromCsv(): List<Patient> {
        val patients = mutableListOf<Patient>()
        try {
            context.assets.open("patients.csv").bufferedReader().use { reader ->
                // Skip header row
                reader.readLine()

                var line = reader.readLine()
                while (line != null) {
                    val tokens = line.split(",")
                    if (tokens.size >= 2) {
                        val userId = tokens[0].trim()
                        val phoneNumber = tokens[1].trim()

                        patients.add(
                            Patient(
                                userId = userId,
                                phoneNumber = phoneNumber,
                                password = "password",
                                name = "Unnamed User",
                                sex = "not specified",
                                heifaTotalScoreMale = 0f,
                                heifaTotalScoreFemale = 0f,
                                DiscretionaryHEIFAscoreMale = 0f,
                                DiscretionaryHEIFAscoreFemale = 0f,
                                VegetablesHEIFAscoreMale = 0f,
                                VegetablesHEIFAscoreFemale = 0f,
                                FruitHEIFAscoreMale = 0f,
                                FruitHEIFAscoreFemale = 0f,
                                GrainsandcerealsHEIFAscoreMale = 0f,
                                GrainsandcerealsHEIFAscoreFemale = 0f,
                                WholegrainsHEIFAscoreMale = 0f,
                                WholegrainsHEIFAscoreFemale = 0f,
                                MeatandalternativesHEIFAscoreMale = 0f,
                                MeatandalternativesHEIFAscoreFemale = 0f,
                                DairyandalternativesHEIFAscoreMale = 0f,
                                DairyandalternativesHEIFAscoreFemale = 0f,
                                SodiumHEIFAscoreMale = 0f,
                                SodiumHEIFAscoreFemale = 0f,
                                AlcoholHEIFAscoreMale = 0f,
                                AlcoholHEIFAscoreFemale = 0f,
                                WaterHEIFAscoreMale = 0f,
                                WaterHEIFAscoreFemale = 0f,
                                SugarHEIFAscoreMale = 0f,
                                SugarHEIFAscoreFemale = 0f,
                                SaturatedFatHEIFAscoreMale = 0f,
                                SaturatedFatHEIFAscoreFemale = 0f,
                                UnsaturatedFatHEIFAscoreMale = 0f,
                                UnsaturatedFatHEIFAscoreFemale = 0f
                            )
                        )
                    }
                    line = reader.readLine()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return patients
    }
}