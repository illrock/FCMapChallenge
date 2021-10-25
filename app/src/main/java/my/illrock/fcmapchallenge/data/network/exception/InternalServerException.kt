package my.illrock.fcmapchallenge.data.network.exception

import okio.IOException

class InternalServerException(val error: String = "") : IOException(error) {
    fun isUnknownApiException() = error.contains(UNKNOWN_API)

    companion object {
        private const val UNKNOWN_API = "Unknown API user"
    }
}