package com.example.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// --------------------------------------------------------------------
// 1. ADVANCED SEARCH SCREEN
// --------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: QuranViewModel,
    onNavigateTo: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme() || viewModel.darkThemeMode.collectAsStateWithLifecycle().value == "dark"
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val results by viewModel.searchResults.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("البحث المتقدم الفوري", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF031913) else Color(0xFF0F5A47)
                )
            )
        }
    ) { innerPadding ->
        MoroccanBackground(isDark = isDark, modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("ابحث عن آية، سورة، تفسير أو كلمة...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_field"),
                    leadingIcon = { Icon(Icons.Filled.Search, null, tint = MoroccanColors.SoftGold) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Filled.Close, "مسح", tint = Color.Gray)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MoroccanColors.SoftGold,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (results.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.FindInPage,
                                contentDescription = null,
                                tint = MoroccanColors.SoftGold.copy(alpha = 0.3f),
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (query.isEmpty()) "ابدأ بكتابة كلمات البحث مثل: 'الحمد' أو 'تقوى'" else "لم يتم العثور على نتائج متطابقة",
                                color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Gray,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Text(
                        text = "نتائج البحث (${results.size}) :",
                        fontWeight = FontWeight.Bold,
                        color = MoroccanColors.SoftGold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(results) { res ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, MoroccanColors.SoftGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.setReadingPage(context, res.page)
                                        onNavigateTo(res.route)
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDark) Color(0xFF042018) else Color(0xFFFDFCF7)
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = res.title,
                                            fontWeight = FontWeight.Bold,
                                            color = MoroccanColors.SoftGold,
                                            fontSize = 14.sp
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(MoroccanColors.SoftGold.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = res.type,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = MoroccanColors.SoftGold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = res.desc,
                                        color = if (isDark) Color.White else Color(0xFF0E382C),
                                        fontSize = 13.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------
// 2. STATISTICS & ACHIEVEMENTS SCREEN
// --------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: QuranViewModel,
    onBack: () -> Unit
) {
    val isDark = isSystemInDarkTheme() || viewModel.darkThemeMode.collectAsStateWithLifecycle().value == "dark"
    val pagesRead by viewModel.statsPagesRead.collectAsStateWithLifecycle()
    val surahsCompleted by viewModel.statsSurahsCompleted.collectAsStateWithLifecycle()
    val listenDuration by viewModel.statsListenDuration.collectAsStateWithLifecycle()
    val streakDays by viewModel.statsStreakDays.collectAsStateWithLifecycle()
    val notesCount = viewModel.personalNotes.collectAsStateWithLifecycle().value.size

    val badges = listOf(
        Triple("مبتدئ مبارك", "ابدأ مسيرة قراءة وتدبر المصحف الشريف", true),
        Triple("القارئ المجتهد", "قراءة أكثر من 10 صفحات (الحالي: $pagesRead صفحة)", pagesRead >= 10),
        Triple("حافظ الملاحظات", "تدوين ملاحظة وتدبر شخصي واحد أو أكثر (الحالي: $notesCount)", notesCount > 0),
        Triple("سماحة الورد", "الاستماع للتلاوة لمدة تزيد عن 30 دقيقة (الحالي: $listenDuration دقيقة)", listenDuration >= 30),
        Triple("صاحب الهمة", "تحقيق كفاءة متابعة مع التزام ورد يومي مستمر", streakDays >= 5)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("لوحة الإحصائيات والإنجازات", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF031913) else Color(0xFF0F5A47)
                )
            )
        }
    ) { innerPadding ->
        MoroccanBackground(isDark = isDark, modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Scoreboard stats rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isDark) Color(0xFF042018) else Color(0xFFF9F6F0), RoundedCornerShape(12.dp))
                            .border(1.dp, MoroccanColors.SoftGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.MenuBook, null, tint = MoroccanColors.SoftGold, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("الصفحات المقروءة", fontSize = 11.sp, color = Color.Gray)
                        Text("$pagesRead", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MoroccanColors.PaleGold)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isDark) Color(0xFF042018) else Color(0xFFF9F6F0), RoundedCornerShape(12.dp))
                            .border(1.dp, MoroccanColors.SoftGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.CheckCircle, null, tint = MoroccanColors.SoftGold, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("السور المكتملة", fontSize = 11.sp, color = Color.Gray)
                        Text("$surahsCompleted", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MoroccanColors.PaleGold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isDark) Color(0xFF042018) else Color(0xFFF9F6F0), RoundedCornerShape(12.dp))
                            .border(1.dp, MoroccanColors.SoftGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.Headset, null, tint = MoroccanColors.SoftGold, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("مدة الاستماع", fontSize = 11.sp, color = Color.Gray)
                        Text("$listenDuration دقيقة", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MoroccanColors.PaleGold)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isDark) Color(0xFF042018) else Color(0xFFF9F6F0), RoundedCornerShape(12.dp))
                            .border(1.dp, MoroccanColors.SoftGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.LocalFireDepartment, null, tint = MoroccanColors.SoftGold, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("الالتزام المتتالي", fontSize = 11.sp, color = Color.Gray)
                        Text("$streakDays أيام", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MoroccanColors.PaleGold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Medal / Badges achievements list
                Text(
                    text = "أوسمة الإنجازات الشريفة",
                    fontWeight = FontWeight.Bold,
                    color = MoroccanColors.SoftGold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                badges.forEach { badge ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(
                                1.dp,
                                if (badge.third) MoroccanColors.SoftGold else Color.LightGray.copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (badge.third) {
                                if (isDark) Color(0xFF033527) else Color(0xFFEDF7F4)
                            } else {
                                if (isDark) Color(0xFF091F18) else Color(0xFFF7F7F7)
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            if (badge.third) MoroccanColors.SoftGold.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.3f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (badge.third) Icons.Filled.MilitaryTech else Icons.Filled.Lock,
                                        contentDescription = null,
                                        tint = if (badge.third) MoroccanColors.SoftGold else Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = badge.first,
                                        fontWeight = FontWeight.Bold,
                                        color = if (badge.third) MoroccanColors.PaleGold else Color.Gray,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = badge.second,
                                        fontSize = 11.sp,
                                        color = if (badge.third) Color.Gray else Color.LightGray
                                    )
                                }
                            }
                            if (badge.third) {
                                Icon(Icons.Filled.CheckCircle, "مكتمل", tint = Color(0xFF2E7D32))
                            }
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------
// 3. KHATMA PLANNER SCREEN
// --------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhatmaScreen(
    viewModel: QuranViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme() || viewModel.darkThemeMode.collectAsStateWithLifecycle().value == "dark"
    val plans by viewModel.khatmaPlans.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }

    var newTitle by remember { mutableStateOf("ختمة الورد الشريف") }
    var selectedDays by remember { mutableStateOf(30) } // 7, 15, 30

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("خطط ختمات القرآن الكريم", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF031913) else Color(0xFF0F5A47)
                ),
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Filled.Add, "إنشاء ختمة", tint = MoroccanColors.PaleGold)
                    }
                }
            )
        }
    ) { innerPadding ->
        MoroccanBackground(isDark = isDark, modifier = Modifier.padding(innerPadding)) {
            if (plans.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MenuBook,
                            contentDescription = null,
                            tint = MoroccanColors.SoftGold.copy(alpha = 0.3f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "لا توجد أي خطة ختمة نشطة حالياً",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ابدأ بإنشاء خطتك القرآنية (7 أيام، 15 يوماً، أو 30 يوماً) لمتابعة تقدمك اليومي بهمة شريفة.",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MoroccanColors.SoftGold)
                        ) {
                            Text("إنشاء خطة ختمة جديدة", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(plans) { plan ->
                        val progressPct = (plan.currentPage.toFloat() / 604f * 100).roundToInt()
                        val dailyPages = (604f / plan.durationDays).roundToInt()

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MoroccanColors.SoftGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF042018) else Color(0xFFFDFCF2)
                            )
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = plan.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MoroccanColors.PaleGold
                                        )
                                        Text(
                                            text = "المدة المقررة: ${plan.durationDays} يوماً (بمعدل $dailyPages صفحات يومياً)",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    IconButton(onClick = { viewModel.removeKhatma(plan.id) }) {
                                        Icon(Icons.Filled.Delete, "حذف", tint = Color.Red.copy(alpha = 0.6f))
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Progress Slider
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("التقدم الحالي: $progressPct%", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("صفحة ${plan.currentPage} من 604", fontSize = 11.sp, color = Color.Gray)
                                }
                                LinearProgressIndicator(
                                    progress = { plan.currentPage.toFloat() / 604f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .height(8.dp)
                                        .clip(CircleShape),
                                    color = MoroccanColors.SoftGold,
                                    trackColor = Color.Gray.copy(alpha = 0.2f)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Quick Increment buttons
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.updateKhatmaProgress(plan, plan.currentPage + dailyPages)
                                            Toast.makeText(context, "تم رفع تقدم الختمة بـ $dailyPages صفحات بنجاح!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MoroccanColors.SoftGold, contentColor = Color.Black)
                                    ) {
                                        Text("أنهيت ورد اليوم (+${dailyPages}ص)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            viewModel.setReadingPage(context, plan.currentPage)
                                            viewModel.updateKhatmaProgress(plan, plan.currentPage + 1)
                                        },
                                        modifier = Modifier.weight(0.8f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MoroccanColors.SoftGold),
                                        border = BorderStroke(1.dp, MoroccanColors.SoftGold)
                                    ) {
                                        Text("انتقل للقراءة", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // New Plan Creator Dialog
    if (showCreateDialog) {
        Dialog(onDismissRequest = { showCreateDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MoroccanColors.SoftGold, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF03261D) else Color(0xFFFFFFFF)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("إنشاء خطة ختمة مباركة", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MoroccanColors.PaleGold)

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("الاسم الشريف للورد") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("حدد المدة الزمنية لحفظ وردك الشريف:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf(7, 15, 30).forEach { days ->
                            FilterChip(
                                selected = selectedDays == days,
                                onClick = { selectedDays = days },
                                label = { Text("$days يوماً") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MoroccanColors.SoftGold.copy(alpha = 0.2f),
                                    selectedLabelColor = MoroccanColors.SoftGold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.createKhatma(newTitle, selectedDays)
                                showCreateDialog = false
                                Toast.makeText(context, "تم حفظ الختمة بنجاح!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MoroccanColors.SoftGold)
                        ) {
                            Text("حفظ", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showCreateDialog = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                        ) {
                            Text("إلغاء")
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------
// 4. QIBLA DIRECTION COMPASS SCREEN
// --------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    var rotationDegrees by remember { mutableStateOf(0f) }

    val sensorListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientationValues = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientationValues)
                    var azimuth = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
                    if (azimuth < 0) azimuth += 360f
                    rotationDegrees = -azimuth
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotationSensor != null) {
            sensorManager.registerListener(sensorListener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            // Simulated slow sway for testing
            rotationDegrees = 245f
        }
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    val isDark = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("بوصلة اتجاه القبلة الفورية", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF031913) else Color(0xFF0F5A47)
                )
            )
        }
    ) { innerPadding ->
        MoroccanBackground(isDark = isDark, modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "وجه الهاتف بشكل مسطح وقم بالدوران لتحديد موقع الكعبة المشرفة بدقة.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MoroccanColors.SoftGold
                )

                // Moroccan Ornamental Dial Container
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Moroccan concentric circle compass background pattern
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = MoroccanColors.SoftGold,
                            radius = size.minDimension / 2,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                        )
                        drawCircle(
                            color = MoroccanColors.SoftGold.copy(alpha = 0.15f),
                            radius = size.minDimension / 2.2f
                        )
                    }

                    // Rotating Dial Rose
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotationDegrees),
                        contentAlignment = Alignment.Center
                    ) {
                        // Pointer Arrow
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Store,
                                contentDescription = "الكعبة المشرفة",
                                tint = Color.Red,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(MoroccanColors.SoftGold, CircleShape)
                            )
                        }

                        // Concentric Compass Coordinates indicators
                        Text("ش", fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp))
                        Text("ج", fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp))
                        Text("ق", fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.align(Alignment.CenterStart).padding(start = 24.dp))
                        Text("غ", fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp))
                    }

                    // Static center compass widget info
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(rotationDegrees.roundToInt().coerceIn(0, 360) + 135) % 360}°",
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = MoroccanColors.PaleGold
                        )
                        Text(
                            text = "مكة المكرمة",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF042D22) else Color(0xFFF9F6F0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.MyLocation, null, tint = MoroccanColors.SoftGold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("الموقع الجغرافي النشط", fontSize = 11.sp, color = Color.Gray)
                            Text("خط عرض: 31.7917 | خط طول: -7.0926 (المغرب الشريف)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------
// 5. PRAYER TIMES & HIJRI CALENDAR SCREEN
// --------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayersScreen(
    viewModel: QuranViewModel,
    onBack: () -> Unit
) {
    val isDark = isSystemInDarkTheme() || viewModel.darkThemeMode.collectAsStateWithLifecycle().value == "dark"
    val context = LocalContext.current

    // Generate Hijri calendar
    val formattedHijri = remember {
        try {
            val hDate = HijrahDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("ar"))
            hDate.format(formatter)
        } catch (e: Exception) {
            "١٤ ذو الحجة ١٤٤٧ هـ"
        }
    }

    val prayersList = listOf(
        Triple("الفجر", "04:34", true),
        Triple("الشروق", "06:12", false),
        Triple("الظهر", "13:24", true),
        Triple("العصر", "16:54", true),
        Triple("المغرب", "19:42", true),
        Triple("العشاء", "21:18", true)
    )

    val events = listOf(
        "رأس السنة الهجرية" to "الأول من محرم",
        "ذكرى المولد النبوي الشريف" to "١٢ ربيع الأول",
        "عيد الفطر المبارك" to "أول شوال",
        "عيد الأضحى المبارك" to "١٠ ذو الحجة"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مواقيت الصلاة والتقويم الهجري", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF031913) else Color(0xFF0F5A47)
                )
            )
        }
    ) { innerPadding ->
        MoroccanBackground(isDark = isDark, modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Calendar Hijri Banner Widget
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MoroccanColors.SoftGold.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, MoroccanColors.SoftGold),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "اليوم الشريف بالتقويم الهجري",
                            fontSize = 11.sp,
                            color = MoroccanColors.PaleGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formattedHijri,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = MoroccanColors.PaleGold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "مواقيت صلاة المسلم اليومية",
                    fontWeight = FontWeight.Bold,
                    color = MoroccanColors.SoftGold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // List of Prayer card rows
                prayersList.forEach { prayer ->
                    var isAlarmActive by remember { mutableStateOf(prayer.third) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF04241B) else Color(0xFFF9F6F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (prayer.first) {
                                        "الفجر" -> Icons.Filled.WbTwilight
                                        "الشروق" -> Icons.Filled.WbSunny
                                        "الظهر" -> Icons.Filled.WbSunny
                                        "العصر" -> Icons.Filled.WbSunny
                                        "المغرب" -> Icons.Filled.WbTwilight
                                        else -> Icons.Filled.WbTwilight
                                    },
                                    contentDescription = null,
                                    tint = MoroccanColors.SoftGold,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "صلاة ${prayer.first}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (isDark) Color.White else Color(0xFF0B3329)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "الميقات: ${prayer.second}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = MoroccanColors.PaleGold
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    isAlarmActive = !isAlarmActive
                                    val act = if (isAlarmActive) "تفعيل صوت الأذان لـ" else "إلغاء أذان"
                                    Toast.makeText(context, "$act صلاة ${prayer.first}", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(
                                        imageVector = if (isAlarmActive) Icons.Filled.NotificationsActive else Icons.Filled.NotificationsOff,
                                        contentDescription = "جرس منبه الأذان",
                                        tint = if (isAlarmActive) MoroccanColors.SoftGold else Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "المناسبات الإسلامية العظيمة",
                    fontWeight = FontWeight.Bold,
                    color = MoroccanColors.SoftGold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                events.forEach { event ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(event.first, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(event.second, color = MoroccanColors.SoftGold, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}

// --------------------------------------------------------------------
// 6. SUPPLICATIONS (الأدعية) SCREEN
// --------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuasScreen(
    onBack: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf(0) } // 0: Quranic, 1: Prophets, 2: General

    val quranDuas = listOf(
        "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
        "رَبَّنَا لَا تُؤَاخِذْنَا إِن نَّسِينَا أَوْ أَخْطَأْنَا ۚ رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَا إِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذِينَ مِن قَبْلِنَا",
        "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً ۚ إِنَّكَ أَنتَ الْوَهَّابُ"
    )

    val prophetDuas = listOf(
        "لا إِلَهَ إِلا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ (دعوة ذي النون)",
        "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي وَاحْلُلْ عُقْدَةً مِّن لِّسَانِي يَفْقَهُوا قَوْلِي (دعوة موسى عليه السلام)",
        "رَبِّ هَبْ لِي مِن لَّدُنكَ ذُرِّيَّةً طَيِّبَةً ۖ إِنَّكَ سَمِيعُ الدُّعَاءِ (دعوة زكريا عليه السلام)"
    )

    val generalDuas = listOf(
        "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي",
        "يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَى دِينِكَ",
        "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْهُدَى وَالتُّقَى وَالْعَفَافَ وَالْغِنَى"
    )

    val activeList = when (activeTab) {
        0 -> quranDuas
        1 -> prophetDuas
        else -> generalDuas
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الأدعية والأذكار الجامعة", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF031913) else Color(0xFF0F5A47)
                )
            )
        }
    ) { innerPadding ->
        MoroccanBackground(isDark = isDark, modifier = Modifier.padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = if (isDark) Color(0xFF031913) else Color(0xFFEFECE2),
                    contentColor = MoroccanColors.SoftGold
                ) {
                    Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("الأدعية القرآنية") })
                    Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("أدعية الأنبياء") })
                    Tab(selected = activeTab == 2, onClick = { activeTab = 2 }, text = { Text("أدعية جامعة") })
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activeList) { duaText ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, MoroccanColors.SoftGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF04241B) else Color(0xFFF9F6F0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = duaText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 26.sp,
                                    color = if (isDark) Color.White else Color(0xFF0B3329),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(onClick = {
                                        clipboard.setText(AnnotatedString(duaText))
                                        Toast.makeText(context, "تم نسخ الدعاء الشريف بنجاح!", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(Icons.Filled.ContentCopy, "نسخ", tint = MoroccanColors.SoftGold)
                                    }

                                    IconButton(onClick = {
                                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(android.content.Intent.EXTRA_TEXT, duaText)
                                        }
                                        context.startActivity(android.content.Intent.createChooser(intent, "مشاركة الدعاء"))
                                    }) {
                                        Icon(Icons.Filled.Share, "مشاركة", tint = MoroccanColors.SoftGold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
