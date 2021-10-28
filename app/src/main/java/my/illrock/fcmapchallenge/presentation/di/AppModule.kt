package my.illrock.fcmapchallenge.presentation.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import my.illrock.fcmapchallenge.BuildConfig
import my.illrock.fcmapchallenge.data.database.Database
import my.illrock.fcmapchallenge.data.network.ApiService
import my.illrock.fcmapchallenge.data.network.interceptor.FakeInterceptor
import my.illrock.fcmapchallenge.data.network.interceptor.ResponseInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideHttpClient(@ApplicationContext context: Context): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor(ResponseInterceptor())
//        .addInterceptor(FakeInterceptor(context))
        .build()

    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .build()
    }

    @Provides
    fun provideApiService(httpClient: OkHttpClient, moshi: Moshi): ApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): Database =
        Room.databaseBuilder(context, Database::class.java, Database.NAME)
            .build()

    @Provides
    fun providesLastDataDao(database: Database) = database.lastDataDao()

    @Provides
    fun provideCoroutineDispatcher() = Dispatchers.IO

    companion object {
        private const val CONNECT_TIMEOUT = 15L
        private const val READ_TIMEOUT = 70L
    }
}