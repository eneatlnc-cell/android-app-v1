package com.myagent.app.model

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * llama.cpp JNI 桥接引擎 — 封装了模型加载、推理、卸载的完整生命周期。
 *
 * 线程安全：所有 native 方法必须在同一线程上调用（推理线程）。
 */
class LlamaEngine {
  companion object {
    private const val TAG = "LlamaEngine"
  }

  @Volatile private var ready = false

  // ── Native methods ──

  private external fun nativeInit(nativeLibDir: String)
  private external fun nativeLoadModel(modelPath: String): Int
  private external fun nativePrepare(): Int
  private external fun nativeProcessPrompt(prompt: String): Int
  private external fun nativeGenerateNextToken(): String?
  private external fun nativeUnload()
  private external fun nativeShutdown()

  init {
    System.loadLibrary("lingji-llama")
  }

  // ── Public API ──

  fun init(nativeLibDir: String) {
    nativeInit(nativeLibDir)
    Log.i(TAG, "Backend initialized")
  }

  /**
   * 加载模型文件。返回 0 表示成功。
   */
  fun loadModel(modelPath: String): Boolean {
    val ret = nativeLoadModel(modelPath)
    if (ret != 0) {
      Log.e(TAG, "Failed to load model: $modelPath")
      return false
    }
    Log.i(TAG, "Model loaded: $modelPath")
    return true
  }

  /**
   * 准备推理上下文，必须在 loadModel 之后调用。
   */
  fun prepare(): Boolean {
    val ret = nativePrepare()
    if (ret != 0) {
      Log.e(TAG, "Failed to prepare context")
      return false
    }
    ready = true
    Log.i(TAG, "Context prepared")
    return true
  }

  /**
   * 执行完整的流式推理。
   *
   * 流程：
   * 1. Process prompt (tokenize + decode)
   * 2. 逐 token 生成并发射
   * 3. 自动检测 EOG 停止
   */
  fun generate(prompt: String, maxTokens: Int = 512): Flow<String> = flow {
    if (!ready) {
      Log.e(TAG, "Engine not ready")
      return@flow
    }

    // 1. Process prompt
    val ret = nativeProcessPrompt(prompt)
    if (ret != 0) {
      Log.e(TAG, "Process prompt failed: $ret")
      return@flow
    }

    // 2. Generate tokens
    var count = 0
    while (count < maxTokens) {
      val token = nativeGenerateNextToken()
      if (token == null) break // EOG or stop
      if (token.isNotEmpty()) {
        emit(token)
      }
      count++
    }

    Log.d(TAG, "Generated $count tokens")
  }

  fun unload() {
    ready = false
    nativeUnload()
    Log.i(TAG, "Model unloaded")
  }

  fun shutdown() {
    ready = false
    nativeShutdown()
    Log.i(TAG, "Backend shut down")
  }
}