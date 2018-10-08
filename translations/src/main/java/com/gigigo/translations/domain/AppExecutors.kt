package com.gigigo.translations.domain

import java.util.concurrent.Executor

interface AppExecutors {

  fun diskIO(): Executor

  fun networkIO(): Executor

  fun mainThread(): Executor

  companion object {
    fun create(): AppExecutors {
      return AppExecutorsImp()
    }
  }
}
