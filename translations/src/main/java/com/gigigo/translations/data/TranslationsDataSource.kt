package com.gigigo.translations.data

interface TranslationsDataSource {
  fun getIndex(url: String?, onSuccess: (translationIndex: Map<String, String>?) -> Unit,
      onError: () -> Unit = {})

  fun getTranslations(url: String?,
      onSuccess: (translationIndex: Map<String, String>?, lastModified: String?) -> Unit,
      onError: () -> Unit = {})

  fun getLastModified(translationsUrl: String?, callback: (lastModified: String) -> Unit)


  companion object {
    fun create(apiService: TranslationsApiService): TranslationsDataSource {
      return TranslationsDataSourceImp(apiService)
    }
  }
}