package com.deyvidandrades.meusdias.ui.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deyvidandrades.meusdias.ui.settings.DialogSettings
import com.deyvidandrades.meusdias.ui.settings.SettingsViewModel

@Composable
fun TopBar(settingsViewModel: SettingsViewModel) {
    var showDialogSettings by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        Icon(
            Icons.AutoMirrored.Rounded.TrendingDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterVertically)
        )

        IconButton(onClick = { showDialogSettings = true }) {
            Icon(
                Icons.Rounded.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
        }
    }

    if (showDialogSettings)
        DialogSettings(settingsViewModel) { showDialogSettings = false }
}