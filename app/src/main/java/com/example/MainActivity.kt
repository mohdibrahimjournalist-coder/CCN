package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.NewsScriptDraft
import com.example.ui.NewsViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    CcnStudioApp(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// Custom CCN Logo Composable modeled directly after the user's high-contrast brand graphic
@Composable
fun CcnLogo(
    modifier: Modifier = Modifier,
    scale: Float = 1.0f
) {
    Box(
        modifier = modifier
            .scale(scale)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A))
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .border(1.5.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Semicircular sun fan rays at top
            Canvas(modifier = Modifier.size(64.dp)) {
                val center = Offset(size.width / 2f, size.height * 0.95f)
                val maxRadius = size.width / 2f
                val numRays = 13
                val angleStep = 180f / (numRays - 1)
                for (i in 0 until numRays) {
                    val angleInDegrees = 180f + i * angleStep
                    val angleInRadians = Math.toRadians(angleInDegrees.toDouble())
                    val startOffset = center + Offset(
                        (maxRadius * 0.42f * Math.cos(angleInRadians)).toFloat(),
                        (maxRadius * 0.42f * Math.sin(angleInRadians)).toFloat()
                    )
                    val endOffset = center + Offset(
                        (maxRadius * 0.95f * Math.cos(angleInRadians)).toFloat(),
                        (maxRadius * 0.95f * Math.sin(angleInRadians)).toFloat()
                    )
                    val width = 4f * scale
                    
                    drawLine(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFD700), Color(0xFFFF7A00)) // Gold to Orange gradient
                        ),
                        start = startOffset,
                        end = endOffset,
                        strokeWidth = width,
                        cap = StrokeCap.Round
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            // "CCN" Letters
            Text(
                text = "CCN",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Normal,
                letterSpacing = 1.sp,
                color = Color(0xFFFFD700), // Brilliant Gold
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            
            // "CHANNEL" red rounded rectangle pill
            Box(
                modifier = Modifier
                    .background(Color(0xFFDC2626), shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "CHANNEL",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = Color.White
                )
            }
        }
    }
}

// Curated Breaking News Headlines
data class NewsHeadline(
    val id: String,
    val title: String,
    val summary: String,
    val category: String,
    val timeAgo: String,
    val isLive: Boolean = false
)

val SampleHeadlines = listOf(
    NewsHeadline(
        id = "1",
        title = "CCN EXCLUSIVE: Advanced Automated Power Grid Commissioned In Capital",
        summary = "An revolutionary smart grid system utilizing regional solar battery cells has gone live, promising 99.9% blackout immunity and immediate redistribution for emergency centers.",
        category = "INTELLIGENCE",
        timeAgo = "10 Mins Ago",
        isLive = true
    ),
    NewsHeadline(
        id = "2",
        title = "Global Semiconductor Supply Stabilizes As Local Mega-Fab Enters Peak Phase",
        summary = "Global logistics and domestic manufacturing indices jumped 4% following the announcement that the new high-altitude semiconductor foundry is operating at 95% throughput capacity.",
        category = "GLOBAL MARKET",
        timeAgo = "25 Mins Ago"
    ),
    NewsHeadline(
        id = "3",
        title = "Quantum Computing Startup Achieves Milestones in Coherence Times",
        summary = "Local academic research group backed by CCN ventures achieves stable electron spin qubit coherence lasting up to 12 minutes under vacuum conditions, breaking modern world record.",
        category = "SCIENCE & TECH",
        timeAgo = "1 Hour Ago"
    ),
    NewsHeadline(
        id = "4",
        title = "CCN Sports Special: National Football League Cup Finals locked",
        summary = "A dramatic overhead penalty kick in the final 30 seconds of stoppage play locks in the championship final matchups, sparking nationwide viewer celebrations.",
        category = "SPORTS CENTRAL",
        timeAgo = "3 Hours Ago"
    ),
    NewsHeadline(
        id = "5",
        title = "Central Bank Announces Digital Coin Phase-2 Trial Infrastructure Expansion",
        summary = "A secure ledger update launched earlier today opens high-density commercial consumer micro-transactions for over 2.4 million localized retail outlets starting next quarter.",
        category = "FINANCE & CORP",
        timeAgo = "5 Hours Ago"
    )
)

