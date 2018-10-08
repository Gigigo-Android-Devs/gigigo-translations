package com.gigigo.translations.domain

import com.gigigo.translations.data.TranslationsDataSource

class GetTranslationsForLanguageInteractor private constructor(
    private val appExecutors: AppExecutors,
    private val dataSource: TranslationsDataSource) {

  fun execute(languageUrl: String?,
      onSuccess: (translations: Map<String, String>?,
          lastModified: String?) -> Unit = { _: Map<String, String>?, _: String? -> },
      onError: () -> Unit = {}) {

    appExecutors.networkIO().execute {
      dataSource.getTranslations(languageUrl,
          onSuccess = { map: Map<String, String>?, lastModified: String? ->
            appExecutors.mainThread().execute { onSuccess(map, lastModified) }
          },
          onError = {
            appExecutors.mainThread().execute { onError() }
          })
    }
  }

  companion object {
    fun create(appExecutors: AppExecutors,
        dataSource: TranslationsDataSource): GetTranslationsForLanguageInteractor {
      return GetTranslationsForLanguageInteractor(
          appExecutors, dataSource)
    }
  }
}