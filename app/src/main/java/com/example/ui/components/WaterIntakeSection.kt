package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.IvorySurface
import com.example.ui.theme.WaterButtonBlue
import com.example.ui.theme.WaterCardBg
import com.example.ui.theme.WaterCardBorder
import com.example.ui.theme.WaterCupOutline
import com.example.ui.theme.WaterFillDeep
import com.example.ui.theme.WaterFillLight
import com.example.ui.theme.WaterGoalGreen
import com.example.ui.theme.WaterTextHighlight

@Composable
fun WaterIntakeSection(
    waterCups: Int,
    waterGoal: Int,
    onAddWater: () -> Unit,
    onRemoveWater: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val isGoalReached = waterCups >= waterGoal
    val goalCupsCount = waterGoal.coerceAtLeast(1)

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = WaterCardBg,
        border = BorderStroke(2.dp, WaterCardBorder),
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.water_section_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 23.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkPrimary
                    )

                    Text(
                        text = if (isGoalReached) {
                            stringResource(R.string.water_goal_done)
                        } else {
                            stringResource(R.string.water_goal_format, waterGoal)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isGoalReached) WaterGoalGreen else InkSecondary
                    )
                }

                Text(
                    text = stringResource(R.string.water_cup_count_format, waterCups),
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 36.sp,
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = WaterTextHighlight,
                    modifier = Modifier.testTag("water_count_text")
                )
            }

            EightWaterCupsGrid(
                filledCups = waterCups,
                totalCups = goalCupsCount,
                gridContentDesc = stringResource(R.string.cd_water_cup),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("water_cup_gauge")
            )

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAddWater()
                },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WaterButtonBlue,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 58.dp)
                    .testTag("water_add_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.WaterDrop,
                    contentDescription = stringResource(R.string.cd_water_drop),
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.water_add_button),
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 23.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (waterCups > 0) {
                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onRemoveWater()
                    },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, WaterCardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = IvorySurface,
                        contentColor = WaterTextHighlight
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("water_undo_button")
                ) {
                    Text(
                        text = stringResource(R.string.water_undo_button),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun EightWaterCupsGrid(
    filledCups: Int,
    totalCups: Int,
    gridContentDesc: String,
    modifier: Modifier = Modifier
) {
    val columnsPerRow = 4
    val rowCount = (totalCups + columnsPerRow - 1) / columnsPerRow

    Column(
        modifier = modifier.semantics {
            contentDescription = "$gridContentDesc (${filledCups.coerceAtMost(totalCups)}/$totalCups)"
        },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (rowIndex in 0 until rowCount) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (colIndex in 0 until columnsPerRow) {
                    val cupIndex = rowIndex * columnsPerRow + colIndex
                    if (cupIndex < totalCups) {
                        SingleWaterCupIcon(
                            isFilled = cupIndex < filledCups,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("water_cup_$cupIndex")
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SingleWaterCupIcon(
    isFilled: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedFill by animateFloatAsState(
        targetValue = if (isFilled) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "single_cup_fill"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokePx = 3.2.dp.toPx()

        val cupWidth = (h * 0.82f).coerceAtMost(w * 0.88f)
        val centerX = w / 2f
        val topLeftX = centerX - cupWidth / 2f
        val topRightX = centerX + cupWidth / 2f
        val inset = cupWidth * 0.14f
        val bottomLeftX = topLeftX + inset
        val bottomRightX = topRightX - inset
        val topY = h * 0.08f
        val bottomY = h * 0.92f
        val cornerRadius = 6.dp.toPx()

        val cupPath = Path().apply {
            moveTo(topLeftX, topY)
            lineTo(topRightX, topY)
            lineTo(bottomRightX, bottomY - cornerRadius)
            quadraticTo(
                bottomRightX,
                bottomY,
                bottomRightX - cornerRadius,
                bottomY
            )
            lineTo(bottomLeftX + cornerRadius, bottomY)
            quadraticTo(
                bottomLeftX,
                bottomY,
                bottomLeftX,
                bottomY - cornerRadius
            )
            close()
        }

        // Empty cup white background
        drawPath(
            path = cupPath,
            color = Color.White
        )

        // Animated water fill clipped inside the individual cup
        if (animatedFill > 0.005f) {
            val usableHeight = (bottomY - topY) * 0.86f
            val waterTopY = bottomY - (usableHeight * animatedFill)

            clipPath(cupPath) {
                val waterPath = Path().apply {
                    moveTo(topLeftX - 4f, waterTopY)
                    quadraticTo(
                        centerX - cupWidth * 0.2f,
                        waterTopY - 4.dp.toPx(),
                        centerX,
                        waterTopY
                    )
                    quadraticTo(
                        centerX + cupWidth * 0.2f,
                        waterTopY + 4.dp.toPx(),
                        topRightX + 4f,
                        waterTopY
                    )
                    lineTo(topRightX + 4f, bottomY + 4f)
                    lineTo(topLeftX - 4f, bottomY + 4f)
                    close()
                }
                drawPath(
                    path = waterPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(WaterFillLight, WaterFillDeep),
                        startY = waterTopY,
                        endY = bottomY
                    )
                )
            }
        }

        // Subtle glass shine highlight
        drawLine(
            color = Color.White.copy(alpha = 0.55f),
            start = Offset(topLeftX + 7.dp.toPx(), topY + 8.dp.toPx()),
            end = Offset(bottomLeftX + 6.dp.toPx(), bottomY - 10.dp.toPx()),
            strokeWidth = 2.2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Cup outline
        drawPath(
            path = cupPath,
            color = WaterCupOutline,
            style = Stroke(
                width = strokePx,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
