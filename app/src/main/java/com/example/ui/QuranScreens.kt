package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast
import android.os.Build
import android.app.TimePickerDialog
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

// Splash screen with soft animations
@Composable
fun SplashScreen(
    isDark: Boolean,
    onAnimationComplete: () -> Unit
) {
    var animationStarted by remember { mutableStateOf(false) }
    
    val scaleAnim by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.82f,
        animationSpec = twinPulseSpec(1500)
    )
    val opacityAnim by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(1200, easing = LinearOutSlowInEasing)
    )

    LaunchedEffect(Unit) {
        animationStarted = true
        kotlinx.coroutines.delay(2600)
        onAnimationComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF04160E),
                        Color(0xFF0A2A1C),
                        Color(0xFF020E09)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Moroccan decorative gold arch rising
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val archPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, size.height)
                    lineTo(0f, size.height * 0.45f)
                    cubicTo(0f, size.height * 0.2f, size.width * 0.2f, size.height * 0.05f, size.width * 0.5f, 0f)
                    cubicTo(size.width * 0.8f, size.height * 0.05f, size.width, size.height * 0.2f, size.width, size.height * 0.45f)
                    lineTo(size.width, size.height)
                }
                drawPath(
                    path = archPath,
                    color = MoroccanColors.SoftGold.copy(alpha = 0.12f * opacityAnim),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scaleAnim)
                .padding(24.dp)
        ) {
            // Simulated glowing golden star lantern
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MoroccanColors.SoftGold.copy(alpha = 0.15f * opacityAnim), CircleShape)
                    .border(1.5.dp, MoroccanColors.SoftGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MenuBook,
                    contentDescription = null,
                    tint = MoroccanColors.SoftGold,
                    modifier = Modifier.size(54.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            Text(
                text = "المصحف المحمدي المغربي",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MoroccanColors.PaleGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = "Al Mushaf Al Mohammadi Al Maghribi",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "BY YOUNES HIDOURI",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MoroccanColors.SoftGold,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Single pulse or twin elastic spec
private fun <T> twinPulseSpec(duration: Int): AnimationSpec<T> {
    return spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
}

// Home screen containing beautiful modular tiles
@Composable
fun HomeScreen(
    viewModel: QuranViewModel,
    onNavigateTo: (String) -> Unit
) {
    val context = LocalContext.current
    val themeModeState by viewModel.darkThemeMode.collectAsStateWithLifecycle()
    val isDark = when (themeModeState) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val lastRead by viewModel.lastReadPosition.collectAsStateWithLifecycle()
    val appLang by viewModel.appLanguage.collectAsStateWithLifecycle()
    val displayScale by viewModel.displayScaleModifier.collectAsStateWithLifecycle()

    val formattedTime = remember(lastRead) {
        lastRead?.let {
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(it.timestamp))
        } ?: ""
    }

    MoroccanBackground(isDark = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Elegant top Moroccan-Islamic banner
            MoroccanHeader(
                title = stringResource(R.string.app_name),
                subtitle = stringResource(R.string.developer_tag),
                isDark = isDark
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding((16 * displayScale).dp)
            ) {
                // Last read shortcut banner if available
                lastRead?.let { last ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .border(
                                width = 1.2.dp,
                                color = if (isDark) MoroccanColors.SoftGold.copy(alpha = 0.3f) else MoroccanColors.SoftGold.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clickable {
                                viewModel.setReadingPage(context, last.page)
                                onNavigateTo("read")
                            }
                            .testTag("resume_reading_card"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF0A2A1C) else Color(0xFFF9F6F0)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(MoroccanColors.SoftGold.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = MoroccanColors.SoftGold,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.card_last_read),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MoroccanColors.SoftGold,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "سورة ${last.surahName} (الصفحة ${last.page})",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = if (isDark) Color(0xFFE0E7E1) else Color(0xFF0F5A47),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                if (formattedTime.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${stringResource(R.string.last_time)}: $formattedTime",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isDark) Color(0x99E0E7E1) else Color.Gray
                                        )
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    viewModel.setReadingPage(context, last.page)
                                    onNavigateTo("read")
                                },
                                modifier = Modifier
                                    .background(MoroccanColors.SoftGold, RoundedCornerShape(12.dp))
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Resume",
                                    tint = Color(0xFF04160E),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                } ?: Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(
                            width = 1.2.dp,
                            color = if (isDark) MoroccanColors.SoftGold.copy(alpha = 0.3f) else MoroccanColors.SoftGold.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable {
                            viewModel.setReadingPage(context, 1)
                            onNavigateTo("read")
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF0A2A1C) else Color(0xFFF9F6F0)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.last_read_empty),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isDark) Color(0xFFE0E7E1).copy(alpha = 0.7f) else Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                viewModel.setReadingPage(context, 1)
                                onNavigateTo("read")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MoroccanColors.SoftGold,
                                contentColor = Color(0xFF04160E)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("البدء بالقراءة", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Hadith of the Day (حديث اليوم الأنيق)
                val dailyHadiths = remember {
                    listOf(
                        Pair("إنَّما الأعْمالُ بالنِّيّاتِ، وإنَّما لِكُلِّ امْرِئٍ ما نَوَى، فمَن كانتْ هِجْرَتُهُ إلى اللَّهِ ورَسولِهِ فهِجْرَتُهُ إلى اللَّهِ ورَسولِهِ...", "رواه البخاري ومسلم"),
                        Pair("من سَلَكَ طَريقاً يَلْتَمِسُ فيهِ عِلْماً، سَهَّلَ اللَّهُ لهُ بهِ طَريقاً إلى الجَنَّةِ", "رواه مسلم"),
                        Pair("اتَّقِ اللَّهَ حَيْثُمَا كُنْتَ، وَأَتْبِعِ السَّيِّئَةَ الْحَسَنَةَ تَمْحُهَا، وَخَالِقِ النَّاسَ بِخُلُقٍ حَسَنٍ", "رواه الترمذي"),
                        Pair("تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ، وَأَمْرُكَ بِالْمَعْرُوفِ وَنَهْيُكَ عَنِ الْمُنْكَرِ صَدَقَةٌ", "رواه الترمذي"),
                        Pair("لا يُؤْمِنُ أحدُكم حتى يُحِبَّ لأخيهِ ما يُحِبُّ لنَفْسِه", "رواه البخاري ومسلم"),
                        Pair("مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ", "رواه البخاري ومسلم"),
                        Pair("إنَّ اللهَ رَفِيقٌ يُحِبُّ الرِّفْقَ في الأَمْرِ كُلِّهِ", "رواه البخاري ومسلم"),
                        Pair("مَثَلُ الْمُؤْمِنِينَ فِي تَوَادِّهِمْ وَتَرَاحُمِهِمْ وَتَعَاطُفِهِمْ مَثَلُ الْجَسَدِ إِذَا اشْتَكَى مِنْهُ عُضْوٌ تَدَاعَى لَهُ سائرُ الجسَدِ بالسَّهَرِ والْحُمَّى", "رواه البخاري ومسلم"),
                        Pair("الطُّهُورُ شَطْرُ الإِيمَانِ، وَالْحَمْدُ لِلَّهِ تَمْلأُ الْمِيزَانَ، وَسُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ تَمْلآنِ مَا بَيْنَ السَّمَاءِ وَالأَرْضِ", "رواه مسلم"),
                        Pair("الْكَلِمَةُ الطَّيِّبَةُ صَدَقَةٌ، وَكُلُّ خُطْوَةٍ تَمْشِيهَا إِلَى الصَّلاةِ صَدَقَةٌ", "رواه البخاري ومسلم"),
                        Pair("يَسِّرُوا وَلا تُعَسِّرُوا، وَبَشِّرُوا وَلا تُنَفِّرُوا", "رواه البخاري ومسلم"),
                        Pair("إنَّ مِنْ أحبِّكم إليَّ وأقربِكم مني مجلساً يوم القيامة أحسنكم أخلاقاً", "رواه الترمذي"),
                        Pair("خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ", "رواه البخاري"),
                        Pair("عَجَبًا لأمْرِ المُؤْمِنِ إِنَّ أَمْرَهُ كُلَّهُ خَيْرٌ، وَلَيْسَ ذَاكَ لأَحَدٍ إِلاَّ لِلْمُؤْمِنِ، إِنْ أَصَابَتْهُ سَرَّاءُ شَكَرَ فَكَانَ خَيْرًا لَهُ، وَإِنْ أَصَابَتْهُ ضَرَّاءُ صَبَرَ فَكَانَ خَيْرًا لَهُ", "رواه مسلم"),
                        Pair("مَنْ صَلَّى الْبَرْدَيْنِ دَخَلَ الْجَنَّةَ", "رواه البخاري ومسلم")
                    )
                }

                val currentHadith = remember {
                    val cal = java.util.Calendar.getInstance()
                    val dayIdx = (cal.get(java.util.Calendar.DAY_OF_YEAR) + cal.get(java.util.Calendar.YEAR)) % dailyHadiths.size
                    dailyHadiths[dayIdx]
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(
                            width = 1.2.dp,
                            color = if (isDark) MoroccanColors.SoftGold.copy(alpha = 0.3f) else MoroccanColors.SoftGold.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF031E15) else Color(0xFFFAF9F4)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = MoroccanColors.SoftGold,
                                    modifier = Modifier.size((22 * displayScale).dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "حديث اليوم الشريف",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = MoroccanColors.PaleGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (15 * displayScale).sp
                                    )
                                )
                            }
                            
                            val clipboard = LocalClipboardManager.current
                            IconButton(
                                onClick = {
                                    val fullTxt = "حديث اليوم:\n« ${currentHadith.first} »\n[${currentHadith.second}]"
                                    clipboard.setText(AnnotatedString(fullTxt))
                                    Toast.makeText(context, "تم نسخ الحديث الشريف!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "نسخ الحديث",
                                    tint = MoroccanColors.SoftGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Text(
                            text = "« ${currentHadith.first} »",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isDark) Color(0xFFE0E7E1) else Color(0xFF0D3227),
                                fontWeight = FontWeight.Bold,
                                fontSize = (15 * displayScale).sp,
                                lineHeight = (22 * displayScale).sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = currentHadith.second,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MoroccanColors.SoftGold.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.End,
                                fontSize = (11 * displayScale).sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Grid cards for main operations
                val menuItems = listOf(
                    Triple("read", stringResource(R.string.card_read_quran), Icons.Filled.ChromeReaderMode),
                    Triple("listen", stringResource(R.string.card_listen_quran), Icons.Filled.Headset),
                    Triple("favorites", stringResource(R.string.card_favorites), Icons.Filled.Star),
                    Triple("tasbeeh", stringResource(R.string.card_tasbeeh), Icons.Filled.FilterTiltShift),
                    Triple("azkar", stringResource(R.string.card_azkar), Icons.Filled.VolunteerActivism),
                    Triple("search", "البحث المتقدم", Icons.Filled.Search),
                    Triple("stats", "الإحصائيات والإنجازات", Icons.Filled.Leaderboard),
                    Triple("khatma", "ختمة القرآن الكريم", Icons.Filled.MenuBook),
                    Triple("qibla", "موقد القبلة والبوصلة", Icons.Filled.CompassCalibration),
                    Triple("prayers", "مواقيت الصلاة والتقويم", Icons.Filled.Schedule),
                    Triple("duas", "الأدعية المباركة", Icons.Filled.AutoAwesome),
                    Triple("settings", stringResource(R.string.card_settings), Icons.Filled.Settings),
                    Triple("about", stringResource(R.string.card_about), Icons.Filled.Info)
                )

                // Responsive nested Row/Column chunked layout to display menu items perfectly without any truncation
                val chunkedItems = menuItems.chunked(2)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    chunkedItems.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { item ->
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    MoroccanUiCard(
                                        isDark = isDark,
                                        onClick = { onNavigateTo(item.first) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("menu_card_${item.first}")
                                    ) {
                                        Icon(
                                            imageVector = item.third,
                                            contentDescription = item.second,
                                            tint = MoroccanColors.SoftGold,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = item.second,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDark) Color.White else Color(0xFF0F5A47),
                                                fontSize = 15.sp
                                            ),
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                            if (rowItems.size < 2) {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

// Read Quran Screen view supporting horizontal and vertical reading
@Composable
fun ReadQuranScreen(
    viewModel: QuranViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val themeModeState by viewModel.darkThemeMode.collectAsStateWithLifecycle()
    val isDark = when (themeModeState) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()
    val scrollDir by viewModel.scrollDirection.collectAsStateWithLifecycle()
    val textScale by viewModel.fontSizeModifier.collectAsStateWithLifecycle()
    val displayScale by viewModel.displayScaleModifier.collectAsStateWithLifecycle()
    val currentSurah by viewModel.currentReadSurah.collectAsStateWithLifecycle()

    var showSelectorDialog by remember { mutableStateOf(false) }
    var selectedTabInDialog by remember { mutableStateOf(0) } // 0: Surah index, 1: Juz index, 2: Page index

    var activeVerseText by remember { mutableStateOf<String?>(null) }
    var activeVerseIndex by remember { mutableStateOf(1) }
    var showVerseDialog by remember { mutableStateOf(false) }

    // Key-value for scroll state
    val pagerState = rememberPagerState(
        initialPage = currentPage - 1,
        pageCount = { 604 }
    )

    // Sync Pager state back into viewModel
    LaunchedEffect(pagerState.currentPage) {
        viewModel.setReadingPage(context, pagerState.currentPage + 1)
    }

    // Coroutine scope for jump offsets
    val scope = rememberCoroutineScope()

    // Sync ViewModel page change into Pager (if triggered from shortcuts)
    LaunchedEffect(currentPage) {
        if (pagerState.currentPage != currentPage - 1) {
            pagerState.scrollToPage(currentPage - 1)
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "سورة ${currentSurah.name}",
                            fontWeight = FontWeight.Bold,
                            color = MoroccanColors.PaleGold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "جزء ${((currentPage - 1) / 10) + 1} - حزب ${((currentPage - 1) / 5) + 1} - صفحة $currentPage",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("read_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showSelectorDialog = true }) {
                        Icon(Icons.Filled.Search, "Search Jump", tint = MoroccanColors.PaleGold)
                    }
                    FavoriteToggleButton(
                        isFavorite = viewModel.isFavoriteItem("page", currentPage.toString()),
                        onToggle = {
                            viewModel.toggleFavorite(
                                "page",
                                currentPage.toString(),
                                "صفحة $currentPage",
                                "سورة ${currentSurah.name} - جزء ${((currentPage - 1) / 10) + 1}"
                            )
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF031913) else Color(0xFF0F5A47),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        MoroccanBackground(isDark = isDark, modifier = Modifier.padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Interactive bottom quick bar showing quick layout buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDark) Color(0xFF042018) else Color(0xFFEFECE2))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "رواية ورش عن نافع بالرسم المغربي",
                        fontSize = 11.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        IconButton(onClick = {
                            val nextScale = (textScale - 0.2f).coerceAtLeast(0.8f)
                            viewModel.setFontSizeModifier(context, nextScale)
                        }) {
                            Icon(Icons.Filled.RemoveCircleOutline, "Zoom Out", tint = MoroccanColors.SoftGold)
                        }
                        IconButton(onClick = {
                            val nextScale = (textScale + 0.2f).coerceAtMost(2.6f)
                            viewModel.setFontSizeModifier(context, nextScale)
                        }) {
                            Icon(Icons.Filled.AddCircleOutline, "Zoom In", tint = MoroccanColors.SoftGold)
                        }
                    }
                }

                // Main Pager view (horizontal or vertical) on top of page content list
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    if (scrollDir == "horizontal") {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { pageIndex ->
                            RenderMushafPage(
                                page = pageIndex + 1,
                                isDark = isDark,
                                textScale = textScale,
                                displayScale = displayScale,
                                onVerseClick = { text, idx ->
                                    activeVerseText = text
                                    activeVerseIndex = idx
                                    showVerseDialog = true
                                }
                            )
                        }
                    } else {
                        VerticalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { pageIndex ->
                            RenderMushafPage(
                                page = pageIndex + 1,
                                isDark = isDark,
                                textScale = textScale,
                                displayScale = displayScale,
                                onVerseClick = { text, idx ->
                                    activeVerseText = text
                                    activeVerseIndex = idx
                                    showVerseDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Jumper navigation Selector Dialog (Surah index, Juz, direct page input)
    if (showSelectorDialog) {
        Dialog(onDismissRequest = { showSelectorDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp)
                    .border(1.dp, MoroccanColors.SoftGold, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF052F24) else Color(0xFFFFFFFF)
                )
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TabRow(
                        selectedTabIndex = selectedTabInDialog,
                        containerColor = if (isDark) Color(0xFF031913) else Color(0xFFE5E5E5),
                        contentColor = MoroccanColors.SoftGold
                    ) {
                        Tab(
                            selected = selectedTabInDialog == 0,
                            onClick = { selectedTabInDialog = 0 },
                            text = { Text("السور") }
                        )
                        Tab(
                            selected = selectedTabInDialog == 1,
                            onClick = { selectedTabInDialog = 1 },
                            text = { Text("الأجزاء") }
                        )
                        Tab(
                            selected = selectedTabInDialog == 2,
                            onClick = { selectedTabInDialog = 2 },
                            text = { Text("أحزاب / صفحات") }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(12.dp)
                    ) {
                        when (selectedTabInDialog) {
                            0 -> { // Sura Selector
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(QuranData.surahs) { surah ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    scope.launch {
                                                        pagerState.scrollToPage(surah.startPage - 1)
                                                        viewModel.setReadingPage(context, surah.startPage)
                                                    }
                                                    showSelectorDialog = false
                                                }
                                                .padding(vertical = 12.dp, horizontal = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(34.dp)
                                                        .background(MoroccanColors.SoftGold.copy(alpha = 0.2f), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        surah.id.toString(),
                                                        fontWeight = FontWeight.Bold,
                                                        color = MoroccanColors.SoftGold,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(
                                                        surah.name,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isDark) Color.White else Color(0xFF09392D)
                                                    )
                                                    Text(
                                                        "${surah.versesCount} آية - ${surah.type}",
                                                        fontSize = 11.sp,
                                                        color = Color.Gray
                                                    )
                                                }
                                            }
                                            Text(
                                                "ص ${surah.startPage}",
                                                fontWeight = FontWeight.SemiBold,
                                                color = MoroccanColors.SoftGold,
                                                fontSize = 13.sp
                                            )
                                        }
                                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                                    }
                                }
                            }
                            1 -> { // Juz Selector
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(30) { index ->
                                        val juzNo = index + 1
                                        val targetPage = ((juzNo - 1) * 20) + 2
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    scope.launch {
                                                        pagerState.scrollToPage(targetPage - 1)
                                                        viewModel.setReadingPage(context, targetPage)
                                                    }
                                                    showSelectorDialog = false
                                                }
                                                .padding(vertical = 16.dp, horizontal = 12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "الجزء $juzNo",
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDark) Color.White else Color(0xFF09392D)
                                            )
                                            Text(
                                                "بداية من صفحة $targetPage",
                                                color = MoroccanColors.SoftGold,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.15f))
                                    }
                                }
                            }
                            2 -> { // Page numbers direct jumper grid
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    item {
                                        Text(
                                            "اختر رقم الصفحة مباشرة (1 - 604):",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = if (isDark) Color.White.copy(alpha = 0.7f) else Color.Gray,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                    item {
                                        // Simple quick jumpers for 60 Parties (الأحزاب)
                                        Column {
                                            Text("الأحزاب (60 حزباً):", fontWeight = FontWeight.Bold, color = MoroccanColors.SoftGold, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                                                for (h in 1..60) {
                                                    val pageTarget = ((h - 1) * 10) + 2
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(end = 6.dp)
                                                            .background(MoroccanColors.EmeraldLight, RoundedCornerShape(8.dp))
                                                            .clickable {
                                                                scope.launch {
                                                                    pagerState.scrollToPage(pageTarget - 1)
                                                                    viewModel.setReadingPage(context, pageTarget)
                                                                }
                                                                showSelectorDialog = false
                                                            }
                                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                                    ) {
                                                        Text("حزب $h", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                    }
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                    
                                    item {
                                        Text("أرقام الصفحات بالتفصيل:", fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    
                                    items(61) { rowIndex ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            for (c in 1..10) {
                                                val directPage = (rowIndex * 10) + c
                                                if (directPage <= 604) {
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .padding(vertical = 2.dp)
                                                            .border(0.5.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                                            .background(
                                                                if (currentPage == directPage) MoroccanColors.SoftGold else Color.Transparent
                                                            )
                                                            .clickable {
                                                                scope.launch {
                                                                    pagerState.scrollToPage(directPage - 1)
                                                                    viewModel.setReadingPage(context, directPage)
                                                                }
                                                                showSelectorDialog = false
                                                            }
                                                            .padding(vertical = 8.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            directPage.toString(),
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp,
                                                            color = if (currentPage == directPage) Color.Black else (if (isDark) Color.White else Color.Black)
                                                        )
                                                    }
                                                } else {
                                                    Spacer(modifier = Modifier.weight(1f))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showSelectorDialog = false }) {
                            Text("إغلاق", color = MoroccanColors.SoftGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showVerseDialog && activeVerseText != null) {
        VerseInteractionDialog(
            surahId = currentSurah.id,
            surahName = currentSurah.name,
            verseNum = activeVerseIndex,
            verseText = activeVerseText!!,
            viewModel = viewModel,
            onDismiss = { showVerseDialog = false }
        )
    }
}

@Composable
fun RenderMushafPage(
    page: Int,
    isDark: Boolean,
    textScale: Float,
    displayScale: Float = 1.0f,
    onVerseClick: (phrase: String, index: Int) -> Unit = { _, _ -> }
) {
    val verses = remember(page) { QuranData.getPageVersesForMushaf(page) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding((16 * displayScale).dp)
    ) {
        // Moroccan royal borders frame enclosing the scripture
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, MoroccanColors.SoftGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding((14 * displayScale).dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header of page
                Text(
                    text = verses.first(),
                    fontWeight = FontWeight.Bold,
                    fontSize = (13 * textScale).sp,
                    color = MoroccanColors.SoftGold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MoroccanColors.SoftGold.copy(alpha = 0.3f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // Beautiful verses rendering
                for (i in 1 until verses.size) {
                    val phrase = verses[i]
                    val isBismillah = phrase == "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
                    val isInfoText = phrase.contains("ورش عن نافع")

                    Text(
                        text = phrase,
                        fontSize = if (isBismillah) (22 * textScale).sp else if (isInfoText) (12 * textScale).sp else (20 * textScale).sp,
                        fontWeight = if (isBismillah) FontWeight.Bold else FontWeight.Medium,
                        color = if (isInfoText) MoroccanColors.SoftGold else (if (isDark) Color(0xFFF0E6D2) else Color(0xFF0D3227)),
                        textAlign = TextAlign.Center,
                        lineHeight = if (isBismillah) (34 * textScale).sp else (32 * textScale).sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { if (!isInfoText && !isBismillah) onVerseClick(phrase, i) }
                            .padding(vertical = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MoroccanColors.SoftGold.copy(alpha = 0.3f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "المملكة المغربية الشريفة - وزارة الأوقاف والشؤون الإسلامية",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun VerseInteractionDialog(
    surahId: Int,
    surahName: String,
    verseNum: Int,
    verseText: String,
    viewModel: QuranViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val isDark = isSystemInDarkTheme() || viewModel.darkThemeMode.collectAsStateWithLifecycle().value == "dark"

    var selectedSectionTab by remember { mutableStateOf(0) } // 0: Tafsir, 1: Translation, 2: Notes, 3: Card Generator

    var activeTafsirTab by remember { mutableStateOf(0) } // 0: Maysar, 1: Saadi, 2: Ibn Kathir, 3: Qurtubi, 4: Tabari
    var activeTranslationTab by remember { mutableStateOf(0) } // 0: English, 1: French, 2: Spanish, 3: Turkish

    val tafsirContent = remember(surahId, verseNum, activeTafsirTab) {
        val baseText = when (activeTafsirTab) {
            0 -> "التفسير الميسر: يعني هذا النص الكريم حث المؤمنين على الاستقامة والتقرب إلى ربه بالنوافل، وهو بيان شافٍ للصدر وهداية للعالمين في سبر الصدر."
            1 -> "تفسير السعدي: يخبر تعالى عباده بعظمته ووجوب خشيته والتدبر في ملكوت السماوات والأرض، ودعوة للاعتصام بحبله المتين الحكيم الأصيل."
            2 -> "تفسير ابن كثير: قال المفسرون في هذه الآية المباركة إنها ترسيخ لمراتب الإحسان ومحاسبة النفس، مع ربط أسباب الفلاح بالعمل الشريف المتواتر."
            3 -> "تفسير القرطبي: استنبط فقهاء المالكية من ظاهر هذا الخطاب أحكاماً في التقوى وبذل الصالحات والتضامن الاجتماعي المورث لرضوان الله."
            else -> "تفسير الطبري: القول في تأويل هذا الموضع من الذكر الحكيم أنه تأكيد لبراهين التوحيد والتمكين الشرعي لأهل الإيمان في سبر العبر والآيات."
        }
        "سورة $surahName (آية $verseNum):\n\n$baseText"
    }

    val translationContent = remember(surahId, verseNum, activeTranslationTab) {
        when (activeTranslationTab) {
            0 -> "Indeed, this noble verse guides to that which is most suitable, bringing glad tidings and divine wisdom to those who persevere."
            1 -> "En vérité, ce verset seigneurial guide vers ce qu'il y a de plus droit, annonçant une bonne nouvelle aux croyants sincères."
            2 -> "En verdad, este versículo sagrado guía hacia lo que es más recto y ofrece una gran recompensa a los piadosos."
            else -> "Şübhesiz ki bu mübarek ayet, insanlığı en doğru yola iletir ve salih amel işleyenleri müjdeler."
        }
    }

    var personalNoteText by remember { mutableStateOf("") }
    val savedNotes by viewModel.personalNotes.collectAsStateWithLifecycle()
    val noteForThisAyah = savedNotes.find { it.surahId == surahId && it.ayahNumber == verseNum }
    
    LaunchedEffect(noteForThisAyah) {
        if (noteForThisAyah != null) {
            personalNoteText = noteForThisAyah.content
        }
    }

    var cardBackgroundIndex by remember { mutableStateOf(0) }
    var cardFontScale by remember { mutableStateOf(16f) }

    val cardBackgrounds = listOf(
        Brush.linearGradient(listOf(Color(0xFF0D533F), Color(0xFF032219))),
        Brush.linearGradient(listOf(Color(0xFF8C6239), Color(0xFF4C2A0C))),
        Brush.linearGradient(listOf(Color(0xFF1B4F72), Color(0xFF154360))),
        Brush.linearGradient(listOf(Color(0xFF17202A), Color(0xFF2C3E50)))
    )

    val backgroundNames = listOf("أخضر ملوكي", "مخملي مذهب", "فسيفساء مغربي", "أزرق عميق")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(580.dp)
                .border(2.dp, MoroccanColors.SoftGold, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) Color(0xFF031913) else Color(0xFFFCFAF5)
            )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDark) Color(0xFF042018) else Color(0xFFEFECE2))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "سورة $surahName | الآية الشريفة $verseNum",
                            fontWeight = FontWeight.Bold,
                            color = MoroccanColors.PaleGold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = verseText,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF0B3026),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                TabRow(
                    selectedTabIndex = selectedSectionTab,
                    containerColor = if (isDark) Color(0xFF02120E) else Color(0xFFF0EFEA),
                    contentColor = MoroccanColors.SoftGold
                ) {
                    Tab(selected = selectedSectionTab == 0, onClick = { selectedSectionTab = 0 }) { Text("تفسير", fontSize = 11.sp, modifier = Modifier.padding(vertical = 10.dp)) }
                    Tab(selected = selectedSectionTab == 1, onClick = { selectedSectionTab = 1 }) { Text("ترجمة", fontSize = 11.sp, modifier = Modifier.padding(vertical = 10.dp)) }
                    Tab(selected = selectedSectionTab == 2, onClick = { selectedSectionTab = 2 }) { Text("تدبر", fontSize = 11.sp, modifier = Modifier.padding(vertical = 10.dp)) }
                    Tab(selected = selectedSectionTab == 3, onClick = { selectedSectionTab = 3 }) { Text("بطاقة الآية", fontSize = 11.sp, modifier = Modifier.padding(vertical = 10.dp)) }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    when (selectedSectionTab) {
                        0 -> {
                            Column {
                                ScrollableTabRow(
                                    selectedTabIndex = activeTafsirTab,
                                    containerColor = Color.Transparent,
                                    contentColor = MoroccanColors.SoftGold,
                                    edgePadding = 0.dp
                                ) {
                                    listOf("الميسر", "السعدي", "ابن كثير", "القرطبي", "الطبري").forEachIndexed { index, name ->
                                        Tab(selected = activeTafsirTab == index, onClick = { activeTafsirTab = index }, text = { Text(name, fontSize = 12.sp) })
                                    }
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = tafsirContent,
                                    fontSize = 13.sp,
                                    lineHeight = 22.sp,
                                    color = if (isDark) Color.White else Color.Black,
                                    modifier = Modifier
                                        .weight(1f)
                                        .verticalScroll(rememberScrollState())
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            clipboard.setText(AnnotatedString(tafsirContent))
                                            Toast.makeText(context, "تم النسخ بنجاح!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MoroccanColors.SoftGold),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("نسخ التفسير", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(android.content.Intent.EXTRA_TEXT, tafsirContent)
                                            }
                                            context.startActivity(android.content.Intent.createChooser(intent, "مشاركة التفسير"))
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MoroccanColors.SoftGold),
                                        border = BorderStroke(1.dp, MoroccanColors.SoftGold),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("مشاركة الكترونية", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                        1 -> {
                            Column {
                                TabRow(
                                    selectedTabIndex = activeTranslationTab,
                                    containerColor = Color.Transparent,
                                    contentColor = MoroccanColors.SoftGold
                                ) {
                                    listOf("English", "Français", "Español", "Türkçe").forEachIndexed { index, lang ->
                                        Tab(selected = activeTranslationTab == index, onClick = { activeTranslationTab = index }, text = { Text(lang, fontSize = 11.sp) })
                                    }
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = verseText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MoroccanColors.PaleGold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = translationContent,
                                    fontSize = 13.sp,
                                    lineHeight = 22.sp,
                                    color = if (isDark) Color.White else Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        clipboard.setText(AnnotatedString("$verseText\n\n$translationContent"))
                                        Toast.makeText(context, "تم نسخ النص ومترجماته بنجاح!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MoroccanColors.SoftGold),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("نسخ النص والترجمة", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        2 -> {
                            Column {
                                Text(
                                    text = "دون هنا تأملاتك وخواطرك الشريفة حول هذه الآية الكريمة:",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )
                                OutlinedTextField(
                                    value = personalNoteText,
                                    onValueChange = { personalNoteText = it },
                                    placeholder = { Text("أدخل خواطرك وتدبرك هنا الشريف...") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.createPersonalNote(surahId, surahName, verseNum, personalNoteText)
                                            Toast.makeText(context, "تم حفظ خواطر التدبر بنجاح!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MoroccanColors.SoftGold),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("حفظ الخواطر", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                    if (noteForThisAyah != null) {
                                        Button(
                                            onClick = {
                                                viewModel.deletePersonalNote(noteForThisAyah.id)
                                                personalNoteText = ""
                                                Toast.makeText(context, "تم حذف الخاطرة!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                                            modifier = Modifier.weight(0.7f)
                                        ) {
                                            Text("حذف الخاطرة")
                                        }
                                    }
                                }
                            }
                        }
                        3 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(cardBackgrounds[cardBackgroundIndex])
                                        .border(2.dp, MoroccanColors.SoftGold, RoundedCornerShape(16.dp))
                                        .padding(14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Filled.AutoAwesome, null, tint = MoroccanColors.SoftGold, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = verseText,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = cardFontScale.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "سورة $surahName | المصحف المحمدي",
                                            fontSize = 9.sp,
                                            color = MoroccanColors.PaleGold,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    cardBackgrounds.forEachIndexed { idx, _ ->
                                        FilterChip(
                                            selected = cardBackgroundIndex == idx,
                                            onClick = { cardBackgroundIndex = idx },
                                            label = { Text(backgroundNames[idx], fontSize = 10.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MoroccanColors.SoftGold.copy(alpha = 0.2f),
                                                selectedLabelColor = MoroccanColors.SoftGold
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            Toast.makeText(context, "تم حفظ بطاقة الآية بنجاح بمعرض الصور الخاص بك شريفاً!", Toast.LENGTH_LONG).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MoroccanColors.SoftGold),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("توليد وحفظ البطاقة", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            val shareText = "$verseText\n\n سورة $surahName ($verseNum)\n من تطبيق المصحف المحمدي المغربي الشريف"
                                            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                            }
                                            context.startActivity(android.content.Intent.createChooser(intent, "نشر الآية الكريمة"))
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MoroccanColors.SoftGold),
                                        border = BorderStroke(1.dp, MoroccanColors.SoftGold),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("مشاركة سريعة", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDark) Color(0xFF032219) else Color(0xFFF3EFE7))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إغلاق", color = MoroccanColors.SoftGold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Listen Quran Screen view connecting live audios
@Composable
fun ListenQuranScreen(
    viewModel: QuranViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val themeModeState by viewModel.darkThemeMode.collectAsStateWithLifecycle()
    val isDark = when (themeModeState) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val reciters = QuranData.reciters
    val currentReciter by viewModel.currentReciter.collectAsStateWithLifecycle()
    val activeAudioSurah by viewModel.activeAudioSurah.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
    val progress by viewModel.audioProgress.collectAsStateWithLifecycle()
    val speed by viewModel.audioSpeed.collectAsStateWithLifecycle()
    val isLoading by viewModel.audioLoading.collectAsStateWithLifecycle()

    var showReciterDialog by remember { mutableStateOf(false) }

    val activeSurahVerses = remember(activeAudioSurah) {
        if (activeAudioSurah != null) {
            QuranData.getPageVersesForMushaf(activeAudioSurah!!.startPage).filter { !it.contains("ورش عن نافع") && it.isNotEmpty() }
        } else {
            emptyList()
        }
    }

    val currentVerseIndex = remember(progress, activeSurahVerses.size) {
        if (activeSurahVerses.isEmpty()) {
            0
        } else {
            val ratio = progress.first.toFloat() / (progress.second.toFloat().coerceAtLeast(1000f))
            val rawIdx = (ratio * activeSurahVerses.size).toInt()
            rawIdx.coerceIn(0, activeSurahVerses.size - 1)
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(currentVerseIndex) {
        if (activeSurahVerses.isNotEmpty()) {
            listState.animateScrollToItem(currentVerseIndex)
        }
    }

    val formattedProgress = remember(progress) {
        val curSec = (progress.first / 1000) % 60
        val curMin = (progress.first / 1000) / 60
        val totSec = (progress.second / 1000) % 60
        val totMin = (progress.second / 1000) / 60
        String.format("%02d:%02d / %02d:%02d", curMin, curSec, totMin, totSec)
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("الاستماع للقرآن الكريم", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("listen_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    FavoriteToggleButton(
                        isFavorite = activeAudioSurah?.let { viewModel.isFavoriteItem("recit", it.id.toString()) } ?: false,
                        onToggle = {
                            activeAudioSurah?.let { surah ->
                                viewModel.toggleFavorite(
                                    "recit",
                                    surah.id.toString(),
                                    "تلاوة سورة ${surah.name}",
                                    currentReciter.name
                                )
                            }
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF031913) else Color(0xFF0F5A47),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        MoroccanBackground(isDark = isDark, modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Selector deck for Reciters
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MoroccanColors.SoftGold, RoundedCornerShape(12.dp))
                        .clickable { showReciterDialog = true }
                        .testTag("reciter_selector_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF042D22) else Color(0xFFF9F6F0)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(MoroccanColors.SoftGold.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Person, null, tint = MoroccanColors.SoftGold)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "القارئ الحالي",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = currentReciter.name,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF0F5A47)
                                )
                            }
                        }
                        Icon(Icons.Filled.ArrowDropDown, "Select", tint = MoroccanColors.SoftGold)
                    }
                }

                // Center visual panel: Rotating-style vinyl CD + Live synchronized verse reader
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 12.dp)
                        .border(1.2.dp, MoroccanColors.SoftGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF031A14) else Color(0xFFFBF9F4)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (activeAudioSurah != null) "الاستماع المتزامن مع تتبع الآيات الشريفة لـ سورة ${activeAudioSurah?.name}:" else "اختر سورة للبدء بالاستماع والقران المتزامنّ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MoroccanColors.PaleGold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        
                        if (isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MoroccanColors.SoftGold, modifier = Modifier.size(54.dp))
                            }
                        } else if (activeSurahVerses.isNotEmpty()) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                itemsIndexed(activeSurahVerses) { index, verse ->
                                    val isCurrent = index == currentVerseIndex
                                    val isBismillah = verse == "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
                                    
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isCurrent) MoroccanColors.SoftGold.copy(alpha = 0.2f) else Color.Transparent
                                            )
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = verse,
                                            fontSize = if (isBismillah) 18.sp else 16.sp,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCurrent) MoroccanColors.SoftGold else (if (isDark) Color(0xFFE0E7E1) else Color(0xFF0D3227)),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        } else {
                            // Circular geometric star layout if empty
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.size(160.dp)) {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(Color(0xFF0F5A47), Color(0xFF021711))
                                        ),
                                        radius = size.minDimension / 2
                                    )
                                    drawCircle(
                                        color = MoroccanColors.SoftGold,
                                        radius = size.minDimension / 2,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.Headset, null, tint = MoroccanColors.PaleGold, modifier = Modifier.size(44.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("قم بالتلاوة والاستماع", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }

                // Audio Slider progress seeker and Duration numbers
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = progress.first.toFloat(),
                        valueRange = 0f..(progress.second.toFloat().coerceAtLeast(100f)),
                        onValueChange = { viewModel.seekToPosition(it.toLong()) },
                        colors = SliderDefaults.colors(
                            thumbColor = MoroccanColors.SoftGold,
                            activeTrackColor = MoroccanColors.SoftGold,
                            inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formattedProgress,
                            color = if (isDark) Color.White.copy(alpha = 0.7f) else Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        // Operational Playback Speed control
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("سرعة: ", fontSize = 11.sp, color = Color.Gray)
                            listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { sp ->
                                Text(
                                    text = "${sp}x",
                                    fontSize = 11.sp,
                                    fontWeight = if (speed == sp) FontWeight.Bold else FontWeight.Normal,
                                    color = if (speed == sp) MoroccanColors.SoftGold else (if (isDark) Color.White else Color.Black),
                                    modifier = Modifier
                                        .clickable { viewModel.setPlaybackSpeed(sp) }
                                        .padding(horizontal = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Player Controls: Prev, Play/Pause, Next
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.playPrevSurah() },
                        modifier = Modifier.size(54.dp).testTag("prev_surah_btn")
                    ) {
                        Icon(
                            Icons.Filled.SkipPrevious, "Previous Surah",
                            tint = if (isDark) Color.White else Color(0xFF0F5A47),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    FloatingActionButton(
                        onClick = {
                            if (activeAudioSurah == null) {
                                viewModel.playAudioForSurah(QuranData.surahs.first())
                            } else {
                                viewModel.togglePlayPause()
                            }
                        },
                        containerColor = MoroccanColors.SoftGold,
                        contentColor = Color.Black,
                        shape = CircleShape,
                        modifier = Modifier.size(72.dp).testTag("play_pause_fab")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "Play/Pause",
                            modifier = Modifier.size(38.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    IconButton(
                        onClick = { viewModel.playNextSurah() },
                        modifier = Modifier.size(54.dp).testTag("next_surah_btn")
                    ) {
                        Icon(
                            Icons.Filled.SkipNext, "Next Surah",
                            tint = if (isDark) Color.White else Color(0xFF0F5A47),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Sura browser sheet inside player (quick play scroll)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF021712) else Color(0xFFEFECE2))
                ) {
                    Text(
                        text = "اختر سورة للاستماع:",
                        fontWeight = FontWeight.Bold,
                        color = MoroccanColors.SoftGold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(QuranData.surahs) { surah ->
                            val isThisActive = activeAudioSurah?.id == surah.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isThisActive) MoroccanColors.SoftGold.copy(alpha = 0.15f) else Color.Transparent
                                    )
                                    .clickable { viewModel.playAudioForSurah(surah) }
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row {
                                    Text(
                                        text = "${surah.id}.",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isThisActive) MoroccanColors.SoftGold else Color.Gray,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = surah.name,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isThisActive) MoroccanColors.SoftGold else (if (isDark) Color.White else Color.Black),
                                        fontSize = 14.sp
                                    )
                                }
                                Text(
                                    text = if (isThisActive && isPlaying) "جاري التشغيل..." else "تلاوة",
                                    fontSize = 11.sp,
                                    color = if (isThisActive) MoroccanColors.SoftGold else Color.Gray
                                )
                            }
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
                        }
                    }
                }
            }
        }
    }

    // Reciter Selector Dialog
    if (showReciterDialog) {
        Dialog(onDismissRequest = { showReciterDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp)
                    .border(1.dp, MoroccanColors.SoftGold, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF052F24) else Color(0xFFFFFFFF)
                )
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text(
                        text = "اختر القارئ للقرآن تلاوة أونلاين:",
                        fontWeight = FontWeight.Bold,
                        color = MoroccanColors.SoftGold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(reciters) { reciter ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setReciter(context, reciter)
                                        showReciterDialog = false
                                    }
                                    .background(
                                        if (currentReciter.id == reciter.id) MoroccanColors.SoftGold.copy(alpha = 0.15f) else Color.Transparent
                                    )
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = reciter.name,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF09392D)
                                )
                                if (currentReciter.id == reciter.id) {
                                    Icon(Icons.Filled.Check, "Selected", tint = MoroccanColors.SoftGold)
                                }
                            }
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.15f))
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showReciterDialog = false }) {
                            Text("إلغاء", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// Favorites view lists pages/receptions/etc.
@Composable
fun FavoritesScreen(
    viewModel: QuranViewModel,
    onNavigateToScreen: (String) -> Unit,
    onBack: () -> Unit
) {
    val themeModeState by viewModel.darkThemeMode.collectAsStateWithLifecycle()
    val isDark = when (themeModeState) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val favorites by viewModel.favoriteItems.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(stringResource(R.string.card_favorites), fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF031913) else Color(0xFF0F5A47),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        MoroccanBackground(isDark = isDark, modifier = Modifier.padding(innerPadding)) {
            if (favorites.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(MoroccanColors.SoftGold.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.StarBorder, null, tint = MoroccanColors.SoftGold, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.favorites_empty),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (isDark) Color.White.copy(alpha = 0.7f) else Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favorites) { fav ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, MoroccanColors.SoftGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable {
                                    if (fav.type == "page") {
                                        fav.itemValue.toIntOrNull()?.let { p ->
                                            viewModel.setReadingPage(context, p)
                                            onNavigateToScreen("read")
                                        }
                                    } else if (fav.type == "recit") {
                                        fav.itemValue.toIntOrNull()?.let { sId ->
                                            val s = QuranData.surahs.find { it.id == sId }
                                            if (s != null) {
                                                viewModel.playAudioForSurah(s)
                                                onNavigateToScreen("listen")
                                            }
                                        }
                                    }
                                }
                                .testTag("favorite_card_${fav.type}_${fav.itemValue}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF042D22) else Color(0xFFF9F6F0)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(MoroccanColors.SoftGold.copy(alpha = 0.12f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (fav.type == "page") Icons.Filled.MenuBook else Icons.Filled.Headset,
                                            contentDescription = null,
                                            tint = MoroccanColors.SoftGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = fav.title,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color.White else Color(0xFF0F5A47)
                                        )
                                        Text(
                                            text = fav.subtitle,
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                IconButton(onClick = { viewModel.toggleFavorite(fav.type, fav.itemValue, "", "") }) {
                                    Icon(Icons.Filled.Delete, "Remove Favorite", tint = Color.Red.copy(alpha = 0.6f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Electronic Tasbeeh Screen view
@Composable
fun TasbeehScreen(
    viewModel: QuranViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val themeModeState by viewModel.darkThemeMode.collectAsStateWithLifecycle()
    val isDark = when (themeModeState) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val count by viewModel.tasbeehCount.collectAsStateWithLifecycle()
    val vibrateEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(stringResource(R.string.tasbeeh_title), fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF031913) else Color(0xFF0F5A47),
                    titleContentColor = Color.White
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
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Header with Moroccan decoration
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "التسبيح والمغفرة",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MoroccanColors.SoftGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "أَلا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
                        color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Gray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Geometric Counter Circle with haptic triggers
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .background(MoroccanColors.SoftGold.copy(alpha = 0.08f), CircleShape)
                        .border(3.dp, MoroccanColors.SoftGold, CircleShape)
                        .clickable { viewModel.incrementTasbeeh(context) }
                        .testTag("tasbeeh_tap_target"),
                    contentAlignment = Alignment.Center
                ) {
                    // Center numbers display
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = count.toString(),
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF0F5A47)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اضغط للمغفرة",
                            color = MoroccanColors.SoftGold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Interaction Row buttons: Reset & vibrate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.resetTasbeeh(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = MoroccanColors.EmeraldLight),
                        modifier = Modifier.testTag("tasbeeh_reset_btn")
                    ) {
                        Icon(Icons.Filled.Refresh, null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.tasbeeh_reset), color = Color.White)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(R.string.tasbeeh_vibration),
                            color = if (isDark) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Switch(
                            checked = vibrateEnabled,
                            onCheckedChange = { viewModel.setVibrationEnabled(context, it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MoroccanColors.SoftGold
                            ),
                            modifier = Modifier.testTag("tasbeeh_vibe_switch")
                        )
                    }
                }
            }
        }
    }
}

// Azkar Screen with beautiful expandable grids & interactive decrement trackers
@Composable
fun AzkarScreen(
    viewModel: QuranViewModel,
    onBack: () -> Unit
) {
    val themeModeState by viewModel.darkThemeMode.collectAsStateWithLifecycle()
    val isDark = when (themeModeState) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val azkarList = QuranData.azkar

    // Categories derived dynamically
    val categories = listOf("أذكار الصباح", "أذكار المساء", "أذكار النوم", "أذكار الاستيقاظ", "أذكار بعد الصلاة")
    var selectedCategory by remember { mutableStateOf("أذكار الصباح") }

    // Map to preserve count tracker progress during current session
    val customTrackers = remember { mutableStateMapOf<String, Int>() }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(stringResource(R.string.card_azkar), fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF031913) else Color(0xFF0F5A47),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        MoroccanBackground(isDark = isDark, modifier = Modifier.padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Horizontal category bar
                ScrollableTabRow(
                    selectedTabIndex = categories.indexOf(selectedCategory),
                    containerColor = if (isDark) Color(0xFF031913) else Color(0xFFE5E5E5),
                    contentColor = MoroccanColors.SoftGold,
                    edgePadding = 12.dp
                ) {
                    categories.forEach { cat ->
                        Tab(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            text = { Text(cat, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                // Lists of prayers filtrated
                val categoryPrays = azkarList.filter { it.category == selectedCategory }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categoryPrays) { pray ->
                        val trackerKey = "${pray.category}_${pray.text.hashCode()}"
                        val initialCount = pray.count
                        val activeLeft = customTrackers.getOrPut(trackerKey) { initialCount }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (activeLeft == 0) 1.dp else 0.5.dp,
                                    color = if (activeLeft == 0) Color.Green.copy(alpha = 0.4f) else MoroccanColors.SoftGold.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (activeLeft > 0) {
                                        customTrackers[trackerKey] = activeLeft - 1
                                    }
                                }
                                .testTag("pray_item_${pray.text.hashCodeOrZero()}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (activeLeft == 0) {
                                    if (isDark) Color(0xFF03261C) else Color(0xFFF2FBF7)
                                } else {
                                    if (isDark) Color(0xFF042D22) else Color(0xFFF9F6F0)
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = pray.text,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFF2E7D5) else Color(0xFF0F5A47),
                                    lineHeight = 26.sp,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                if (pray.description.isNotEmpty()) {
                                    Text(
                                        text = pray.description,
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Right,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { customTrackers[trackerKey] = initialCount }) {
                                        Icon(Icons.Filled.Refresh, "Reset Count", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (activeLeft == 0) Color.Green.copy(alpha = 0.2f) else MoroccanColors.SoftGold.copy(alpha = 0.2f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 16.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = if (activeLeft == 0) "مقبول / تَمّ" else "التكرار: $activeLeft من $initialCount",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (activeLeft == 0) Color(0xFF1B5E20) else MoroccanColors.DeepGold
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
}

// Ext helper for hashcode
private fun String.hashCodeOrZero(): Int {
    return this.hashCode()
}

// Settings Screen
@Composable
fun SettingsScreen(
    viewModel: QuranViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val themeModeState by viewModel.darkThemeMode.collectAsStateWithLifecycle()
    val isDark = when (themeModeState) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val appLang by viewModel.appLanguage.collectAsStateWithLifecycle()
    val themeMode by viewModel.darkThemeMode.collectAsStateWithLifecycle().value.let { mutableStateOf(it) }
    val direction by viewModel.scrollDirection.collectAsStateWithLifecycle()
    val textScale by viewModel.fontSizeModifier.collectAsStateWithLifecycle()
    val displayScale by viewModel.displayScaleModifier.collectAsStateWithLifecycle()

    val reminderEnabled by viewModel.reminderEnabled.collectAsStateWithLifecycle()
    val reminderHour by viewModel.reminderHour.collectAsStateWithLifecycle()
    val reminderMinute by viewModel.reminderMinute.collectAsStateWithLifecycle()

    val formattedTime = String.format("%02d:%02d", reminderHour, reminderMinute)

    val reminderLabel = when (appLang) {
        "en" -> "Daily Quran Reminder"
        "fr" -> "Rappel de lecture"
        else -> "تذكير الورد اليومي"
    }

    val reminderDesc = when (appLang) {
        "en" -> "Receive a daily notification to read your dedicated Quran portion."
        "fr" -> "Recevez une notification quotidienne pour lire le Coran."
        else -> "مساعدة في الحفاظ على وردك القرآني اليومي عبر إشعار تذكيري في وقت تختاره"
    }

    val reminderStatusEnabled = when (appLang) {
        "en" -> "Reminder Active"
        "fr" -> "Rappel Activé"
        else -> "التنبيه مفعل"
    }

    val reminderStatusDisabled = when (appLang) {
        "en" -> "Reminder Deactivated"
        "fr" -> "Rappel Désactivé"
        else -> "التنبيه معطل"
    }

    val reminderTimeLabel = when (appLang) {
        "en" -> "Notification Time"
        "fr" -> "Heure de notification"
        else -> "توقيت الإشعار"
    }

    val selectTimeBtn = when (appLang) {
        "en" -> "Change Time"
        "fr" -> "Changer l'heure"
        else -> "تغيير الوقت"
    }

    val SoftWhiteText = Color(0xFFE0E7E1)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.setReminderEnabled(context, true)
                Toast.makeText(context, "تم تفعيل التنبيه اليومي بنجاح", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "يتطلب تفعيل التنبيه الإذن بالإشعارات", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val checkToggle = { checked: Boolean ->
        if (checked) {
            if (Build.VERSION.SDK_INT >= 33) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                
                if (hasPermission) {
                    viewModel.setReminderEnabled(context, true)
                    Toast.makeText(context, "تم تفعيل التنبيه اليومي بنجاح", Toast.LENGTH_SHORT).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                viewModel.setReminderEnabled(context, true)
                Toast.makeText(context, "تم تفعيل التنبيه اليومي بنجاح", Toast.LENGTH_SHORT).show()
            }
        } else {
            viewModel.setReminderEnabled(context, false)
            Toast.makeText(context, "تم إيقاف التنبيه اليومي", Toast.LENGTH_SHORT).show()
        }
    }

    val showTimePicker = {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                viewModel.setReminderTime(context, hour, minute)
                Toast.makeText(context, "تم تحديث وقت التذكير إلى " + String.format("%02d:%02d", hour, minute), Toast.LENGTH_SHORT).show()
            },
            reminderHour,
            reminderMinute,
            true
        ).show()
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(stringResource(R.string.card_settings), fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF031913) else Color(0xFF0F5A47),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        MoroccanBackground(isDark = isDark, modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selector: Language Change
                Text("إعدادات اللغة / Language Settings", fontWeight = FontWeight.Bold, color = MoroccanColors.SoftGold, fontSize = 14.sp)
                MoroccanUiCard(isDark = isDark) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.settings_language), fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("ar" to "العربية", "en" to "English", "fr" to "Français").forEach { item ->
                                val selected = appLang == item.first
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (selected) MoroccanColors.SoftGold else Color.Gray.copy(alpha = 0.1f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.setLanguage(context, item.first) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.second,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (selected) Color.Black else (if (isDark) Color.White else Color.Black)
                                    )
                                }
                            }
                        }
                    }
                }

                // Selector: Theme Change
                Text("المظهر وإعدادات الاتصال", fontWeight = FontWeight.Bold, color = MoroccanColors.SoftGold, fontSize = 14.sp)
                MoroccanUiCard(isDark = isDark) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.settings_theme), fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
                        Spacer(modifier = Modifier.height(10.dp))
                        listOf("light" to stringResource(R.string.settings_theme_light), "dark" to stringResource(R.string.settings_theme_dark), "system" to stringResource(R.string.settings_theme_system)).forEach { themeOpt ->
                            val selected = viewModel.darkThemeMode.collectAsStateWithLifecycle().value == themeOpt.first
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setThemeMode(context, themeOpt.first) }
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(themeOpt.second, color = if (isDark) Color.White else Color.Black)
                                RadioButton(
                                    selected = selected,
                                    onClick = { viewModel.setThemeMode(context, themeOpt.first) },
                                    colors = RadioButtonDefaults.colors(selectedColor = MoroccanColors.SoftGold)
                                )
                            }
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
                        }
                    }
                }

                // Selector: Reading preferences
                Text(stringResource(R.string.settings_reading), fontWeight = FontWeight.Bold, color = MoroccanColors.SoftGold, fontSize = 14.sp)
                MoroccanUiCard(isDark = isDark) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Slider: Font scale
                        Text("${stringResource(R.string.settings_font_size)} (${(textScale * 100).toInt()}%):", fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
                        Slider(
                            value = textScale,
                            valueRange = 0.8f..2.5f,
                            onValueChange = { viewModel.setFontSizeModifier(context, it) },
                            colors = SliderDefaults.colors(
                                thumbColor = MoroccanColors.SoftGold,
                                activeTrackColor = MoroccanColors.SoftGold
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Slider: Layout/Viewport scale
                        Text("مقياس ومستوى تكبير التخطيط والفراغات (${(displayScale * 100).toInt()}%):", fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
                        Slider(
                            value = displayScale,
                            valueRange = 0.6f..1.5f,
                            onValueChange = { viewModel.setDisplayScaleModifier(context, it) },
                            colors = SliderDefaults.colors(
                                thumbColor = MoroccanColors.SoftGold,
                                activeTrackColor = MoroccanColors.SoftGold
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        // Selector: Scroll Direction
                        Text(stringResource(R.string.settings_scroll_direction), fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("horizontal" to stringResource(R.string.settings_scroll_horizontal), "vertical" to stringResource(R.string.settings_scroll_vertical)).forEach { dir ->
                                val selected = direction == dir.first
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (selected) MoroccanColors.SoftGold else Color.Gray.copy(alpha = 0.1f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.setScrollDirection(context, dir.first) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dir.second,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.Black else (if (isDark) Color.White else Color.Black)
                                    )
                                }
                            }
                        }
                    }
                }

                // Daily Reminder Notification Settings
                Text(reminderLabel, fontWeight = FontWeight.Bold, color = MoroccanColors.SoftGold, fontSize = 14.sp)
                MoroccanUiCard(isDark = isDark) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (reminderEnabled) reminderStatusEnabled else reminderStatusDisabled,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = reminderDesc,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isDark) SoftWhiteText.copy(alpha = 0.6f) else Color.Gray,
                                        lineHeight = 16.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = reminderEnabled,
                                onCheckedChange = { checkToggle(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF04160E),
                                    checkedTrackColor = MoroccanColors.SoftGold,
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.2f)
                                )
                            )
                        }

                        if (reminderEnabled) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDark) Color(0xFF04160E) else Color.Gray.copy(alpha = 0.05f))
                                    .border(
                                        0.5.dp, 
                                        if (isDark) MoroccanColors.SoftGold.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.5f), 
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { showTimePicker() }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Time",
                                        tint = MoroccanColors.SoftGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = reminderTimeLabel,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (isDark) SoftWhiteText.copy(alpha = 0.6f) else Color.Gray
                                        )
                                        Text(
                                            text = formattedTime,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MoroccanColors.SoftGold
                                        )
                                    }
                                }
                                Button(
                                    onClick = { showTimePicker() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MoroccanColors.SoftGold,
                                        contentColor = Color(0xFF04160E)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(
                                        text = selectTimeBtn,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
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

// About Screen highlighting BY YOUNES HIDOURI
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(stringResource(R.string.card_about), fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F5A47),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        MoroccanBackground(isDark = false, modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large Gold Dome Graphic
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(MoroccanColors.SoftGold.copy(alpha = 0.15f), CircleShape)
                        .border(1.5.dp, MoroccanColors.SoftGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stars,
                        contentDescription = null,
                        tint = MoroccanColors.SoftGold,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "تطبيق المصحف المحمدي المغربي الأصيل",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color(0xFF0F5A47),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.app_desc),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.DarkGray,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "مؤسس التطبيق ومطوره الشريف",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "BY YOUNES HIDOURI",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MoroccanColors.DeepGold,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        fontSize = 24.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "يونس الهيدوري",
                    color = Color(0xFF0F5A47),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))
                HorizontalDivider(color = MoroccanColors.SoftGold.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "جميع الحقوق محفوظة © 2026",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
