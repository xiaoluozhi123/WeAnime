package com.xiaoluozhi.anime.data.utils

import com.google.gson.ExclusionStrategy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import com.google.gson.Strictness
import com.google.gson.reflect.TypeToken

// Gson 对象
val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

// Gson 扩展函数：将对象转换为 JSON 字符串
fun Any.toJsonString(): String {
    return gson.toJson(this)
}

// Gson 扩展函数：将 JSON 字符串转换为对象
inline fun <reified T : Any> String.fromJson(): T? {
    return try {
        gson.fromJson<T>(this, object : TypeToken<T>() {}.type)
    } catch (e: JsonSyntaxException) {
//        e.printStackTrace()
        null
    }
}

// Gson 扩展函数：将 JSON 字符串转换为对象列表
inline fun <reified T : Any> String.fromJsonList(): List<T>? {
    return try {
        gson.fromJson<List<T>>(this, object : TypeToken<List<T>>() {}.type)
    } catch (e: JsonSyntaxException) {
//        e.printStackTrace()
        null
    }
}

// Gson 扩展函数：将对象转换为指定类型的对象
inline fun <reified T : Any> Any.convertTo(): T? {
    val json = gson.toJson(this)
    return try {
        gson.fromJson(json, object : TypeToken<T>() {}.type)
    } catch (e: JsonSyntaxException) {
//        e.printStackTrace()
        null
    }
}

// Gson 扩展函数：将 JsonElement 对象转换为对象
inline fun <reified T : Any> Gson.fromJson(jsonElement: JsonElement): T? {
    return try {
        fromJson<T>(jsonElement, object : TypeToken<T>() {}.type)
    } catch (e: JsonSyntaxException) {
//        e.printStackTrace()
        null
    }
}

// Gson 扩展函数：将 JsonElement 对象转换为对象列表
inline fun <reified T : Any> Gson.fromJsonList(jsonElement: JsonElement): List<T>? {
    return try {
        fromJson<List<T>>(jsonElement, object : TypeToken<List<T>>() {}.type)
    } catch (e: JsonSyntaxException) {
//        e.printStackTrace()
        null
    }
}

// Gson 扩展函数：将对象转换为 JsonElement 对象
fun <T : Any> T.toJsonElement(): JsonElement {
    return gson.toJsonTree(this)
}

// Gson 扩展函数：将对象转换为格式化的 JSON 字符串（自定义缩进和换行）
fun <T : Any> T.toFormattedJsonString(indent: Int = 2): String {
    val gsonBuilder = GsonBuilder().setPrettyPrinting().apply {
        setStrictness(Strictness.LENIENT)
    }
    val gson = gsonBuilder.create()
    val json = gson.toJson(this)

    val indentedJson = buildString {
        var level = 0
        var inQuote = false

        for (char in json) {
            when (char) {
                '{', '[' -> {
                    append(char)
//                    appendLine()
                    append(" ".repeat((level + 1) * indent))
                    level++
                }

                '}', ']' -> {
//                    appendLine()
                    level--
                    append(" ".repeat(level * indent))
                    append(char)
                }

                ',' -> {
                    append(char)
                    if (!inQuote) {
//                        appendLine()
                        append(" ".repeat(level * indent))
                    }
                }

                '"' -> {
                    append(char)
                    inQuote = !inQuote
                }

                else -> append(char)
            }
        }
    }

    return indentedJson
}


// Gson 扩展函数：将对象转换为 JSON 字节数组
fun <T : Any> T.toJsonBytes(): ByteArray {
    return gson.toJson(this).toByteArray(Charsets.UTF_8)
}

// Gson 扩展函数：将 JSON 字节数组转换为对象
inline fun <reified T : Any> ByteArray.fromJson(): T? {
    return try {
        gson.fromJson<T>(String(this, Charsets.UTF_8), object : TypeToken<T>() {}.type)
    } catch (e: JsonSyntaxException) {
//        e.printStackTrace()
        null
    }
}

// Gson 扩展函数：将 JSON 字节数组转换为对象列表
inline fun <reified T : Any> ByteArray.fromJsonList(): List<T>? {
    return try {
        gson.fromJson<List<T>>(
            String(this, Charsets.UTF_8), object : TypeToken<List<T>>() {}.type
        )
    } catch (e: JsonSyntaxException) {
//        e.printStackTrace()
        null
    }
}

// Gson 扩展函数：将对象转换为 JSON 字符串（包含 null 值）
fun Any.toJsonStringIncludeNulls(): String {
    val gsonBuilder = GsonBuilder().serializeNulls().apply {
        setStrictness(Strictness.LENIENT)
    }
    return gsonBuilder.create().toJson(this)
}

// Gson 扩展函数：将 JSON 字符串转换为对象（包含 null 值）
inline fun <reified T : Any> String.fromJsonIncludeNulls(): T? {
    val gsonBuilder = GsonBuilder().serializeNulls().apply {
        setStrictness(Strictness.LENIENT)
    }
    return try {
        gsonBuilder.create().fromJson<T>(this, object : TypeToken<T>() {}.type)
    } catch (e: JsonSyntaxException) {
//        e.printStackTrace()
        null
    }
}

// Gson 扩展函数：将对象转换为 JSON 字符串（排除指定字段）
fun Any.toJsonStringWithExclusionStrategy(
    strategy: ExclusionStrategy
): String {
    val gsonBuilder = gson.newBuilder().apply {
        setExclusionStrategies(strategy)
        setStrictness(Strictness.LENIENT)
    }
    return gsonBuilder.create().toJson(this)
}

// Gson 扩展函数：将 JSON 字符串转换为对象（排除指定字段）
inline fun <reified T : Any> String.fromJsonWithExclusionStrategy(
    strategy: ExclusionStrategy
): T? {
    val gsonBuilder = gson.newBuilder().apply {
        setExclusionStrategies(strategy)
        setStrictness(Strictness.LENIENT)
    }
    return try {
        gsonBuilder.create().fromJson<T>(this, object : TypeToken<T>() {}.type)
    } catch (e: JsonSyntaxException) {
//        e.printStackTrace()
        null
    }
}