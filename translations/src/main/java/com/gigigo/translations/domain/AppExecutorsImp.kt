package com.gigigo.translations.domain

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutorsImp(private val diskIO: Executor, private val networkIO: Executor,
    private val mainThread: Executor) : AppExecutors {

  constructor() : this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3),
      MainThreadExecutor())

  override fun diskIO(): Executor {
    return diskIO
  }

  override fun networkIO(): Executor {
    return networkIO
  }

  override fun mainThread(): Executor {
    return mainThread
  }

  private class MainThreadExecutor : Executor {
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable) {
      mainThreadHandler.post(command)
    }
  }
}
