package com.swiftquantum.quantumcareer.presentation.ui.screen

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.swiftquantum.quantumcareer.R
import com.swiftquantum.quantumcareer.presentation.ui.theme.QuantumColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Language enum matching iOS
enum class Language(
    val code: String,
    val flag: String,
    val displayName: String
) {
    ENGLISH("en", "🇺🇸", "English"),
    KOREAN("ko", "🇰🇷", "한국어"),
    JAPANESE("ja", "🇯🇵", "日本語"),
    CHINESE("zh", "🇨🇳", "中文"),
    GERMAN("de", "🇩🇪", "Deutsch")
}

data class FeatureItem(
    val icon: ImageVector,
    val titleRes: Int,
    val descriptionRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    var showSplash by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf(getCurrentLanguage()) }

    // 4 pages: Language -> Welcome -> Features -> GetStarted
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    // Splash animation - 2.5 seconds like iOS
    LaunchedEffect(Unit) {
        delay(2500)
        showSplash = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0F27), // iOS: Color(red: 0.04, green: 0.06, blue: 0.15)
                        Color(0xFF050814)  // iOS: Color(red: 0.02, green: 0.03, blue: 0.08)
                    )
                )
            )
    ) {
        AnimatedVisibility(
            visible = showSplash,
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            SplashContent()
        }

        AnimatedVisibility(
            visible = !showSplash,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Page indicators at top (iOS style)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index)
                                        QuantumColors.Primary
                                    else
                                        Color.White.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                // Pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { page ->
                    when (page) {
                        0 -> LanguageSelectionPage(
                            selectedLanguage = selectedLanguage,
                            onLanguageSelected = { language ->
                                selectedLanguage = language
                                setAppLanguage(context, language)
                            },
                            onContinue = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            }
                        )
                        1 -> WelcomePage(
                            onBack = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            },
                            onNext = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(2)
                                }
                            }
                        )
                        2 -> FeaturesPage(
                            onBack = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            onNext = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(3)
                                }
                            }
                        )
                        3 -> GetStartedPage(
                            onBack = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(2)
                                }
                            },
                            onGetStarted = onComplete
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SplashContent() {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    var splashAlpha by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        // Animate in
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(800)
        ) { value, _ -> splashAlpha = value }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .alpha(splashAlpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // App Icon (from mipmap)
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher),
            contentDescription = "QuantumCareer",
            modifier = Modifier
                .size(140.dp)
                .scale(scale)
                .clip(RoundedCornerShape(28.dp))
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = QuantumColors.Primary.copy(alpha = 0.4f),
                    spotColor = QuantumColors.Primary.copy(alpha = 0.4f)
                )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // App Name with gradient
        Text(
            text = "QuantumCareer",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White // Will use gradient in production
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tagline
        Text(
            text = stringResource(R.string.onboarding_splash_tagline),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Feature checkmarks (SwiftQuantum style)
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SplashFeatureRow(
                icon = Icons.Default.Psychology,
                text = stringResource(R.string.onboarding_splash_feature1)
            )
            SplashFeatureRow(
                icon = Icons.Default.Verified,
                text = stringResource(R.string.onboarding_splash_feature2)
            )
            SplashFeatureRow(
                icon = Icons.Default.Work,
                text = stringResource(R.string.onboarding_splash_feature3)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Version info
        Text(
            text = "v5.8.0 | ${stringResource(R.string.onboarding_splash_edition)}",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun SplashFeatureRow(
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = QuantumColors.Primary,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun LanguageSelectionPage(
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.5f))

        // App Logo (smaller)
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher),
            contentDescription = "QuantumCareer",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(20.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "QuantumCareer",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Language Selection Header
        Text(
            text = stringResource(R.string.onboarding_select_language),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.onboarding_select_language_subtitle),
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Language Options
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Language.entries.forEach { language ->
                val isSelected = selectedLanguage == language

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected)
                                QuantumColors.Primary.copy(alpha = 0.2f)
                            else
                                Color.White.copy(alpha = 0.05f)
                        )
                        .then(
                            if (isSelected)
                                Modifier.border(1.5.dp, QuantumColors.Primary, RoundedCornerShape(12.dp))
                            else
                                Modifier
                        )
                        .clickable { onLanguageSelected(language) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = language.flag,
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = language.displayName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = QuantumColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Continue Button
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = QuantumColors.Primary
            )
        ) {
            Text(
                text = stringResource(R.string.onboarding_continue),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun WelcomePage(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // App Logo
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher),
            contentDescription = "QuantumCareer",
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(28.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "QuantumCareer",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Welcome Title
        Text(
            text = stringResource(R.string.onboarding_welcome_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Welcome Subtitle
        Text(
            text = stringResource(R.string.onboarding_welcome_subtitle),
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.2f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.onboarding_back),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = QuantumColors.Primary
                )
            ) {
                Text(
                    text = stringResource(R.string.onboarding_next),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FeaturesPage(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val features = listOf(
        FeatureItem(
            Icons.Default.Psychology,
            R.string.onboarding_feature1_title,
            R.string.onboarding_feature1_desc
        ),
        FeatureItem(
            Icons.Default.Verified,
            R.string.onboarding_feature2_title,
            R.string.onboarding_feature2_desc
        ),
        FeatureItem(
            Icons.Default.Work,
            R.string.onboarding_feature3_title,
            R.string.onboarding_feature3_desc
        ),
        FeatureItem(
            Icons.Default.TrendingUp,
            R.string.onboarding_feature4_title,
            R.string.onboarding_feature4_desc
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.onboarding_features_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Feature Cards
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            features.forEach { feature ->
                FeatureCard(
                    icon = feature.icon,
                    title = stringResource(feature.titleRes),
                    description = stringResource(feature.descriptionRes)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.2f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.onboarding_back),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = QuantumColors.Primary
                )
            ) {
                Text(
                    text = stringResource(R.string.onboarding_next),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(QuantumColors.Primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = QuantumColors.Primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.6f),
                maxLines = 2,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun GetStartedPage(
    onBack: () -> Unit,
    onGetStarted: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Success Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(QuantumColors.Primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = QuantumColors.Primary,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = stringResource(R.string.onboarding_get_started_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subtitle
        Text(
            text = stringResource(R.string.onboarding_get_started_subtitle),
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.2f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.onboarding_back),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = QuantumColors.Primary
                )
            ) {
                Text(
                    text = stringResource(R.string.onboarding_get_started_button),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

private fun getCurrentLanguage(): Language {
    val locale = AppCompatDelegate.getApplicationLocales()[0]
    return when (locale?.language) {
        "ko" -> Language.KOREAN
        "ja" -> Language.JAPANESE
        "zh" -> Language.CHINESE
        "de" -> Language.GERMAN
        else -> Language.ENGLISH
    }
}

private fun setAppLanguage(context: Context, language: Language) {
    val localeList = LocaleListCompat.forLanguageTags(language.code)
    AppCompatDelegate.setApplicationLocales(localeList)
}

private val EaseInOutCubic = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
