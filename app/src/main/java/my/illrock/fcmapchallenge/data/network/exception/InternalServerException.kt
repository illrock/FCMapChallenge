package my.illrock.fcmapchallenge.data.network.exception

import okio.IOException

class InternalServerException(val error: String = "") : IOException(error) {
    fun isUnknownApiKeyException() = error.contains(UNKNOWN_API_KEY)

    companion object {
        private const val UNKNOWN_API_KEY = "Unknown API user"
    }
}