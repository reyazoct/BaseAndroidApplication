package com.reyaz.provider

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitProvider {

    fun createRetrofitClient(url: String, interceptor: (Request.Builder) -> Unit): Retrofit {
        val okHttpClientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()

        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        with(okHttpClientBuilder) {
            connectTimeout(60, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            addInterceptor(loggingInterceptor)
            addInterceptor(networkInterceptor(interceptor))
        }

        val gson = GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder()
            .baseUrl(url)
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
