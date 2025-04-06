package com.fit2081.hulongxi33555397

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.BufferedReader
import java.io.InputStreamReader

data class UserData(
    val userId: String,
    val phoneNumber: String,
    val sex: String,
    val heifaTotalScoreMale: Float,
    val heifaTotalScoreFemale: Float
)

@Composable
fun loadUserDataFromCsv(userId: String): UserData? {
    val context = LocalContext.current
    try {
        context.assets.open("users.csv").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.readLines().drop(1).forEach { line ->
                    val columns = line.split(",")
                    if (columns[1] == userId) {
                        return UserData(
                            userId = columns[1],
                            phoneNumber = columns[0],
                            sex = columns[2],
                            heifaTotalScoreMale = columns[3].toFloat(),
                            heifaTotalScoreFemale = columns[4].toFloat()
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