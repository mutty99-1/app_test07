package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MedicationSlot
import com.example.ui.components.MedicationCheckButton
import com.example.ui.components.WaterIntakeSection
import com.example.ui.theme.EveningAccentBg
import com.example.ui.theme.EveningAccentTint
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.IvoryBackground
import com.example.ui.theme.IvoryBorder
import com.example.ui.theme.IvorySurfaceWarm
import com.example.ui.theme.LunchAccentBg
import com.example.ui.theme.LunchAccentTint
import com.example.ui.theme.MedDoneContainer
import com.example.ui.theme.MorningAccentBg
import com.example.ui.theme.MorningAccentTint

@Composable
fun HealthCheckScreen(
    uiState: HealthCheckUiState,
    onToggleMedication: (MedicationSlot) -> Unit,
    onAddWater: () -> Unit,
    onRemoveWater: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = IvoryBackground,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .align(Alignment.TopCenter)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DateHeaderCard(
                    yearLabel = uiState.yearLabel,
                    monthDayWeekdayLabel = uiState.monthDayWeekdayLabel
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.medication_section_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 23.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkPrimary
                    )

                    val allDone = uiState.completedMedCount == 3
                    Text(
                        text = if (allDone) {
                            stringResource(R.string.med_all_done_message)
                        } else {
                            stringResource(R.string.med_progress_format, uiState.completedMedCount)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (allDone) MedDoneContainer else InkSecondary
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MedicationCheckButton(
                        title = stringResource(R.string.med_morning),
                        isTaken = uiState.morningTaken,
                        slotIcon = Icons.Filled.WbTwilight,
                        slotIconDescription = stringResource(R.string.cd_morning_icon),
                        pendingAccentBg = MorningAccentBg,
                        pendingAccentTint = MorningAccentTint,
                        testTag = "morning_med_button",
                        onToggle = { onToggleMedication(MedicationSlot.MORNING) }
                    )

                    MedicationCheckButton(
                        title = stringResource(R.string.med_lunch),
                        isTaken = uiState.lunchTaken,
                        slotIcon = Icons.Filled.LightMode,
                        slotIconDescription = stringResource(R.string.cd_lunch_icon),
                        pendingAccentBg = LunchAccentBg,
                        pendingAccentTint = LunchAccentTint,
                        testTag = "lunch_med_button",
                        onToggle = { onToggleMedication(MedicationSlot.LUNCH) }
                    )

                    MedicationCheckButton(
                        title = stringResource(R.string.med_evening),
                        isTaken = uiState.eveningTaken,
                        slotIcon = Icons.Filled.NightsStay,
                        slotIconDescription = stringResource(R.string.cd_evening_icon),
                        pendingAccentBg = EveningAccentBg,
                        pendingAccentTint = EveningAccentTint,
                        testTag = "evening_med_button",
                        onToggle = { onToggleMedication(MedicationSlot.EVENING) }
                    )
                }

                WaterIntakeSection(
                    waterCups = uiState.waterCups,
                    waterGoal = uiState.waterGoal,
                    onAddWater = onAddWater,
                    onRemoveWater = onRemoveWater
                )
            }
        }
    }
}

@Composable
private fun DateHeaderCard(
    yearLabel: String,
    monthDayWeekdayLabel: String
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = IvorySurfaceWarm,
        border = BorderStroke(1.5.dp, IvoryBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = null,
                    tint = InkSecondary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "${stringResource(R.string.today_label)} · $yearLabel",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 22.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = InkSecondary
                )
            }

            Text(
                text = monthDayWeekdayLabel,
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = InkPrimary,
                modifier = Modifier.testTag("today_date_text")
            )
        }
    }
}
