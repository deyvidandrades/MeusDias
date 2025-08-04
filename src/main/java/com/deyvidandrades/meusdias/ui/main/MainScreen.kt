package com.deyvidandrades.meusdias.ui.main

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.data.model.Goal
import com.deyvidandrades.meusdias.ui.main.components.FabShare
import com.deyvidandrades.meusdias.ui.main.components.SheetRecordsHistory
import com.deyvidandrades.meusdias.ui.main.components.TopBar
import com.deyvidandrades.meusdias.ui.settings.SettingsViewModel
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel, settingsViewModel: SettingsViewModel) {
    val goals by mainViewModel.flowGoals.collectAsStateWithLifecycle()
    val currentGoal by mainViewModel.flowCurrentGoal.collectAsStateWithLifecycle()

    Box(
        Modifier
            .padding(horizontal = 24.dp, vertical = 36.dp)
            .fillMaxSize()
    ) {
        Column {
            TopBar(settingsViewModel)

            Box(Modifier.fillMaxSize()) {
                //Check if theres a new record and pops konfetti in case there is.
                ShowKonfetti(goals.lastOrNull(), onNewRecord = { mainViewModel.updateRecord() })

                Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {

                    ContainerConteudo(currentGoal, onFraseChanged = { mainViewModel.createNewGoal(it) })

                    Column {
                        ContainerRecorde(currentGoal?.currentRecord ?: 0)

                        ContainerShowGoalsHistory(goals, onDeleteGoal = { mainViewModel.deleteGoal(it) })
                    }
                }
            }
        }

        FabShare(mainViewModel, Modifier
            .align(Alignment.BottomEnd)
            .size(64.dp))
    }
}

@Composable
fun ContainerConteudo(goal: Goal?, onFraseChanged: (String) -> Unit) {
    val stringSem = stringResource(R.string.sem)

    var text by remember { mutableStateOf(stringSem) }
    var isEditing by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val fontWeight = FontWeight.Black

    LaunchedEffect(goal?.title) {
        text = goal?.title ?: stringSem
    }

    Column {
        Text(stringResource(R.string.estou_a), fontSize = 60.sp, fontWeight = fontWeight)

        Text(
            "${goal?.currentStreak ?: 0} ${stringResource(if ((goal?.currentStreak ?: 0) > 9) R.string.dias else R.string.dia)}",
            fontSize = 64.sp,
            fontWeight = fontWeight,
            color = MaterialTheme.colorScheme.primary
        )

        BasicTextField(
            text,
            enabled = isEditing,
            onValueChange = { text = it },
            textStyle = TextStyle(
                fontSize = 60.sp,
                fontWeight = fontWeight,
                color = MaterialTheme.colorScheme.onBackground
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    isEditing = false
                    onFraseChanged.invoke(text)
                }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            modifier = Modifier.combinedClickable(onClick = {}, onLongClick = { isEditing = true })
        )

        Row {
            Icon(
                Icons.Outlined.Info,
                contentDescription = null,
                modifier = Modifier
                    .size(14.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                stringResource(R.string.clique_e_segure_na_frase_para_editar),
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 2.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContainerRecorde(value: Int) {
    val fontWeight = FontWeight.Black

    Column {
        Text(stringResource(R.string.meu_recorde_e_de), fontSize = 34.sp, fontWeight = fontWeight)

        Text(
            "$value ${stringResource(if (value > 1) R.string.dias else R.string.dia)}",
            fontSize = 44.sp,
            fontWeight = fontWeight,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ContainerShowGoalsHistory(goals: List<Goal>, onDeleteGoal: (Long) -> Unit) {
    var showBottomSheet by remember { mutableStateOf(false) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        IconButton(onClick = { showBottomSheet = true }) {
            Icon(
                Icons.Rounded.KeyboardArrowUp,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
            )
        }
    }

    if (showBottomSheet) SheetRecordsHistory(
        goals,
        onDeleteGoal = { onDeleteGoal(it) },
        onDialogDismiss = { showBottomSheet = false }
    )
}

@Composable
fun ShowKonfetti(goal: Goal?, onNewRecord: () -> Unit) {
    goal?.let {
        if (it.currentStreak > it.currentRecord) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(
                    Party(
                        speed = 0f,
                        maxSpeed = 30f,
                        damping = .9f,
                        spread = 270,
                        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                        emitter = Emitter(duration = 500, TimeUnit.MILLISECONDS).perSecond(30),
                        position = Position.Relative(.3, .8)
                    )
                ),
            )
            onNewRecord.invoke()
        }
    }
}

