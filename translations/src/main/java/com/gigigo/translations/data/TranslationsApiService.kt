package com.gigigo.translations.data

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Url

interface TranslationsApiService {

  @GET
  fun getMap(@Url url: String): Call<Map<String, String>>

  @HEAD
  fun getVersion(@Url url: String): Call<Void>

  companion object {
    fun create(url: String): TranslationsApiService {
      return Retrofit.Builder().baseUrl(url)
          .addConverterFactory(JacksonConverterFactory.create())
          .build()
          .create(TranslationsApiService::class.java)
    }
  }
}