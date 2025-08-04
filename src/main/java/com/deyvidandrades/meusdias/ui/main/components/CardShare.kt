package com.deyvidandrades.meusdias.ui.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.data.model.Goal

@Composable
fun CardShare(currentGoal: Goal) {
    val fontWeight = FontWeight.Black
    Surface(
        Modifier
            .height(350.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Card(
            Modifier.padding(12.dp), RoundedCornerShape(32.dp), colors = CardColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = CardDefaults.cardColors().contentColor,
                disabledContainerColor = CardDefaults.cardColors().disabledContainerColor,
                disabledContentColor = CardDefaults.cardColors().disabledContentColor,
            )
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.estou_a), fontSize = 32.sp, fontWeight = fontWeight)

                        Icon(
                            Icons.AutoMirrored.Rounded.TrendingDown,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                    Text(
                        "${currentGoal.currentStreak} ${if (currentGoal.currentStreak > 9) stringResource(R.string.dias) else stringResource(
                            R.string.dia
                        )}",
                        fontSize = 32.sp,
                        fontWeight = fontWeight,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(currentGoal.title, fontSize = 32.sp, fontWeight = fontWeight)
                }

                Column(Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.meu_recorde_e_de), fontSize = 24.sp, fontWeight = fontWeight)
                    Text(
                        "${currentGoal.currentRecord} ${if (currentGoal.currentRecord > 9) stringResource(R.string.dias) else stringResource(
                            R.string.dia
                        )}.",
                        fontSize = 24.sp,
                        fontWeight = fontWeight,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}