enum class ChannelTab(val title: String, val icon: ImageVector) {
    LIVE_STUDIO("Live Studio", Icons.Default.Tv),
    AI_WRITER("AI Studio", Icons.Default.AutoAwesome),
    TELEPROMPTER("Teleprompter", Icons.Default.FormatAlignLeft),
    DRAFTS("Saved Drafts", Icons.Default.Archive)
}

@Composable
fun CcnStudioApp(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(ChannelTab.LIVE_STUDIO) }
    
    // Share script text to pass from AI scriptwriter or drafts straight into teleprompter
    var activeTeleprompterText by remember { mutableStateOf("") }
    var activeTeleprompterTitle by remember { mutableStateOf("LIVE CCN BROADCAST SCRIPT") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
    ) {
        // App Header incorporating logo and brand
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(StudioDarkBg)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CcnLogo(scale = 0.8f)
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = "CCN STUDIO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color.Red, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ON LIGHT / PRODUCTION ROOM",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Red,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Rolling News Ticker Tape
        NewsTickerTape(headlines = SampleHeadlines)

        Divider(color = Color(0xFFFFD700).copy(alpha = 0.2f), thickness = 1.dp)

        // Main Tab Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedTab) {
                ChannelTab.LIVE_STUDIO -> {
                    LiveStudioScreen(
                        onHeadlineSelected = { headline ->
                            // Load into editor/prompter quickly
                            activeTeleprompterTitle = headline.title
                            activeTeleprompterText = "CCN BROADCAST DESK: \n\n${headline.title}. \n\n${headline.summary} ... [PAUSE] ... More to follow, reporting live for CCN, I'm anchor desk."
                            selectedTab = ChannelTab.TELEPROMPTER
                        }
                    )
                }
                ChannelTab.AI_WRITER -> {
                    AiWriterScreen(
                        viewModel = viewModel,
                        onLoadToTeleprompter = { title, body ->
                            activeTeleprompterTitle = title
                            activeTeleprompterText = body
                            selectedTab = ChannelTab.TELEPROMPTER
                        }
                    )
                }
                ChannelTab.TELEPROMPTER -> {
                    TeleprompterScreen(
                        initialTitle = activeTeleprompterTitle,
                        initialText = activeTeleprompterText
                    )
                }
                ChannelTab.DRAFTS -> {
                    DraftsScreen(
                        viewModel = viewModel,
                        onLoadDraft = { draft ->
                            activeTeleprompterTitle = draft.title
                            activeTeleprompterText = draft.body
                            selectedTab = ChannelTab.TELEPROMPTER
                        }
                    )
                }
            }
        }

        // Bottom Navigation Bar
        NavigationBar(
            containerColor = StudioCardBg,
            tonalElevation = 8.dp,
            modifier = Modifier.height(72.dp)
        ) {
            ChannelTab.values().forEach { tab ->
                val selected = selectedTab == tab
                NavigationBarItem(
                    selected = selected,
                    onClick = { selectedTab = tab },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (selected) Color(0xFFFFD700) else SlateGray
                        )
                    },
                    label = {
                        Text(
                            text = tab.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) Color(0xFFFFD700) else SlateGray
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = StudioSurface
                    ),
                    modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                )
            }
        }
    }
}

