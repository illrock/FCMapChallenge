package my.illrock.fcmapchallenge.data.preference

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val preferences = context.getSharedPreferences(context.preferencesName, Context.MODE_PRIVATE)
    private val Context.preferencesName get() = packageName.replace('.', '_')

    fun putLong(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long = 0): Long {
        return try {
            preferences.getLong(key, defaultValue)
        } catch (e: ClassCastException) {
            preferences.getInt(key, defaultValue.toInt()).toLong()
        }
    }

    fun putString(key: String, value: String?) {
        preferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return preferences.getString(key, defaultValue) ?: defaultValue
    }
}