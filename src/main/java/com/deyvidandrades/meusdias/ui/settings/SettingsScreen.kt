package com.deyvidandrades.meusdias.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.ui.common.ConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogSettings(settingsViewModel: SettingsViewModel, onDialogDismiss: () -> Unit) {
    val settings by settingsViewModel.flowSettings.collectAsStateWithLifecycle()
    val fontWeight = FontWeight.Black

    val context = LocalContext.current

    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName

    val localUriHandler = LocalUriHandler.current

    var showConfirmDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = { onDialogDismiss.invoke() },
        sheetState = rememberModalBottomSheetState(true)
    ) {
        Column(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)) {

            Text(stringResource(R.string.customizacao), fontWeight = fontWeight)

            SwitchItem(
                stringResource(R.string.tema_escuro),
                settings?.isDarkTheme ?: false,
                onSwitchChecked = { settingsViewModel.updatingDarkTheme(it) }
            )

            SwitchItem(
                stringResource(R.string.notificacoes),
                settings?.showNotifications ?: true,
                onSwitchChecked = { settingsViewModel.updatingShowNotifications(context, it) }
            )

            NumberSwitchItem(
                stringResource(R.string.horario),
                settings?.notificationHour ?: 8,
                onSwitchChanged = {
                    settingsViewModel.updatingNotificationTime(context, it)
                }
            )

            Text(stringResource(R.string.dados), fontWeight = fontWeight)

            ButtonItem(stringResource(R.string.deletar_dados), Icons.AutoMirrored.Rounded.KeyboardArrowRight) {
                showConfirmDialog = true
            }

            Text(stringResource(R.string.sobre), fontWeight = fontWeight)

            ButtonItem(stringResource(R.string.termos_e_condicoes), Icons.AutoMirrored.Rounded.KeyboardArrowRight) {
                localUriHandler.openUri(context.getString(R.string.termos_url))
            }

            Spacer(Modifier.height(12.dp))

            Text("${stringResource(R.string.app_name)} v$versionName", Modifier.align(Alignment.CenterHorizontally))
        }
    }

    if (showConfirmDialog)
        ConfirmationDialog(
            Icons.Outlined.DeleteForever,
            stringResource(R.string.limpar_dados),
            stringResource(R.string.se_voce_confirmar_todos_os_objetivos_serao_apagados),
            onConfirm = {
                settingsViewModel.deleteAllGoals()
                showConfirmDialog = false
            },
            onDismiss = { showConfirmDialog = false }
        )
}

@Composable
fun SwitchItem(title: String, checked: Boolean, onSwitchChecked: (Boolean) -> Unit) {
    var isChecked by remember { mutableStateOf(false) }

    LaunchedEffect(checked) {
        isChecked = checked
    }

    Spacer(Modifier.height(6.dp))

    Card(shape = RoundedCornerShape(24.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title)
            Switch(isChecked, onCheckedChange = {
                isChecked = it
                onSwitchChecked.invoke(it)
            })
        }
    }

    Spacer(Modifier.height(12.dp))
}

@Composable
fun NumberSwitchItem(title: String, number: Int, onSwitchChanged: (Int) -> Unit) {
    var currentNumber by remember { mutableIntStateOf(0) }

    LaunchedEffect(number) {
        currentNumber = number
    }

    Spacer(Modifier.height(6.dp))

    Card(shape = RoundedCornerShape(24.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 6.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title)

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    val newNumber = if (currentNumber - 1 > -1) currentNumber - 1 else 23
                    currentNumber = newNumber
                    onSwitchChanged.invoke(newNumber)
                }) {
                    Icon(
                        Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text("${if (currentNumber > 9) currentNumber else "0$currentNumber"}:00", fontWeight = FontWeight.Black)

                IconButton(onClick = {
                    val newNumber = if (currentNumber + 1 < 24) currentNumber + 1 else 0
                    currentNumber = newNumber
                    onSwitchChanged.invoke(newNumber)
                }) {
                    Icon(
                        Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    Spacer(Modifier.height(12.dp))
}

@Composable
fun ButtonItem(title: String, icon: ImageVector, onClicked: () -> Unit) {

    Spacer(Modifier.height(6.dp))

    Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(true, onClick = { onClicked.invoke() })
                    .padding(16.dp),
            ) {
                Text(title, fontWeight = FontWeight.Black)
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    Spacer(Modifier.height(12.dp))
}