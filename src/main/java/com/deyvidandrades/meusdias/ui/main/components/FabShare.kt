package com.deyvidandrades.meusdias.ui.main.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.data.model.Goal
import com.deyvidandrades.meusdias.ui.main.MainViewModel
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FabShare(mainViewModel: MainViewModel, modifier: Modifier) {
    val currentGoal by mainViewModel.flowCurrentGoal.collectAsStateWithLifecycle()
    var isShareClicked by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val captureController = rememberCaptureController()

    FloatingActionButton(
        onClick = { isShareClicked = true },
        modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Icon(Icons.Rounded.Share, contentDescription = null)
    }

    if (isShareClicked) {
        Box(modifier = Modifier.alpha(0f)) {
            Box(Modifier.capturable(captureController)) {
                CardShare(currentGoal ?: Goal(stringResource(R.string.sem), 0, 0))
            }
        }
        mainViewModel.shareCurrentGoal(context, captureController, onShared = { isShareClicked = false })
    }
}