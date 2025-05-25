package com.fit2081.hulongxi33555397.db

import android.content.Context
import com.fit2081.hulongxi33555397.loadUserDataFromCsv
import java.io.BufferedReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NutritrackRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val patientDao = database.patientDao()
    private val foodIntakeDao = database.foodIntakeDao()
    private val nutriCoachTipDao = database.nutriCoachTipDao()

    // 从CSV文件导入患者数据
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

    // 检查数据库是否为空
    suspend fun patientCount(): Int {
        return withContext(Dispatchers.IO) {
            patientDao.getPatientCount()
        }
    }

    // 获取所有患者
    suspend fun getAllPatients(): List<Patient> {
        return withContext(Dispatchers.IO) {
            patientDao.getAllPatients()
        }
    }

    // 保存问卷回答
    suspend fun saveFoodIntake(foodIntake: FoodIntake): Long {
        return withContext(Dispatchers.IO) {
            foodIntakeDao.insert(foodIntake)
        }
    }

    // 获取患者的最新问卷回答
    suspend fun getLatestFoodIntake(patientId: String): FoodIntake? {
        return withContext(Dispatchers.IO) {
            foodIntakeDao.getLatestByPatientId(patientId)
        }
    }

    // 获取患者信息
    suspend fun getPatientById(userId: String): Patient? {
        return withContext(Dispatchers.IO) {
            patientDao.getPatientById(userId)
        }
    }

    // 用户认证
    suspend fun authenticateUser(userId: String, password: String): Patient? {
        return withContext(Dispatchers.IO) {
            patientDao.authenticateUser(userId, password)
        }
    }

    // 更新用户信息
    suspend fun updatePatient(patient: Patient) {
        withContext(Dispatchers.IO) {
            patientDao.updatePatient(patient)
        }
    }

    // 添加提示
    suspend fun saveTip(tip: NutriCoachTip): Long {
        return nutriCoachTipDao.insertTip(tip)
    }

    // 获取用户的所有提示
    suspend fun getUserTips(userId: String): List<NutriCoachTip> {
        return nutriCoachTipDao.getTipsForUser(userId)
    }

    // 从UserData创建Patient对象
    suspend fun createPatientFromUserData(userData: com.fit2081.hulongxi33555397.UserData): Patient? {
        return withContext(Dispatchers.IO) {
            try {
                val patient = Patient(
                    userId = userData.userId,
                    phoneNumber = userData.phoneNumber,
                    name = null, // 将由用户设置
                    password = null, // 将由用户设置
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

    // 从CSV数据创建新用户
    suspend fun createPatientFromCsv(userId: String, phoneNumber: String): Patient? {
        return withContext(Dispatchers.IO) {
            try {
                // 使用现有的loadUserDataFromCsv工具函数
                val userData = loadUserDataFromCsv(context, userId)
                if (userData != null) {
                    val patient = Patient(
                        userId = userData.userId,
                        phoneNumber = userData.phoneNumber,
                        name = null, // 将由用户设置
                        password = null, // 将由用户设置
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
                    return@withContext patient
                }
                null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // 从CSV文件加载患者数据
    private fun loadPatientsFromCsv(): List<Patient> {
        val patients = mutableListOf<Patient>()
        try {
            context.assets.open("users.csv").use { inputStream ->
                val reader = BufferedReader(inputStream.reader())

                // 读取表头行
                val headers = reader.readLine().split(",")
                val columnMap = headers.withIndex().associate { it.value to it.index }

                // 逐行读取并解析数据
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val columns = line?.split(",") ?: continue
                    if (columns.size < 30) continue // 确保至少有必要的列

                    try {
                        val userId = columns[columnMap["User_ID"] ?: 1].trim()
                        val phoneNumber = columns[columnMap["PhoneNumber"] ?: 0].trim()
                        val sex = columns[columnMap["Sex"] ?: 2].trim()

                        val patient = Patient(
                            userId = userId,
                            phoneNumber = phoneNumber,
                            name = null, // CSV没有Name字段，设为null
                            password = null, // 初始导入时密码为null
                            sex = sex,
                            heifaTotalScoreMale = columns[columnMap["HEIFAtotalscoreMale"] ?: 3].toFloatOrNull() ?: 0f,
                            heifaTotalScoreFemale = columns[columnMap["HEIFAtotalscoreFemale"] ?: 4].toFloatOrNull() ?: 0f,
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
                        patients.add(patient)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return patients
    }
}
