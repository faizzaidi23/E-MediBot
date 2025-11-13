package com.example.emedibotsimpleuserlogin

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val medicines by viewModel.medicines.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    // Get current date
    val currentDate = remember {
        val sdf = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
        sdf.format(Date())
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(1500)
            isRefreshing = false
        }
    }

    Scaffold(
        topBar = {
            EnhancedTopBar(
                currentDate = currentDate,
                medicineCount = medicines.size
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            if (isError) {
                ErrorState(onRetry = {
                    isError = false
                    isRefreshing = true
                })
            } else {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing),
                    onRefresh = { isRefreshing = true }
                ) {
                    if (medicines.isEmpty()) {
                        EmptyState()
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(vertical = 20.dp)
                        ) {
                            // Summary Card
                            item {
                                SummaryCard(
                                    totalMedicines = medicines.size,
                                    completedMedicines = 0 // You can track this with your ViewModel
                                )
                            }

                            // Section Header
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.Schedule,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Scheduled Medications",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            // Medicine Items with animation
                            itemsIndexed(
                                items = medicines,
                                key = { _, medicine -> medicine.name }
                            ) { index, medicine ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            delayMillis = index * 50
                                        )
                                    ) + slideInVertically(
                                        initialOffsetY = { it / 2 },
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            delayMillis = index * 50
                                        )
                                    )
                                ) {
                                    EnhancedMedicineItem(
                                        medicine = medicine,
                                        index = index
                                    )
                                }
                            }

                            // Bottom Spacing
                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedTopBar(currentDate: String, medicineCount: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Today's Schedule",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Medicine Count Badge
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                    shadowElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.MedicalServices,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$medicineCount",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(totalMedicines: Int, completedMedicines: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SummaryItem(
                icon = Icons.Outlined.Assignment,
                label = "Total",
                value = totalMedicines.toString(),
                color = MaterialTheme.colorScheme.primary
            )

            VerticalDivider(
                modifier = Modifier
                    .height(50.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            SummaryItem(
                icon = Icons.Outlined.CheckCircle,
                label = "Completed",
                value = completedMedicines.toString(),
                color = Color(0xFF4CAF50)
            )

            VerticalDivider(
                modifier = Modifier
                    .height(50.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            SummaryItem(
                icon = Icons.Outlined.PendingActions,
                label = "Pending",
                value = (totalMedicines - completedMedicines).toString(),
                color = Color(0xFFFF9800)
            )
        }
    }
}

@Composable
fun SummaryItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun EnhancedMedicineItem(medicine: Medicine, index: Int) {
    var isChecked by remember { mutableStateOf(false) }

    // Determine if the medicine time has passed
    val isPast = remember(medicine.time) {
        // You can implement actual time comparison logic here
        false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isChecked)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time Badge
            Surface(
                color = if (isPast)
                    Color(0xFFFF5252).copy(alpha = 0.1f)
                else
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = if (isPast) Color(0xFFFF5252) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = medicine.time.split(" ")[0], // Time
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isPast) Color(0xFFFF5252) else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = medicine.time.split(" ").getOrNull(1) ?: "", // AM/PM
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isPast) Color(0xFFFF5252) else MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Medicine Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.MedicalServices,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isPast) "Missed" else "Scheduled",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Checkbox
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4CAF50),
                    uncheckedColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Lottie Animation
            LottieAnimation(
                resId = R.raw.empty_calender,
                modifier = Modifier.size(240.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Empty State Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.EventAvailable,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No Medications Today",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "You're all caught up! \nEnjoy your day.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun LottieAnimation(resId: Int, modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}

@Composable
fun ErrorState(onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Oops! Something went wrong",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Failed to load your medicines.\nPlease try again.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp)
                ) {
                    Icon(
                        Icons.Outlined.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Retry",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}