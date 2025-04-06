package com.fit2081.hulongxi33555397

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.times

data class UserData(
    val userId: String,
    val phoneNumber: String,
    val sex: String,
    val heifaTotalScoreMale: Float,
    val heifaTotalScoreFemale: Float,
    // 添加CSV中的其他字段
    val discretionaryHEIFAscoreMale: Float,
    val discretionaryHEIFAscoreFemale: Float,
    val vegetablesHEIFAscoreMale: Float,
    val vegetablesHEIFAscoreFemale: Float,
    val fruitHEIFAscoreMale: Float,
    val fruitHEIFAscoreFemale: Float,
    val grainsAndCerealsHEIFAscoreMale: Float,
    val grainsAndCerealsHEIFAscoreFemale: Float,
    val meatAndAlternativesHEIFAscoreMale: Float,
    val meatAndAlternativesHEIFAscoreFemale: Float,
    val dairyAndAlternativesHEIFAscoreMale: Float,
    val dairyAndAlternativesHEIFAscoreFemale: Float,
    val sodiumHEIFAscoreMale: Float,
    val sodiumHEIFAscoreFemale: Float,
    val alcoholHEIFAscoreMale: Float,
    val alcoholHEIFAscoreFemale: Float,
    val waterHEIFAscoreMale: Float,
    val waterHEIFAscoreFemale: Float,
    val sugarHEIFAscoreMale: Float,
    val sugarHEIFAscoreFemale: Float,
    val saturatedFatHEIFAscoreMale: Float,
    val saturatedFatHEIFAscoreFemale: Float,
    val unsaturatedFatHEIFAscoreMale: Float,
    val unsaturatedFatHEIFAscoreFemale: Float
)

