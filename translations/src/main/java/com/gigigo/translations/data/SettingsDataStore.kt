package com.gigigo.translations.data

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.util.Locale

@SuppressLint("ApplySharedPref")
class SettingsDataStore(context: Context) {

  private val sharedPref = context.getSharedPreferences("TranslationsManager", Context.MODE_PRIVATE)
  private var updatedTranslations: Map<String, String>? = emptyMap()
  private val gson: Gson = Gson()

  fun setLanguage(language: String, languageIndexUrl: String) {
    Timber.d("setLanguage($language)")
    with(sharedPref.edit()) {
      putString(LANGUAGE_KEY, language)
      putString(LANGUAGE_INDEX_URL,
          languageIndexUrl)
      commit()
    }
  }

  fun getLanguage(): String {
    return sharedPref.getString(LANGUAGE_KEY, Locale.getDefault().language) ?: ""
  }

  fun getLanguageIndexUrl(): String {
    return sharedPref.getString(LANGUAGE_INDEX_URL, "") ?: ""
  }

  fun setUpdatedTranslations(translations: Map<String, String>?, lastModified: String?) {
    if (translations != null && translations.isNotEmpty()) {
      updatedTranslations = translations

      sharedPref.edit().putString(LAST_MODIFIED, lastModified).commit()
      sharedPref.edit().putString(UPDATED_TRANSLATIONS, gson.toJson(translations)).commit()
    }
  }

  fun getUpdatedTranslations(): Map<String, String>? {
    updatedTranslations?.let {
      if (it.isNotEmpty()) return it
    }

    sharedPref.getString(UPDATED_TRANSLATIONS, "").let {
      return if (!it.isNullOrEmpty()) {
        updatedTranslations = gson.fromJson(it, object : TypeToken<Map<String, String>>() {}.type)
        updatedTranslations
      } else {
        emptyMap()
      }
    }
  }

  fun getLastModified(): String {
    return sharedPref.getString(LAST_MODIFIED, "") ?: ""
  }

  fun clear() {
    sharedPref.edit().clear().commit()
  }

  companion object {
    private const val LANGUAGE_KEY = "language_key"
    private const val LANGUAGE_INDEX_URL = "language_index_url"
    private const val LAST_MODIFIED = "last_modified_translations"
    private const val UPDATED_TRANSLATIONS = "updated_translations"
  }
}

