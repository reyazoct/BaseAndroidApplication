package com.reyaz.provider

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitProvider {

    fun createRetrofitClient(
        url: String,
        interceptor: (Request.Builder) -> Unit,
        timeoutSeconds: Long = 15L,
    ): Retrofit {
        val okHttpClientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()

        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        with(okHttpClientBuilder) {
            connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
            addInterceptor(loggingInterceptor)
            addInterceptor(networkInterceptor(interceptor))
        }

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClientBuilder.build())
            .build()
    }

    private fun networkInterceptor(interceptor: (Request.Builder) -> Unit) = Interceptor { chain ->
        val request = chain.request().newBuilder().apply {
            interceptor(this)
        }.build()
        return@Interceptor chain.proceed(request)
    }
}
