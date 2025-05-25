package com.fit2081.hulongxi33555397.utils

// 扩展函数：将float格式化为指定小数位的字符串
fun Float.format(digits: Int): String = "%.${digits}f".format(this)
