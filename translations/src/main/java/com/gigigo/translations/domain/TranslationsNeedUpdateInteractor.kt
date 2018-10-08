package com.gigigo.translations.domain

import com.gigigo.translations.data.TranslationsDataSource
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class TranslationsNeedUpdateInteractor private constructor(private val appExecutors: AppExecutors,
    private val translationsDataSource: TranslationsDataSource) {

  fun execute(language: String?, indexUrl: String?, localLastModified: String,
      onTranslationsNeedValidation: (languageUrl: String?) -> Unit = {}) {
    appExecutors.networkIO().execute {

      translationsDataSource.getIndex(url = indexUrl, onSuccess = { translationIndex ->
        var languageUrl = translationIndex?.get("default")

        translationIndex?.keys?.takeIf { it.contains(language) }?.apply {
          languageUrl = translationIndex[language]
        }

        translationsDataSource.getLastModified(translationsUrl = languageUrl,
            callback = { serverLastModified ->
              if (localTranslationsAreDeprecated(localLastModified, serverLastModified)) {
                appExecutors.mainThread().execute { onTranslationsNeedValidation(languageUrl) }
              }
            })
      })
    }
  }

  private fun localTranslationsAreDeprecated(localLastModified: String?,
      serverLastModified: String?): Boolean {
    val dateTimeFormat = "EEE, dd MMM YYYY HH:mm:ss ZZZ"

    if (serverLastModified.isNullOrEmpty()) return false
    if (localLastModified.isNullOrEmpty() && !serverLastModified.isNullOrEmpty()) return true

    return try {
      val localTime = SimpleDateFormat(dateTimeFormat, Locale.ENGLISH).parse(localLastModified)
      val serverTime = SimpleDateFormat(dateTimeFormat, Locale.ENGLISH).parse(serverLastModified)

      localTime.before(serverTime)
    } catch (e: ParseException) {
      Timber.e(e)
      true
    } catch (illegalArgument: IllegalArgumentException) {
      Timber.e(illegalArgument)
      try {
        val dateTimeFormatNew = "EEE, dd MMM yyyy HH:mm:ss ZZZ"
        val localTime = SimpleDateFormat(dateTimeFormatNew, Locale.ENGLISH).parse(localLastModified)
        val serverTime = SimpleDateFormat(dateTimeFormatNew, Locale.ENGLISH).parse(
            serverLastModified)

        localTime.before(serverTime)
      } catch (exception: Exception) {
        Timber.e(exception)
        true
      }
    }
  }

  companion object {
    fun create(appExecutors: AppExecutors,
        dataSource: TranslationsDataSource): TranslationsNeedUpdateInteractor {
      return TranslationsNeedUpdateInteractor(appExecutors, dataSource)
    }
  }
}