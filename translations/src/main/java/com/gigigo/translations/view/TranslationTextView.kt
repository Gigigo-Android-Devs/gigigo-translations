package com.gigigo.translations.view

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.AttributeSet
import android.widget.TextView
import com.gigigo.translations.R
import com.gigigo.translations.TranslationsManager

open class TranslationTextView : TextView {

  private val translationsManager = TranslationsManager(context)

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {

    val a = getContext().theme.obtainStyledAttributes(attrs, R.styleable.TranslationTextView, 0, 0)

    try {
      val translationKey = a.getResourceId(R.styleable.TranslationTextView_translation, -1)
      if (translationKey != -1) {
        val text = translationsManager.getTextFromKey(translationKey)
        setText(formatText(text))
      }
    } finally {
      a.recycle()
    }
  }

  fun setTranslation(translationKey: Int) {
    val text = translationsManager.getTextFromKey(translationKey)
    setText(text)
  }

  private fun formatText(text: String): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
    } else {
      Html.fromHtml(text)
    }
  }
}