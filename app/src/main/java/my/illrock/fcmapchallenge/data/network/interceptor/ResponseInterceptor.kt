package my.illrock.fcmapchallenge.data.network.interceptor

import my.illrock.fcmapchallenge.BuildConfig
import my.illrock.fcmapchallenge.data.network.exception.InternalServerException
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.json.JSONObject

/**
 * Very handy to test app with local raw json response.
 * Just add this interceptor to the end of your OkHttpClient.Builder() chain
 * */
class ResponseInterceptor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return handleStatus(chain)
    }

    private fun handleStatus(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val apiBaseUrl = BuildConfig.API_BASE_URL.toHttpUrlOrNull()?.host
        val isFleetUrl = request.url.host.equals(apiBaseUrl, true)
        val response = chain.proceed(request)

        return if (isFleetUrl) {
            response.body?.let {
                val json = JSONObject(it.string())
                val contentType = it.contentType()
                return if (json.has(STATUS)) {
                    when (json.getInt(STATUS)) {
                        STATUS_OK -> {
                            val body = json.getString(RESPONSE).toResponseBody(contentType)
                            response.newBuilder().body(body).build()
                        }
                        else -> throw InternalServerException(json.getString(ERROR))
                    }
                } else throw IOException("Unknown response format")
            } ?: response
        } else {
            return response
        }
    }

    companion object {
        private const val STATUS = "status"
        private const val RESPONSE = "response"
        private const val ERROR = "errormessage"

        private const val STATUS_OK = 0
    }
}