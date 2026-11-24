package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.IvoryBorder
import com.example.ui.theme.IvorySurface
import com.example.ui.theme.MedDoneBadgeBg
import com.example.ui.theme.MedDoneBorder
import com.example.ui.theme.MedDoneContainer
import com.example.ui.theme.MedDoneContent

@Composable
fun MedicationCheckButton(
    title: String,
    isTaken: Boolean,
    slotIcon: ImageVector,
    slotIconDescription: String,
    pendingAccentBg: Color,
    pendingAccentTint: Color,
    testTag: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    val containerColor by animateColorAsState(
        targetValue = if (isTaken) MedDoneContainer else IvorySurface,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "med_card_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isTaken) MedDoneBorder else IvoryBorder,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "med_card_border"
    )
    val titleColor by animateColorAsState(
        targetValue = if (isTaken) MedDoneContent else InkPrimary,
        label = "med_card_title"
    )
    val statusColor by animateColorAsState(
        targetValue = if (isTaken) MedDoneContent else InkSecondary,
        label = "med_card_status"
    )

    val statusText = if (isTaken) {
        stringResource(R.string.med_status_done)
    } else {
        stringResource(R.string.med_status_pending)
    }

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onToggle()
        },
        shape = RoundedCornerShape(22.dp),
        color = containerColor,
        border = BorderStroke(width = if (isTaken) 3.dp else 2.dp, color = borderColor),
        shadowElevation = if (isTaken) 4.dp else 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 68.dp)
            .testTag(testTag)
            .semantics {
                stateDescription = statusText
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            color = if (isTaken) MedDoneBadgeBg else pendingAccentBg,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = slotIcon,
                        contentDescription = slotIconDescription,
                        tint = if (isTaken) MedDoneContent else pendingAccentTint,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 24.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        fontWeight = if (isTaken) FontWeight.Bold else FontWeight.Medium,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (isTaken) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(R.string.cd_completed_check),
                    tint = MedDoneContent,
                    modifier = Modifier.size(38.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = IvoryBorder,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
