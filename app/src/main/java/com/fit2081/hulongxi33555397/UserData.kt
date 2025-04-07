package com.fit2081.hulongxi33555397

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

// 定义用户数据的结构体，包含用户基本信息和各类别 HEIFA 分数（男女分开）
data class UserData(
    val userId: String, // 用户 ID
    val phoneNumber: String, // 用户电话号码
    val sex: String, // 用户性别
    val heifaTotalScoreMale: Float, // 男性总 HEIFA 分数
    val heifaTotalScoreFemale: Float, // 女性总 HEIFA 分数

    // 以下为各类别得分，分别针对男性和女性
    val DiscretionaryHEIFAscoreMale: Float, // 男性随意食物得分
    val DiscretionaryHEIFAscoreFemale: Float, // 女性随意食物得分
    val VegetablesHEIFAscoreMale: Float, // 男性蔬菜得分
    val VegetablesHEIFAscoreFemale: Float, // 女性蔬菜得分
    val FruitHEIFAscoreMale: Float, // 男性水果得分
    val FruitHEIFAscoreFemale: Float, // 女性水果得分
    val GrainsandcerealsHEIFAscoreMale: Float, // 男性谷物和谷类得分
    val GrainsandcerealsHEIFAscoreFemale: Float, // 女性谷物和谷类得分
    val WholegrainsHEIFAscoreMale: Float, // 男性全谷物得分
    val WholegrainsHEIFAscoreFemale: Float, // 女性全谷物得分
    val MeatandalternativesHEIFAscoreMale: Float, // 男性肉类及替代品得分
    val MeatandalternativesHEIFAscoreFemale: Float, // 女性肉类及替代品得分
    val DairyandalternativesHEIFAscoreMale: Float, // 男性乳制品及替代品得分
    val DairyandalternativesHEIFAscoreFemale: Float, // 女性乳制品及替代品得分
    val SodiumHEIFAscoreMale: Float, // 男性钠得分
    val SodiumHEIFAscoreFemale: Float, // 女性钠得分
    val AlcoholHEIFAscoreMale: Float, // 男性酒精得分
    val AlcoholHEIFAscoreFemale: Float, // 女性酒精得分
    val WaterHEIFAscoreMale: Float, // 男性水得分
    val WaterHEIFAscoreFemale: Float, // 女性水得分
    val SugarHEIFAscoreMale: Float, // 男性添加糖得分
    val SugarHEIFAscoreFemale: Float, // 女性添加糖得分
    val SaturatedFatHEIFAscoreMale: Float, // 男性饱和脂肪得分
    val SaturatedFatHEIFAscoreFemale: Float, // 女性饱和脂肪得分
    val UnsaturatedFatHEIFAscoreMale: Float, // 男性不饱和脂肪得分
    val UnsaturatedFatHEIFAscoreFemale: Float // 女性不饱和脂肪得分
)

// 从 assets 中的 users.csv 文件加载用户数据的函数
fun loadUserDataFromCsv(context: Context, userId: String): UserData? {
    try {
        // 打开 assets 目录下的 users.csv 文件
        context.assets.open("users.csv").use { inputStream ->
            // 使用 BufferedReader 读取 CSV 文件内容
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // 读取第一行（标题行），并按逗号分隔创建列名到索引的映射
                val headers = reader.readLine().split(",")
                val columnMap = headers.withIndex().associate { it.value to it.index }

                var line: String?
                // 逐行读取文件内容
                while (reader.readLine().also { line = it } != null) {
                    // 将当前行按逗号分隔成列
                    val columns = line?.split(",") ?: continue
                    // 如果列数不足，跳过该行
                    if (columns.size < columnMap.size) continue

                    // 获取当前行的用户 ID 并去除首尾空白
                    val currentUserId = columns[columnMap["User_ID"] ?: 1].trim()

                    // 如果找到匹配的用户 ID，则构造并返回 UserData 对象
                    if (currentUserId == userId) {
                        return UserData(
                            userId = currentUserId,
                            phoneNumber = columns[columnMap["PhoneNumber"] ?: 0].trim(), // 电话号码
                            sex = columns[columnMap["Sex"] ?: 2].trim(), // 性别
                            heifaTotalScoreMale = columns[columnMap["HEIFAtotalscoreMale"] ?: 3].toFloatOrNull() ?: 0f, // 男性总分
                            heifaTotalScoreFemale = columns[columnMap["HEIFAtotalscoreFemale"] ?: 4].toFloatOrNull() ?: 0f, // 女性总分

                            // 使用列名映射获取各类别得分，失败时默认值为 0f
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
        // 如果读取文件或解析数据时发生异常，打印堆栈跟踪
        e.printStackTrace()
    }
    // 如果未找到匹配的用户或发生错误，返回 null
    return null
}