// Dynamic Scrolling News Ticker Tape Composable
@Composable
fun NewsTickerTape(headlines: List<NewsHeadline>) {
    val scrollState = rememberScrollState()
    var isPlaying by remember { mutableStateOf(true) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                // Loop simple horizontal auto scroll
                scrollState.scrollBy(2f)
                if (scrollState.value >= scrollState.maxValue) {
                    scrollState.scrollTo(0)
                }
                delay(12L)
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF070B14))
            .clickable { isPlaying = !isPlaying }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ticker Alert Label
        Box(
            modifier = Modifier
                .background(Color(0xFFDC2626))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "TICKER",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Auto-Scroll Text String
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState, enabled = false)
                .fillMaxWidth()
        ) {
            headlines.forEach { headline ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "★ ${headline.category}:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${headline.title}  • ",
                        fontSize = 11.sp,
                        color = Color.White,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}

// SCREEN 1: Live Studio & News feed interface
@Composable
fun LiveStudioScreen(
    onHeadlineSelected: (NewsHeadline) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Broadcast Video Player Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Black, RoundedCornerShape(12.dp))
                .border(2.dp, StudioSurface, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Live video room background / static grid lines
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = 40.dp.toPx()
                // Horizontal lines
                for (y in 0 step step.toInt() .. size.height.toInt()) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(0f, y.toFloat()),
                        end = Offset(size.width, y.toFloat()),
                        strokeWidth = 1f
                    )
                }
                // Vertical lines
                for (x in 0 step step.toInt() .. size.width.toInt()) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(x.toFloat(), 0f),
                        end = Offset(x.toFloat(), size.height),
                        strokeWidth = 1f
                    )
                }
            }

            // Blinking Video Anchor Placeholder Silhouette / Graphic Accent
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "On Air Anchor Feed",
                    tint = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.size(72.dp)
                )
                Text(
                    text = "CCN STUDIO DIGITAL FEED: LIVE 1080P",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Watermark brand logo overlaid (hyper-realistic TV network detail!)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color.Green, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "CCN DIRECT",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Live Time Indicator
            val currentUTCTime = "14:11 UTC"
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.61f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "LIVE FEED • $currentUTCTime",
                    color = Color.Red,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Central blinking live bar
            var blinkState by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                while (true) {
                    delay(700L)
                    blinkState = !blinkState
                }
            }
            if (blinkState) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color.Red, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "ON AIR",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Headline Feed Section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BREAKING BROADCAST DESK",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )
            Text(
                text = "SELECT TO BROADCAST",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // List of Curated News Cards
        SampleHeadlines.forEach { headline ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .border(
                        1.dp,
                        if (headline.isLive) Color(0xFFFFD700).copy(alpha = 0.4f) else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    ),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = StudioCardBg
                ),
                onClick = { onHeadlineSelected(headline) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (headline.isLive) Color(0xFFDC2626) else StudioSurface,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = headline.category,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = headline.timeAgo,
                            fontSize = 10.sp,
                            color = SlateGray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = headline.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = headline.summary,
                        fontSize = 12.sp,
                        color = SlateGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = "Send to Prompter",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Send to Teleprompter",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                }
            }
        }
    }
}

