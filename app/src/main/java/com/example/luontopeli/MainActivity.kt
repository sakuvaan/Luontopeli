// 📁 MainActivity.kt
package com.example.luontopeli

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.luontopeli.ui.navigation.LuontopeliBottomBar
import com.example.luontopeli.ui.navigation.LuontopeliNavHost
import com.example.luontopeli.ui.theme.LuontopeliTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Sovelluksen pääaktiviteetti ja ainoa Activity (Single Activity -arkkitehtuuri).
 *
 * @AndroidEntryPoint mahdollistaa Hilt-riippuvuusinjektion tässä aktiviteetissa.
 * Kaikki näkymät toteutetaan Jetpack Compose -komponentteina ja navigointi
 * hoidetaan Navigation Compose -kirjastolla.
 */
@AndroidEntryPoint
// 📁 app/src/main/java/com/example/luontopeli/MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Asenna splash screen ENNEN super.onCreate()-kutsua
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            LuontopeliTheme {
                LuontopeliApp()
            }
        }
    }
}
@Composable
fun LuontopeliApp() {
    // Luodaan navigointikontrolleri, joka muistaa tilan konfiguraatiomuutoksissa
    val navController = rememberNavController()

    Scaffold(
        // Alanäkymäpalkki (bottom navigation bar) 4 välilehdellä
        bottomBar = {
            LuontopeliBottomBar(navController = navController)
        }
    ) { innerPadding ->
        // Navigointisisältö, joka reagoi alanäkymäpalkin valintoihin
        LuontopeliNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}