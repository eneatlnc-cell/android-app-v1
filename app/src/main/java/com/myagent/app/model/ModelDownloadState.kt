package com.myagent.app.model

/**
 * 模型下载进度状态。
 */
sealed class ModelDownloadState {
  /** 空闲，尚未开始 */
  data object Idle : ModelDownloadState()

  /** 下载中 */
  data class Downloading(
    val progress: Int,          // 0–100
    val downloadedBytes: Long,
    val totalBytes: Long,
    val speedBytesPerSec: Long, // 当前速度
  ) : ModelDownloadState()

  /** 校验中 */
  data object Verifying : ModelDownloadState()

  /** 完成 */
  data object Completed : ModelDownloadState()

  /** 失败 */
  data class Failed(val error: String) : ModelDownloadState()
}

/** 方便的格式化辅助方法 */
fun ModelDownloadState.Downloading.speedText(): String {
  val kb = speedBytesPerSec / 1024.0
  return if (kb >= 1024) "${"%.1f".format(kb / 1024.0)} MB/s" else "${"%.0f".format(kb)} KB/s"
}

fun ModelDownloadState.Downloading.downloadedText(): String {
  val mb = downloadedBytes / (1024.0 * 1024.0)
  val totalMb = totalBytes / (1024.0 * 1024.0)
  return "${"%.1f".format(mb)} MB / ${"%.1f".format(totalMb)} MB"
}