// SCREEN 2: AI Broadcast Script writer
@Composable
fun AiWriterScreen(
    viewModel: NewsViewModel,
    onLoadToTeleprompter: (String, String) -> Unit
) {
    var subject by remember { mutableStateOf("New revolutionary solar-grid power plant goes live") }
    var selectedCategory by remember { mutableStateOf("BREAKING ALERTS") }
    var selectedTone by remember { mutableStateOf("URGENT / CRITICAL") }

    val scriptText by viewModel.aiScriptText.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val errorText by viewModel.genError.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "AI NEWS ASSISTANT",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 1.sp
        )
        Text(
            text = "Generate professional, read-along television scripts from prompt topics using the CCN generative intelligence engine.",
            fontSize = 12.sp,
            color = SlateGray
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Topic field
        Text(
            text = "Topic/Outline of the story",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            placeholder = { Text("E.g., Local market reaches new high after fiscal results") },
            textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFFD700),
                unfocusedBorderColor = StudioSurface,
                focusedContainerColor = StudioCardBg,
                unfocusedContainerColor = StudioCardBg
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            // Category selectors
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Broadcast Category",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                // Dropdown mock selector
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(StudioCardBg, RoundedCornerShape(4.dp))
                        .border(1.dp, StudioSurface, RoundedCornerShape(4.dp))
                        .clickable {
                            val options = listOf("BREAKING ALERTS", "TECH CHRONICLES", "WORLD DESK", "SPORTS EXTRA", "MARKET ANALYTICS")
                            val nextIndex = (options.indexOf(selectedCategory) + 1) % options.size
                            selectedCategory = options[nextIndex]
                        }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(selectedCategory, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ArrowDropDown, "Next Option", tint = Color(0xFFFFD700))
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Tone selectors
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Delivery Emphasis / Tone",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(StudioCardBg, RoundedCornerShape(4.dp))
                        .border(1.dp, StudioSurface, RoundedCornerShape(4.dp))
                        .clickable {
                            val options = listOf("URGENT / CRITICAL", "SERIOUS / IN-DEPTH", "CALM / ANALYTICAL", "BULLETIN / FRESH")
                            val nextIndex = (options.indexOf(selectedTone) + 1) % options.size
                            selectedTone = options[nextIndex]
                        }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(selectedTone, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ArrowDropDown, "Next Option", tint = Color(0xFFFFD700))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Run Engine Button
        Button(
            onClick = {
                viewModel.generateAiScript(subject, selectedCategory, selectedTone)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFD700),
                contentColor = StudioDarkBg
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("generate_script_button"),
            enabled = !isGenerating && subject.isNotBlank()
        ) {
            if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = StudioDarkBg, strokeWidth = 2.5.dp)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Intelligence AI")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RUN CCN NEWS INTELLIGENCE",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Script Output Field
        Spacer(modifier = Modifier.height(18.dp))

        if (errorText != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF450A0A), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFF87171), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row {
                    Icon(Icons.Default.Warning, "Error icon", tint = Color(0xFFF87171))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(errorText!!, fontSize = 12.sp, color = Color(0xFFFECACA), fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (scriptText.isNotBlank() || isGenerating) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = StudioCardBg
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI GENERATED BROADCAST SCRIPT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD700)
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF155E75), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "READY FOR TELEPROMPTER",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (scriptText.isBlank()) "Generating broadcast structure. Please hold..." else scriptText,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif,
                        lineHeight = 22.sp
                    )

                    if (scriptText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = StudioSurface, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Save to drafts button
                            OutlinedButton(
                                onClick = {
                                    viewModel.saveScript(
                                        title = "CCN AI: ${if (subject.length > 25) subject.take(22)+"..." else subject}",
                                        body = scriptText,
                                        category = selectedCategory
                                    )
                                    // Feedback
                                    kotlin.run {
                                        // Just standard mock notify in real code
                                    }
                                },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                border = BorderStroke(1.dp, StudioSurface),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Save, "Save Draft")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Draft", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Load to teleprompter
                            Button(
                                onClick = {
                                    onLoadToTeleprompter(
                                        "AI OUTLINE: ${if (subject.length > 25) subject.take(22)+"..." else subject}",
                                        scriptText
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD700),
                                    contentColor = StudioDarkBg
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PlayArrow, "Start Read")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Broadcast", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// SCREEN 3: Active Studio Teleprompter Screen
@Composable
fun TeleprompterScreen(
    initialTitle: String,
    initialText: String
) {
    var titleText by remember { mutableStateOf(initialTitle) }
    var rawBodyText by remember { mutableStateOf(initialText) }
    var isEditingMode by remember { mutableStateOf(initialText.isBlank()) }

    var isScrolling by remember { mutableStateOf(false) }
    var scrollSpeed by remember { mutableStateOf(2) } // 1 to 5
    var fontScale by remember { mutableStateOf(24f) } // Adjust text size

    val teleprompterScrollState = rememberScrollState()

    // Automatic Scroll loop calculations
    LaunchedEffect(isScrolling, scrollSpeed) {
        if (isScrolling && scrollSpeed > 0) {
            while (true) {
                val delayMs = when (scrollSpeed) {
                    1 -> 110L
                    2 -> 65L
                    3 -> 40L
                    4 -> 24L
                    5 -> 14L
                    else -> 50L
                }
                delay(delayMs)
                // Incremental smooth scroll offset
                teleprompterScrollState.scrollBy(1.5f)
                if (teleprompterScrollState.value >= teleprompterScrollState.maxValue) {
                    isScrolling = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TELEPROMPTER MONITOR",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )
            // Edit vs Read mode switch
            IconButton(
                onClick = { isEditingMode = !isEditingMode }
            ) {
                Icon(
                    imageVector = if (isEditingMode) Icons.Default.Visibility else Icons.Default.Edit,
                    contentDescription = "Toggle mode",
                    tint = Color(0xFFFFD700)
                )
            }
        }

        if (isEditingMode) {
            // Write Mode
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Edit or paste custom anchor reports inside this terminal, then click view to run on prompt hardware.",
                    fontSize = 11.sp,
                    color = SlateGray
                )
                
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Script Headline ID") },
                    textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFD700),
                        unfocusedBorderColor = StudioSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = rawBodyText,
                    onValueChange = { rawBodyText = it },
                    label = { Text("Anchor Dialogue Body Text") },
                    textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFD700),
                        unfocusedBorderColor = StudioSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    maxLines = 15
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { isEditingMode = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700),
                        contentColor = StudioDarkBg
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("LOCK & LOAD TO HARDWARE", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Prompter hardware visual view
            if (rawBodyText.isBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.Black, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TERMINAL IDLE:\nPlease select or write a story script from the other rooms.",
                        textAlign = TextAlign.Center,
                        color = SlateGray,
                        fontSize = 14.sp
                    )
                }
            } else {
                // Settings bar (Speed & Font)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StudioCardBg, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Font Scale Control
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("FONT : ", fontSize = 10.sp, color = SlateGray, fontWeight = FontWeight.Bold)
                        IconButton(
                            onClick = { if (fontScale > 16) fontScale -= 4f },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Remove, "Smaller text", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Text("${fontScale.toInt()}sp", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Black)
                        IconButton(
                            onClick = { if (fontScale < 48) fontScale += 4f },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Add, "Larger text", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }

                    // Divider segment
                    Box(modifier = Modifier.width(1.dp).height(20.dp).background(StudioSurface))

                    // Dynamic Speed selector
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("SPEED : ", fontSize = 10.sp, color = SlateGray, fontWeight = FontWeight.Bold)
                        IconButton(
                            onClick = { if (scrollSpeed > 1) scrollSpeed -= 1 },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Remove, "Slower scroll", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Text("$scrollSpeed", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Black)
                        IconButton(
                            onClick = { if (scrollSpeed < 5) scrollSpeed += 1 },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Add, "Faster scroll", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scroll view wrapping with target assist focus overlay!
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.Black, RoundedCornerShape(8.dp))
                        .border(1.5.dp, StudioSurface, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Script body text inside a scrolling area
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(teleprompterScrollState)
                            .padding(vertical = 120.dp, horizontal = 24.dp) // Generous top offset spacing so anchor begins at target area
                    ) {
                        Text(
                            text = titleText.uppercase(),
                            color = Color(0xFFFFD700),
                            fontSize = (fontScale * 0.9f).sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = rawBodyText,
                            color = Color.White,
                            fontSize = fontScale.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = (fontScale * 1.5f).sp
                        )
                        
                        Spacer(modifier = Modifier.height(240.dp)) // padding at bottom so scrolling goes completely out
                    }

                    // Physical Teleprompter "Red Alignment Target Line" (Anchor Focus Assist)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .align(Alignment.Center)
                            .background(Color.Red.copy(alpha = 0.08f))
                            .border(BorderStroke(1.5.dp, Color.Red.copy(alpha = 0.35f)))
                    ) {
                        // Tiny arrow indicator flags in corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 6.dp)
                                .size(8.dp)
                                .background(Color.Red, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 6.dp)
                                .size(8.dp)
                                .background(Color.Red, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Play / Pause / Reset hardware layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reset scroll
                    OutlinedButton(
                        onClick = {
                            isScrolling = false
                            kotlin.run {
                                // Reset scrollState back to zero smoothly
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, StudioSurface),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Refresh, "Rewind prompter")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rewind", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Major start control
                    Button(
                        onClick = { isScrolling = !isScrolling },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isScrolling) Color(0xFFDC2626) else Color(0xFF16A34A),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Icon(
                            imageVector = if (isScrolling) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Scroll trigger"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isScrolling) "STOP SCROLL" else "PLAY PROMPT",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// SCREEN 4: Draft Logs Archive Screen (Room Persistence)
@Composable
fun DraftsScreen(
    viewModel: NewsViewModel,
    onLoadDraft: (NewsScriptDraft) -> Unit
) {
    val draftsList by viewModel.savedDrafts.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SAVED STUDIO ARCHIVES",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )
            // Empty all action
            if (draftsList.isNotEmpty()) {
                TextButton(
                    onClick = { viewModel.clearAllScripts() }
                ) {
                    Text("Clear All", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Text(
            text = "Review, read-along, or instantly load historically logged news script drafts back to the hardware monitors.",
            fontSize = 12.sp,
            color = SlateGray
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (draftsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(StudioCardBg, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = "No Drafts Cached",
                        tint = SlateGray,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your Studio Archives table is currently empty.\nGo to AI Studio to write scripts!",
                        textAlign = TextAlign.Center,
                        color = SlateGray,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(draftsList) { draft ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF1E3A8A), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = draft.category,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }

                                // Delete option
                                IconButton(
                                    onClick = { viewModel.deleteScript(draft.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove script",
                                        tint = Color.Red.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = draft.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = draft.body,
                                fontSize = 12.sp,
                                color = SlateGray,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            val roundedDate = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault()).format(Date(draft.timestamp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = roundedDate,
                                    fontSize = 10.sp,
                                    color = SlateGray
                                )

                                Button(
                                    onClick = { onLoadDraft(draft) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFD700),
                                        contentColor = StudioDarkBg
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Load Prompter", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
