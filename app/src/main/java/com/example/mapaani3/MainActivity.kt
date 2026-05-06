package com.example.mapaani3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.mapaani3.ui.theme.MapaAni3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapaAni3Theme {
                var currentScreen by remember { mutableStateOf("splash") }

                when (currentScreen) {
                    "splash" -> {
                        SplashScreen(onNavigateToMain = {
                            currentScreen = "onboarding"
                        })
                    }
                    "onboarding" -> {
                        OnboardingScreen(onFinished = {
                            currentScreen = "login_selection"
                        })
                    }
                    "login_selection" -> {
                        LoginSelectionScreen(
                            onFarmerClick = { currentScreen = "farmer_login" },
                            onBuyerClick = { currentScreen = "buyer_login" }
                        )
                    }
                    "farmer_login" -> {
                        FarmerLoginScreen(
                            onBackClick = { currentScreen = "login_selection" },
                            onLoginClick = { currentScreen = "main" },
                            onSignUpClick = { /* Navigate to Farmer Sign Up */ }
                        )
                    }
                    "buyer_login" -> {
                        BuyerLoginScreen(
                            onBackClick = { currentScreen = "login_selection" },
                            onLoginClick = { currentScreen = "main" },
                            onSignUpClick = { /* Navigate to Buyer Sign Up */ }
                        )
                    }
                    "main" -> {
                        MainScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MapaAni3Theme {
        Greeting("Android")
    }
}