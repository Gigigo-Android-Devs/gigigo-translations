package com.gigigo.translations.sample

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.gigigo.translations.TranslationsManager
import com.gigigo.translations.sample.R.id
import com.gigigo.translations.sample.R.layout
import kotlinx.android.synthetic.main.activity_main.fab
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.content_main.translationButton
import timber.log.Timber
import java.util.Locale

class MainActivity : AppCompatActivity() {

  private lateinit var translationsManager: TranslationsManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_main)
    setSupportActionBar(toolbar)

    fab.setOnClickListener { view ->
      Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
          .setAction("Action", null).show()
    }

    initTranslationsManager()
    initView()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      id.action_clear_data -> {
        translationsManager.removedUpdatedLanguage()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun initView() {
    translationButton.text = translationsManager.getTextFromKey(R.string.login_country)
  }

  private fun initTranslationsManager() {
    translationsManager = TranslationsManager(this)

    val language = Locale.getDefault().language
    Timber.d("displayLanguage: %s", language)

    translationsManager.setLanguage(language,
        "http://translations-q.woah.com/translations/app/index.json")
  }
}
