package my.illrock.fcmapchallenge.util

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import my.illrock.fcmapchallenge.BuildConfig

fun Throwable.print() = if (BuildConfig.DEBUG) printStackTrace() else Unit

inline fun <reified T> Moshi.fromJson(json: String): T? = try {
    adapter(T::class.java).fromJson(json)
} catch (e: Exception) {
    e.print()
    null
}

inline fun <reified T> Moshi.toJson(item: T): String = adapter(T::class.java).toJson(item)

inline fun <reified T> Moshi.fromJsonArray(json: String): List<T> {
    val type = Types.newParameterizedType(List::class.java, T::class.java)
    return adapter<List<T>>(type).fromJson(json) ?: listOf()
}

inline fun <reified T> Moshi.toJsonArray(items: List<T>): String {
    val type = Types.newParameterizedType(List::class.java, T::class.java)
    return adapter<List<T>>(type).toJson(items)
}