// 从CSV加载用户数据的函数
fun loadUserDataFromCsv(context: Context, userId: String): UserData? {
    try {
        context.assets.open("users.csv").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // 跳过标题行
                val headers = reader.readLine()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    val columns = line?.split(",") ?: continue
                    if (columns.size < 32) continue // 确保有足够的列

                    val currentUserId = columns[1].trim()
                    if (currentUserId == userId) {
                        return UserData(
                            userId = currentUserId,
                            phoneNumber = columns[0].trim(),
                            sex = columns[2].trim(),
                            heifaTotalScoreMale = columns[3].toFloatOrNull() ?: 0f,
                            heifaTotalScoreFemale = columns[4].toFloatOrNull() ?: 0f,
                            discretionaryHEIFAscoreMale = columns[5].toFloatOrNull() ?: 0f,
                            discretionaryHEIFAscoreFemale = columns[6].toFloatOrNull() ?: 0f,
                            vegetablesHEIFAscoreMale = columns[7].toFloatOrNull() ?: 0f,
                            vegetablesHEIFAscoreFemale = columns[8].toFloatOrNull() ?: 0f,
                            fruitHEIFAscoreMale = columns[9].toFloatOrNull() ?: 0f,
                            fruitHEIFAscoreFemale = columns[10].toFloatOrNull() ?: 0f,
                            grainsAndCerealsHEIFAscoreMale = columns[11].toFloatOrNull() ?: 0f,
                            grainsAndCerealsHEIFAscoreFemale = columns[12].toFloatOrNull() ?: 0f,
                            meatAndAlternativesHEIFAscoreMale = columns[13].toFloatOrNull() ?: 0f,
                            meatAndAlternativesHEIFAscoreFemale = columns[14].toFloatOrNull() ?: 0f,
                            dairyAndAlternativesHEIFAscoreMale = columns[15].toFloatOrNull() ?: 0f,
                            dairyAndAlternativesHEIFAscoreFemale = columns[16].toFloatOrNull() ?: 0f,
                            sodiumHEIFAscoreMale = columns[17].toFloatOrNull() ?: 0f,
                            sodiumHEIFAscoreFemale = columns[18].toFloatOrNull() ?: 0f,
                            alcoholHEIFAscoreMale = columns[19].toFloatOrNull() ?: 0f,
                            alcoholHEIFAscoreFemale = columns[20].toFloatOrNull() ?: 0f,
                            waterHEIFAscoreMale = columns[21].toFloatOrNull() ?: 0f,
                            waterHEIFAscoreFemale = columns[22].toFloatOrNull() ?: 0f,
                            sugarHEIFAscoreMale = columns[23].toFloatOrNull() ?: 0f,
                            sugarHEIFAscoreFemale = columns[24].toFloatOrNull() ?: 0f,
                            saturatedFatHEIFAscoreMale = columns[25].toFloatOrNull() ?: 0f,
                            saturatedFatHEIFAscoreFemale = columns[26].toFloatOrNull() ?: 0f,
                            unsaturatedFatHEIFAscoreMale = columns[27].toFloatOrNull() ?: 0f,
                            unsaturatedFatHEIFAscoreFemale = columns[28].toFloatOrNull() ?: 0f
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

// 根据用户数据和选择的食物类别计算得分
fun calculateScoreFromUserData(userData: UserData?, selectedCategories: List<String>): List<CategoryScore> {
    if (userData == null) {
        return emptyList()
    }

    val isMale = userData.sex == "Male"

    return listOf(
        // 自由选择食物
        CategoryScore(
            name = "Discretionary",
            score = if (isMale) userData.discretionaryHEIFAscoreMale else userData.discretionaryHEIFAscoreFemale,
            maxScore = 10f
        ),

        // 蔬菜
        CategoryScore(
            name = "Vegetables",
            score = if (isMale) userData.vegetablesHEIFAscoreMale else userData.vegetablesHEIFAscoreFemale,
            maxScore = 10f
        ),

        // 水果
        CategoryScore(
            name = "Fruits",
            score = if (isMale) userData.fruitHEIFAscoreMale else userData.fruitHEIFAscoreFemale,
            maxScore = 10f
        ),

        // 谷物
        CategoryScore(
            name = "Grains",
            score = if (isMale) userData.grainsAndCerealsHEIFAscoreMale else userData.grainsAndCerealsHEIFAscoreFemale,
            maxScore = 10f
        ),

        // 全麦谷物
        CategoryScore(
            name = "Whole grains",
            score = if (isMale) userData.grainsAndCerealsHEIFAscoreMale * 0.5f else userData.grainsAndCerealsHEIFAscoreFemale * 0.5f,
            maxScore = 10f
        ),

        // 肉类及替代品
        CategoryScore(
            name = "Meat & Alternatives",
            score = if (isMale) userData.meatAndAlternativesHEIFAscoreMale else userData.meatAndAlternativesHEIFAscoreFemale,
            maxScore = 10f
        ),

        // 乳制品
        CategoryScore(
            name = "Dairy & Alternatives",
            score = if (isMale) userData.dairyAndAlternativesHEIFAscoreMale else userData.dairyAndAlternativesHEIFAscoreFemale,
            maxScore = 10f
        ),

        // 钠
        CategoryScore(
            name = "Sodium",
            score = if (isMale) userData.sodiumHEIFAscoreMale else userData.sodiumHEIFAscoreFemale,
            maxScore = 10f
        ),

        // 酒精
        CategoryScore(
            name = "Alcohol",
            score = if (isMale) userData.alcoholHEIFAscoreMale else userData.alcoholHEIFAscoreFemale,
            maxScore = 5f
        ),

        // 水
        CategoryScore(
            name = "Water",
            score = if (isMale) userData.waterHEIFAscoreMale else userData.waterHEIFAscoreFemale,
            maxScore = 5f
        ),

        // 添加糖
        CategoryScore(
            name = "Added Sugar",
            score = if (isMale) userData.sugarHEIFAscoreMale else userData.sugarHEIFAscoreFemale,
            maxScore = 10f
        ),

        // 饱和脂肪
        CategoryScore(
            name = "Saturated Fat",
            score = if (isMale) userData.saturatedFatHEIFAscoreMale else userData.saturatedFatHEIFAscoreFemale,
            maxScore = 10f
        ),

        // 不饱和脂肪
        CategoryScore(
            name = "Unsaturated Fat",
            score = if (isMale) userData.unsaturatedFatHEIFAscoreMale else userData.unsaturatedFatHEIFAscoreFemale,
            maxScore = 10f
        )
    )
}

// 生成改善建议的函数
fun getImprovementSuggestion(categoryName: String): String {
    return when(categoryName) {
        "Discretionary" -> "Try to limit intake of high-sugar, high-salt, and high-fat foods like chips, candy, and soft drinks. Recommended less than 3 servings daily for men and less than 2.5 for women."
        "Vegetables" -> "Increase vegetable intake to at least 5-6 servings daily. One serving is about 75g of cooked vegetables or 1 cup of salad. Try to include more leafy greens and cruciferous vegetables."
        "Fruits" -> "Eat at least 2 servings of fruit daily. One serving is about 150g, equivalent to 1 medium or 2 small fruits. Variety in fruit types provides more nutrients."
        "Grains" -> "Choose whole grain foods like whole wheat bread, brown rice, and oats. Whole grains retain the bran, germ, and endosperm, making them more nutritious."
        "Meat & Alternatives" -> "Choose lean meats, fish, legumes, eggs, and nuts as protein sources. One serving is about 65-100g of cooked meat, 2 eggs, or 30g of nuts."
        "Dairy & Alternatives" -> "Consume appropriate amounts of dairy or plant-based alternatives daily. One serving is about 250ml of milk, 200g of yogurt, or 40g of cheese."
        "Sodium" -> "Reduce salt intake to less than 2000mg of sodium daily. Eat fewer processed foods and takeout meals, which often contain high amounts of sodium."
        "Alcohol" -> "If you drink alcohol, do so in moderation. No more than 1.4 standard drinks (10g pure alcohol per drink) daily. Not drinking is best."
        "Water" -> "Drink enough water daily, recommended 2-2.5 liters (8-10 cups). Make water your primary beverage and reduce intake of sugary drinks."
        "Added Sugar" -> "Limit added sugar intake to less than 10% of total energy intake. Reduce consumption of desserts, candies, and sugary drinks."
        "Saturated Fat" -> "Limit saturated fat intake from sources like fatty meats, full-fat dairy, and coconut oil. Choose leaner protein options and low-fat dairy products."
        "Unsaturated Fat" -> "Choose healthy fat sources like olive oil, fish oils, avocados, and nuts. Aim for adequate intake of unsaturated fats while limiting saturated fats."
        else -> "Maintain a balanced diet with a variety of food choices, increase intake of fruits, vegetables, and whole grains, and reduce intake of high-salt, high-sugar, and high-fat foods."
    }
}