package com.gigigo.translations.data

import timber.log.Timber


class TranslationsDataSourceImp(
    private val apiService: TranslationsApiService) : TranslationsDataSource {

  override fun getLastModified(translationsUrl: String?, callback: (lastModified: String) -> Unit) {
    if (translationsUrl.isNullOrEmpty()) {
      callback("")
    } else {
      apiService.getVersion(translationsUrl!!).execute().apply {
        headers()["Last-Modified"].let {
          if (!it.isNullOrEmpty()) {
            callback(it!!)
          } else {
            callback("")
          }
        }
      }
    }
  }

  override fun getIndex(url: String?, onSuccess: (translationIndex: Map<String, String>?) -> Unit,
      onError: () -> Unit) {

    if (url == null) {
      onError()
    } else {
      try {
        apiService.getMap(url).execute().apply {
          if (isSuccessful) {
            onSuccess(body())
          } else {
            onError()
          }
        }
      } catch (e: Exception) {
        Timber.e(e, "getIndex()")
        onError()
      }
    }
  }

  override fun getTranslations(url: String?,
      onSuccess: (translationIndex: Map<String, String>?, lastModified: String?) -> Unit,
      onError: () -> Unit) {

    if (url == null) {
      onError()
    } else {
      apiService.getMap(url).execute().apply {
        if (isSuccessful) {
          onSuccess(body(), headers()["Last-Modified"])
        } else {
          onError()
        }
      }
    }
  }
}