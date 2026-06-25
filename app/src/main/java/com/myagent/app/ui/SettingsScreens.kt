package com.myagent.app.ui

import com.myagent.app.AppearanceThemeMode
import com.myagent.app.MainViewModel
import com.myagent.app.model.ModelDownloadState
import com.myagent.app.model.PersonaType
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 设置页面。
 */
@Composable
fun SettingsScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier,
) {
  val currentPersona by viewModel.currentPersona.collectAsState()
  val appearanceMode by viewModel.appearanceThemeMode.collectAsState()
  val downloadState by viewModel.downloadState.collectAsState()
  var showPersonaDialog by remember { mutableStateOf(false) }
  var showAppearanceDialog by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
  ) {
    Text(
      text = "设置",
      style = MaterialTheme.typography.headlineMedium,
      modifier = Modifier.padding(bottom = 16.dp),
    )

    // 人格设置
    SettingsRow(
      icon = Icons.Default.Person,
      title = "AI 人格",
      subtitle = currentPersona.displayName,
      onClick = { showPersonaDialog = true },
    )

    HorizontalDivider()

    // 外观设置
    SettingsRow(
      icon = Icons.Default.Palette,
      title = "外观",
      subtitle = when (appearanceMode) {
        AppearanceThemeMode.System -> "跟随系统"
        AppearanceThemeMode.Light -> "浅色"
        AppearanceThemeMode.Dark -> "深色"
      },
      onClick = { showAppearanceDialog = true },
    )

    HorizontalDivider()

    // 模型下载
    DownloadSection(
      state = downloadState,
      onStartDownload = { viewModel.resetModelDownload() },
    )

    Spacer(modifier = Modifier.height(32.dp))

    Text(
      text = "灵机 v2.0",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }

  // 人格选择弹窗
  if (showPersonaDialog) {
    PersonaDialog(
      currentPersona = currentPersona,
      onSelect = {
        viewModel.setPersona(it)
        showPersonaDialog = false
      },
      onDismiss = { showPersonaDialog = false },
    )
  }

  // 外观选择弹窗
  if (showAppearanceDialog) {
    AppearanceDialog(
      currentMode = appearanceMode,
      onSelect = {
        viewModel.setAppearanceThemeMode(it)
        showAppearanceDialog = false
      },
      onDismiss = { showAppearanceDialog = false },
    )
  }
}

@Composable
private fun SettingsRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(vertical = 16.dp, horizontal = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
    )
    Spacer(modifier = Modifier.width(16.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(text = title, style = MaterialTheme.typography.bodyLarge)
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    Icon(
      imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun PersonaDialog(
  currentPersona: PersonaType,
  onSelect: (PersonaType) -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("选择 AI 人格") },
    text = {
      Column {
        for (persona in PersonaType.entries) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onSelect(persona) }
              .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            RadioButton(
              selected = persona == currentPersona,
              onClick = { onSelect(persona) },
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = persona.displayName)
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) { Text("取消") }
    },
  )
}

@Composable
private fun DownloadSection(
  state: ModelDownloadState,
  onStartDownload: () -> Unit,
) {
  val isDownloading = state is ModelDownloadState.Downloading || state is ModelDownloadState.Verifying
  val isCompleted = state is ModelDownloadState.Completed

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp, horizontal = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Default.Download,
      contentDescription = null,
      tint = if (isCompleted) Color(0xFF4ECDC4) else MaterialTheme.colorScheme.primary,
    )
    Spacer(modifier = Modifier.width(16.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = "AI 模型",
        style = MaterialTheme.typography.bodyLarge,
      )
      Text(
        text = when {
          isCompleted -> "模型已就绪"
          isDownloading -> "正在下载..."
          state is ModelDownloadState.Failed -> "下载失败，点击下载"
          state is ModelDownloadState.Idle -> "未下载，点击下载"
          else -> "未下载，点击下载"
        },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    if (!isDownloading && !isCompleted) {
      Button(
        onClick = onStartDownload,
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF4ECDC4),
        ),
      ) {
        Text("下载")
      }
    }
  }
  HorizontalDivider()
}

@Composable
private fun AppearanceDialog(
  currentMode: AppearanceThemeMode,
  onSelect: (AppearanceThemeMode) -> Unit,
  onDismiss: () -> Unit,
) {
  val modes = listOf(
    AppearanceThemeMode.System to "跟随系统",
    AppearanceThemeMode.Light to "浅色",
    AppearanceThemeMode.Dark to "深色",
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("选择外观") },
    text = {
      Column {
        for ((mode, label) in modes) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onSelect(mode) }
              .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            RadioButton(
              selected = mode == currentMode,
              onClick = { onSelect(mode) },
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label)
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) { Text("取消") }
    },
  )
}