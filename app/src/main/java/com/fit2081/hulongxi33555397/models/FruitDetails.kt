package com.fit2081.hulongxi33555397.models

data class FruitDetails(
    val name: String,
    val family: String,
    val nutritions: Nutritions
) {
    data class Nutritions(
        val calories: Float,
        val fat: Float,
        val sugar: Float,
        val carbohydrates: Float,
        val protein: Float
    )
}