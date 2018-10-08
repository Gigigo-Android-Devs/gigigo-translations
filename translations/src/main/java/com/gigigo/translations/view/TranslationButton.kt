package com.gigigo.translations.view

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import com.gigigo.translations.R
import com.gigigo.translations.TranslationsManager

open class TranslationButton : Button {

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
        setText(text)
      }
    } finally {
      a.recycle()
    }
  }

  fun setTranslation(translationKey: Int) {
    val text = translationsManager.getTextFromKey(translationKey)
    setText(text)
  }
}