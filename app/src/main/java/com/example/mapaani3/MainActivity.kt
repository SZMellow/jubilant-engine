package com.example.mapaani3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.mapaani3.ui.theme.MapaAni3Theme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapaAni3Theme {
                val repository = remember { AppRepository() }
                val scope = rememberCoroutineScope()
                var currentScreen by remember { mutableStateOf("splash") }

                // Authentication and Navigation Logic
                when (currentScreen) {
                    "splash" -> {
                        SplashScreen(onNavigateToMain = {
                            currentScreen = "onboarding"
                        })
                    }
                    "onboarding" -> {
                        OnboardingScreen(onFinished = {
                            currentScreen = "login"
                        })
                    }
                    "login" -> {
                        val contextForToast = LocalContext.current
                        LoginScreen(
                            onLoginClick = { email, password ->
                                scope.launch {
                                    val user = repository.getUserByEmail(email)
                                    val hashedInput = PasswordHasher.hash(password)
                                    if (user != null && user.passwordHash == hashedInput) {
                                        UserSession.currentUserId = user.id
                                        UserSession.currentUserType = when (user.userType) {
                                            "FARMER" -> UserType.FARMER
                                            "ADMIN" -> UserType.ADMIN
                                            else -> UserType.BUYER
                                        }
                                        UserSession.isUserVerified = user.isVerified
                                        android.widget.Toast.makeText(contextForToast, "Logged in as ${user.userType}. Verified: ${user.isVerified}", android.widget.Toast.LENGTH_SHORT).show()
                                        currentScreen = when (user.userType) {
                                            "FARMER" -> "farmer_main"
                                            "ADMIN" -> "admin_main"
                                            else -> "main"
                                        }
                                    } else {
                                        android.widget.Toast.makeText(contextForToast, "Invalid email or password", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onSignUpClick = { currentScreen = "signup" }
                        )
                    }
                    "signup" -> {
                        val contextForToast = LocalContext.current
                        SignUpScreen(
                            onSignUpClick = { name, email, password, type, proof ->
                                scope.launch {
                                    val hashedPassword = PasswordHasher.hash(password)
                                    val newUser = UserEntity(
                                        name = name, 
                                        email = email, 
                                        passwordHash = hashedPassword, 
                                        userType = type, 
                                        identificationProof = proof
                                    )
                                    repository.registerUser(newUser)
                                    val user = repository.getUserByEmail(email)
                                    user?.let {
                                        UserSession.currentUserId = it.id
                                        UserSession.currentUserType = when (type) {
                                            "FARMER" -> UserType.FARMER
                                            "ADMIN" -> UserType.ADMIN
                                            else -> UserType.BUYER
                                        }
                                        UserSession.isUserVerified = it.isVerified
                                        android.widget.Toast.makeText(contextForToast, "Account Created. Verified: ${it.isVerified}", android.widget.Toast.LENGTH_SHORT).show()
                                        currentScreen = when (type) {
                                            "FARMER" -> "farmer_main"
                                            "ADMIN" -> "admin_main"
                                            else -> "main"
                                        }
                                    }
                                }
                            },
                            onLoginClick = { currentScreen = "login" }
                        )
                    }
                    "farmer_main" -> {
                        FarmerMain(onExit = { 
                            UserSession.currentUserId = null
                            UserSession.isUserVerified = false
                            currentScreen = "login" 
                        })
                    }
                    "main" -> {
                        MainScreen(onExit = { 
                            UserSession.currentUserId = null
                            UserSession.isUserVerified = false
                            currentScreen = "login" 
                        })
                    }
                    "admin_main" -> {
                        AdminDashboardScreen(onExit = {
                            UserSession.currentUserId = null
                            UserSession.isUserVerified = false
                            currentScreen = "login"
                        })
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
