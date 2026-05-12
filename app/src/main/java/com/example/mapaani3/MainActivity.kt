package com.example.mapaani3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.mapaani3.ui.theme.MapaAni3Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapaAni3Theme {
                val context = LocalContext.current
                val db = remember { AppDatabase.getDatabase(context) }
                val scope = rememberCoroutineScope()
                var currentScreen by remember { mutableStateOf("splash") }

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
                                    val user = db.userDao().getUserByEmail(email)
                                    if (user != null && user.password == password) {
                                        UserSession.currentUserId = user.id
                                        UserSession.currentUserType = if (user.userType == "FARMER") UserType.FARMER else UserType.BUYER
                                        currentScreen = if (user.userType == "FARMER") "farmer_main" else "main"
                                    } else {
                                        android.widget.Toast.makeText(contextForToast, "Invalid email or password", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onSignUpClick = { currentScreen = "signup" }
                        )
                    }
                    "signup" -> {
                        SignUpScreen(
                            onSignUpClick = { name, email, password, type, proof ->
                                scope.launch {
                                    val newUser = UserEntity(name = name, email = email, password = password, userType = type, identificationProof = proof)
                                    db.userDao().insertUser(newUser)
                                    val userFromDb = db.userDao().getUserByEmail(email)
                                    UserSession.currentUserId = userFromDb?.id
                                    UserSession.currentUserType = if (type == "FARMER") UserType.FARMER else UserType.BUYER
                                    currentScreen = if (type == "FARMER") "farmer_main" else "main"
                                }
                            },
                            onLoginClick = { currentScreen = "login" }
                        )
                    }
                    "farmer_main" -> {
                        FarmerMain(onExit = { 
                            UserSession.currentUserId = null
                            currentScreen = "login" 
                        })
                    }
                    "main" -> {
                        MainScreen(onExit = { 
                            UserSession.currentUserId = null
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
