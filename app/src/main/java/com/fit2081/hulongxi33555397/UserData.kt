package com.fit2081.hulongxi33555397

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader


data class UserData(
    val userId: String,
    val phoneNumber: String,
    val sex: String,
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

// 从CSV加载用户数据的函数
fun loadUserDataFromCsv(context: Context, userId: String): UserData? {
    try {
        context.assets.open("users.csv").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // 读取标题行并创建列名到索引的映射
                val headers = reader.readLine().split(",")
                val columnMap = headers.withIndex().associate { it.value to it.index }

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val columns = line?.split(",") ?: continue
                    if (columns.size < columnMap.size) continue

                    val currentUserId = columns[columnMap["User_ID"] ?: 1].trim()

                    if (currentUserId == userId) {
                        return UserData(
                            userId = currentUserId,
                            phoneNumber = columns[columnMap["PhoneNumber"] ?: 0].trim(),
                            sex = columns[columnMap["Sex"] ?: 2].trim(),
                            heifaTotalScoreMale = columns[columnMap["HEIFAtotalscoreMale"] ?: 3].toFloatOrNull() ?: 0f,
                            heifaTotalScoreFemale = columns[columnMap["HEIFAtotalscoreFemale"] ?: 4].toFloatOrNull() ?: 0f,

                            // 使用列名获取对应索引的数据
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
        e.printStackTrace()
    }
    return null
}