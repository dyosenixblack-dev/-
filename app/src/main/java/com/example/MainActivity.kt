package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.QuranDatabase
import com.example.data.QuranRepository
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database Context
        val database = QuranDatabase.getDatabase(applicationContext)
        val quranDao = database.quranDao()
        val repository = QuranRepository(quranDao)

        setContent {
            // Instantiate viewmodel with Factory safely inside content scope
            val viewModel: QuranViewModel = viewModel(
                factory = QuranViewModelFactory(repository)
            )

            // Re-sync dynamic settings
            LaunchedEffect(Unit) {
                viewModel.loadSettings(applicationContext)
            }

            // Bind dynamic language
            val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
            
            // Dynamically reload resources configuration on language swap
            val resources = resources
            val configuration = resources.configuration
            val locale = java.util.Locale(appLanguage)
            java.util.Locale.setDefault(locale)
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)

            // Bind dynamic dark theme
            val themeMode by viewModel.darkThemeMode.collectAsStateWithLifecycle()
            val isDarkTheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("splash") {
                        SplashScreen(
                            isDark = isDarkTheme,
                            onAnimationComplete = {
                                navController.navigate("home") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(
                        "home",
                        enterTransition = {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300))
                        },
                        exitTransition = {
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300))
                        },
                        popEnterTransition = {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
                        },
                        popExitTransition = {
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
                        }
                    ) {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateTo = { route -> navController.navigate(route) }
                        )
                    }

                    composable("read") {
                        ReadQuranScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("listen") {
                        ListenQuranScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("favorites") {
                        FavoritesScreen(
                            viewModel = viewModel,
                            onNavigateToScreen = { route -> navController.navigate(route) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("tasbeeh") {
                        TasbeehScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("azkar") {
                        AzkarScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("about") {
                        AboutScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("search") {
                        SearchScreen(
                            viewModel = viewModel,
                            onNavigateTo = { route -> navController.navigate(route) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("stats") {
                        StatsScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("khatma") {
                        KhatmaScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("qibla") {
                        QiblaScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("prayers") {
                        PrayersScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("duas") {
                        DuasScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
