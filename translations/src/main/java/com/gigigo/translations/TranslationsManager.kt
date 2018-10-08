package com.gigigo.translations

import android.content.Context
import android.support.annotation.StringRes
import com.gigigo.translations.data.SettingsDataStore
import com.gigigo.translations.data.TranslationsApiService
import com.gigigo.translations.data.TranslationsDataSource
import com.gigigo.translations.domain.AppExecutors
import com.gigigo.translations.domain.GetTranslationsForLanguageInteractor
import com.gigigo.translations.domain.TranslationsNeedUpdateInteractor
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import kotlin.text.Charsets.UTF_8

class TranslationsManager(private val context: Context) {

  private var stringJson: String? = null
  private var defaultStringJson: String? = null
  private val settingsDataStore = SettingsDataStore(context)

  private var getTranslationsForLanguageInteractor: GetTranslationsForLanguageInteractor
  private var translationsNeedUpdateInteractor: TranslationsNeedUpdateInteractor

  init {

    val translationsApiService = TranslationsApiService.create("https://google.com/")
    val dataSource = TranslationsDataSource.create(translationsApiService)
    val appExecutors = AppExecutors.create()

    getTranslationsForLanguageInteractor = GetTranslationsForLanguageInteractor.create(appExecutors,
        dataSource)
    translationsNeedUpdateInteractor = TranslationsNeedUpdateInteractor.create(appExecutors,
        dataSource)
  }

  fun getTextFromKey(@StringRes key: Int): String {
    return getTextFromKey(context.getString(key))
  }

  private fun getTextFromKey(key: String): String {
    var translation = settingsDataStore.getUpdatedTranslations()?.get(key)


    return if (translation.isNullOrEmpty()) {
      translation = getLocalTranslationForKey(key)
      Timber.d("LOCAL -> translation: %s", translation)
      translation
    } else {
      Timber.d("NETWORK -> translation: %s", translation)
      translation!!
    }
  }

  fun removedUpdatedLanguage() {
    settingsDataStore.clear()
  }

  fun setLanguage(language: String?, indexUrl: String?) {
    if (language != null && indexUrl != null) {
      settingsDataStore.setLanguage(language, indexUrl)

      doTranslationsNeedUpdate(language = language, indexUrl = indexUrl,
          needUpdateCallback = { languageUrl ->
            getTranslationsForLanguageInteractor.execute(languageUrl = languageUrl,
                onSuccess = { translations: Map<String, String>?, lastModified: String? ->
                  settingsDataStore.setUpdatedTranslations(translations, lastModified)
                },
                onError = {
                  Timber.e(
                      "Couldn't retrieve translations for language $language and URL $indexUrl")
                })
          })
    } else {
      Timber.e(
          "Couldn't set current language")
    }
  }

  private fun doTranslationsNeedUpdate(language: String?, indexUrl: String?,
      needUpdateCallback: (languageUrl: String?) -> Unit) =

      translationsNeedUpdateInteractor.execute(language, indexUrl,
          settingsDataStore.getLastModified()) { languageUrl ->
        needUpdateCallback(languageUrl)
      }

  private fun loadJSONFromAsset(): String {
    return if (stringJson != null) {
      stringJson as String
    } else {
      try {
        val file = context.assets.open(
            "translations/${settingsDataStore.getLanguage()}.json")
        val size = file.available()
        val buffer = ByteArray(size)
        file.read(buffer)
        file.close()
        String(buffer, UTF_8)
      } catch (ex: IOException) {
        Timber.e("Language: ${settingsDataStore.getLanguage()}")
        "{}"
      }
    }
  }

  private fun getLocalTranslationForKey(key: String): String {
    return try {
      val obj = JSONObject(loadJSONFromAsset())
      if (obj.has(key)) {
        obj.getString(key)
      } else {
        val defaultObj = JSONObject(loadDefaultJSONFromAsset())
        defaultObj.getString(key)
      }
    } catch (ex: JSONException) {
      Timber.e(ex)
      if (BuildConfig.DEBUG) {
        "ERROR! key: $key"
      } else {
        ""
      }
    }
  }

  private fun loadDefaultJSONFromAsset(): String {
    return if (defaultStringJson != null) {
      defaultStringJson as String
    } else {
      try {
        val file = context.assets.open("translations/$LANGUAGE_DEFAULT.json")
        val size = file.available()
        val buffer = ByteArray(size)
        file.read(buffer)
        file.close()
        String(buffer, UTF_8)
      } catch (ex: IOException) {
        Timber.e(ex, "Default")
        "{}"
      }
    }
  }

  companion object {
    private const val LANGUAGE_DEFAULT = "en"
  }
}
