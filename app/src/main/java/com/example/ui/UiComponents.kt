package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color constants matching light/dark Moroccan-Islamic guidelines
object MoroccanColors {
    // Emerald variations
    val EmeraldLight = Color(0xFF0F5A47)
    val EmeraldMedium = Color(0xFF0A2A1C)
    val EmeraldDark = Color(0xFF04160E)
    val IvoryWhite = Color(0xFFF9F6F0)
    val SoftGold = Color(0xFFD4AF37)
    val DeepGold = Color(0xFFB8860B)
    val PaleGold = Color(0xFFD4AF37)
    val SubtleGray = Color(0xFFE5E5E5)
    
    // Theme gradients
    val EmeraldGradientLight = Brush.linearGradient(
        colors = listOf(Color(0xFF14745C), Color(0xFF0D4B3B))
    )
    val EmeraldGradientDark = Brush.linearGradient(
        colors = listOf(Color(0xFF0A2A1C), Color(0xFF04160E))
    )
    val GoldGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFE5C158), Color(0xFFB8860B))
    )
}

@Composable
fun MoroccanBackground(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val bgColors = if (isDark) {
        listOf(Color(0xFF04160E), Color(0xFF020E09))
    } else {
        listOf(Color(0xFFF7F4EC), Color(0xFFEFECE2))
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgColors))
            .drawBehind {
                // Draw a beautiful subtle traditional Moroccan repeating trellis pattern on the canvas
                val spacing = 80.dp.toPx()
                val goldColor = MoroccanColors.SoftGold.copy(alpha = if (isDark) 0.04f else 0.08f)
                
                for (x in 0..size.width.toInt() step spacing.toInt()) {
                    for (y in 0..size.height.toInt() step spacing.toInt()) {
                        // Draw thin diamond lines
                        drawLine(
                            color = goldColor,
                            start = Offset(x.toFloat(), y.toFloat() - spacing / 2),
                            end = Offset(x.toFloat() + spacing / 2, y.toFloat()),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = goldColor,
                            start = Offset(x.toFloat() + spacing / 2, y.toFloat()),
                            end = Offset(x.toFloat(), y.toFloat() + spacing / 2),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = goldColor,
                            start = Offset(x.toFloat(), y.toFloat() + spacing / 2),
                            end = Offset(x.toFloat() - spacing / 2, y.toFloat()),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = goldColor,
                            start = Offset(x.toFloat() - spacing / 2, y.toFloat()),
                            end = Offset(x.toFloat(), y.toFloat() - spacing / 2),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
            },
        content = content
    )
}

@Composable
fun MoroccanMoorishArch(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val strokeColor = MoroccanColors.SoftGold.copy(alpha = if (isDark) 0.15f else 0.3f)
    Canvas(modifier = modifier.fillMaxWidth()) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(0f, h)
            lineTo(0f, h * 0.4f)
            // Left shoulder curving inward
            cubicTo(0f, h * 0.2f, w * 0.25f, h * 0.1f, w * 0.5f, 0f)
            // Right shoulder curving outward
            cubicTo(w * 0.75f, h * 0.1f, w, h * 0.2f, w, h * 0.4f)
            lineTo(w, h)
        }
        drawPath(
            path = path,
            color = strokeColor,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
fun MoroccanHeader(
    title: String,
    subtitle: String,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    listOf(MoroccanColors.SoftGold, Color.Transparent)
                ),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF0A2A1C) else Color(0xFF0F5A47)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp)
        ) {
            // Draw visual arches on the background of header card
            Canvas(modifier = Modifier.matchParentSize()) {
                val archPath = Path().apply {
                    moveTo(size.width * 0.1f, size.height)
                    lineTo(size.width * 0.1f, size.height * 0.3f)
                    cubicTo(size.width * 0.1f, size.height * 0.1f, size.width * 0.3f, 0f, size.width * 0.5f, 0f)
                    cubicTo(size.width * 0.7f, 0f, size.width * 0.9f, size.height * 0.1f, size.width * 0.9f, size.height * 0.3f)
                    lineTo(size.width * 0.9f, size.height)
                }
                drawPath(archPath, MoroccanColors.SoftGold.copy(alpha = 0.12f), style = Stroke(width = 1.5.dp.toPx()))
            }
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MoroccanColors.PaleGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        letterSpacing = 0.5.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 1.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MoroccanUiCard(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    gilded: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val baseModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }
    
    val containerBg = if (isDark) Color(0xFF0A2A1C).copy(alpha = 0.5f) else Color(0xFFFFFFFF)
    val shadowColor = if (isDark) Color.Black.copy(alpha = 0.4f) else Color(0xFFDCD8CF)
    val borderColor = if (isDark) {
        if (gilded) MoroccanColors.SoftGold.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f)
    } else {
        if (gilded) MoroccanColors.SoftGold.copy(alpha = 0.7f) else MoroccanColors.SubtleGray
    }
    
    Column(
        modifier = baseModifier
            .drawBehind {
                drawRect(
                    color = shadowColor,
                    topLeft = Offset(0f, 4.dp.toPx()),
                    size = Size(size.width, size.height)
                )
            }
            .background(containerBg, RoundedCornerShape(16.dp))
            .border(
                width = if (gilded) 1.dp else 0.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content
    )
}

@Composable
fun FavoriteToggleButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier.testTag("favorite_toggle_btn")
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Add to Favorites",
            tint = if (isFavorite) MoroccanColors.SoftGold else Color.Gray.copy(alpha = 0.5f),
            modifier = Modifier.size(28.dp)
        )
    }
}
