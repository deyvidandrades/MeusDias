package com.deyvidandrades.meusdias.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.deyvidandrades.meusdias.R

@Composable
fun ConfirmationDialog(icon: ImageVector, title: String, text: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        icon = { Icon(icon, contentDescription = null) },
        title = { Text(title) },
        text = { Text(text) },
        onDismissRequest = { onDismiss.invoke() },
        confirmButton = { TextButton(onClick = { onConfirm.invoke() }) { Text(stringResource(R.string.confirmar)) } },
        dismissButton = { TextButton(onClick = { onDismiss.invoke() }) { Text(stringResource(R.string.cancelar)) } }
    )
}