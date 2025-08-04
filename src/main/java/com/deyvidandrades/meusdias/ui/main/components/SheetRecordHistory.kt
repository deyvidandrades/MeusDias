package com.deyvidandrades.meusdias.ui.main.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.data.helpers.DateHelper
import com.deyvidandrades.meusdias.data.model.Goal
import com.deyvidandrades.meusdias.ui.common.ConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetRecordsHistory(goals: List<Goal>, onDeleteGoal: (Long) -> Unit, onDialogDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = { onDialogDismiss.invoke() }) {
        Column(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)) {

            Text(stringResource(R.string.seus_recordes), fontWeight = FontWeight.Black)

            LazyColumn {
                items(goals) { goal -> ItemGoal(goal, onItemDeleted = { onDeleteGoal.invoke(it) }) }
            }
        }
    }
}

@Composable
private fun ItemGoal(goal: Goal, onItemDeleted: (Long) -> Unit) {
    var showDialogConfirm by remember { mutableStateOf(false) }
    val fontWeight = FontWeight.Black
    Spacer(Modifier.height(12.dp))

    Card(
        Modifier
            .fillMaxWidth()
            .combinedClickable(true, onClick = {}, onLongClick = { showDialogConfirm = true }),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "${goal.currentRecord} ${
                        if (goal.currentRecord > 9) stringResource(R.string.dias) else stringResource(
                            R.string.dia
                        )
                    }",
                    fontSize = 24.sp,
                    fontWeight = fontWeight,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(DateHelper.getFormatedDate(LocalContext.current, goal.createdAt), fontSize = 14.sp)
            }

            Text(goal.title, fontSize = 24.sp, fontWeight = fontWeight)
        }
    }

    if (showDialogConfirm)
        ConfirmationDialog(
            Icons.Outlined.Delete,
            stringResource(R.string.deletar_objetivo),
            stringResource(R.string.tem_certeza_que_deseja_deletar_esse_objetivo),
            onConfirm = {
                onItemDeleted.invoke(goal.createdAt)
                showDialogConfirm = false
            },
            onDismiss = { showDialogConfirm = false }
        )
}