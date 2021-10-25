package my.illrock.fcmapchallenge.data.repository

import my.illrock.fcmapchallenge.data.preference.PreferencesManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyRepository @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    fun get() = preferencesManager.getString(PREF_API_KEY_VALUE, "")

    fun set(apiKey: String) = preferencesManager.putString(PREF_API_KEY_VALUE, apiKey)

    companion object {
        private const val PREF_API_KEY_VALUE = "api_key_value"
    }
}