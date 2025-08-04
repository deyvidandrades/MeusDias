package com.deyvidandrades.meusdias

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deyvidandrades.meusdias.data.helpers.NotificationsHelper
import com.deyvidandrades.meusdias.data.helpers.WorkManagerHelper
import com.deyvidandrades.meusdias.data.repository.GoalsRepositoryImpl
import com.deyvidandrades.meusdias.data.repository.NotificationsRepositoryImpl
import com.deyvidandrades.meusdias.data.repository.SettingsRepositoryImpl
import com.deyvidandrades.meusdias.data.source.PreferencesDataStore
import com.deyvidandrades.meusdias.ui.main.MainScreen
import com.deyvidandrades.meusdias.ui.main.MainViewModel
import com.deyvidandrades.meusdias.ui.settings.SettingsViewModel
import com.deyvidandrades.meusdias.ui.theme.MeusDiasTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferencesDataStore = PreferencesDataStore(dataStore)
        val goalsRepository = GoalsRepositoryImpl(preferencesDataStore)
        val settingsRepository = SettingsRepositoryImpl(preferencesDataStore)
        val notificationRepository = NotificationsRepositoryImpl(preferencesDataStore)

        val mainViewModel = MainViewModel(goalsRepository)
        val settingsViewModel = SettingsViewModel(settingsRepository, goalsRepository)

        setContent {
            var hasNotificationPermission by remember { mutableStateOf(true) }
            val settings by settingsViewModel.flowSettings.collectAsStateWithLifecycle()

            ApplyStatusBarStyle(settings?.isDarkTheme ?: false)

            LaunchedEffect(hasNotificationPermission) {
                if (hasNotificationPermission) {
                    NotificationsHelper.criarCanalDeNotificacoes(applicationContext)
                    WorkManagerHelper.initWorker(applicationContext, notificationRepository.getNotificationTime())
                } else {
                    WorkManagerHelper.stopWorker(applicationContext)
                }
            }

            MeusDiasTheme(darkTheme = settings?.isDarkTheme ?: false) {
                Surface {
                    NotificationPermissionRequester(onPermissionRequested = { hasNotificationPermission = it })

                    MainScreen(mainViewModel, settingsViewModel)
                }
            }
        }
    }

    @Composable
    fun ApplyStatusBarStyle(isDarkTheme: Boolean) {
        val view = LocalView.current
        val activity = LocalActivity.current as Activity

        SideEffect {
            val window = activity.window
            WindowCompat.setDecorFitsSystemWindows(window, false)

            val insetsController = WindowInsetsControllerCompat(window, view)
            insetsController.isAppearanceLightStatusBars = !isDarkTheme
            insetsController.isAppearanceLightNavigationBars = !isDarkTheme
        }
    }

    @Composable
    fun NotificationPermissionRequester(onPermissionRequested: (Boolean) -> Unit) {
        val context = LocalContext.current
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted -> onPermissionRequested.invoke(isGranted) }

        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
