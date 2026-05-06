package com.example.mapaani3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mapaani3.ui.theme.MapaAni3Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerLoginScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var emailOrMobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.green1)) // Using darker green for Farmer
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_media_play),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "Farmer Log In",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(30.dp))

            Surface(
                modifier = Modifier.fillMaxSize().weight(1f),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Kumusta Farmer!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ready to sell your crops? Log in to manage your farm's harvest.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = "Email or Mobile Number",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF3E2723),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = emailOrMobile,
                        onValueChange = { emailOrMobile = it },
                        placeholder = { Text("farmer@example.com", color = Color(0xFF3E2723).copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colorResource(id = R.color.green2A).copy(alpha = 0.5f),
                            unfocusedContainerColor = colorResource(id = R.color.green2A).copy(alpha = 0.5f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Password",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF3E2723),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("*************", color = Color(0xFF3E2723).copy(alpha = 0.5f)) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(painter = painterResource(id = android.R.drawable.ic_menu_view), contentDescription = null, tint = Color(0xFF3E2723))
                            }
                        },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colorResource(id = R.color.green2A).copy(alpha = 0.5f),
                            unfocusedContainerColor = colorResource(id = R.color.green2A).copy(alpha = 0.5f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Text(
                        text = "Forget Password",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.End).padding(top = 8.dp).clickable { }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier.fillMaxWidth(0.7f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green1)),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(text = "Log In", fontSize = 18.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "or sign up with", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Image(painter = painterResource(id = R.drawable.google), contentDescription = "Google", modifier = Modifier.size(36.dp).clip(CircleShape).clickable { })
                        Image(painter = painterResource(id = R.drawable.facebook), contentDescription = "Facebook", modifier = Modifier.size(36.dp).clip(CircleShape).clickable { })
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row {
                        Text(text = "Don't have an account? ", fontSize = 12.sp, color = Color.Gray)
                        Text(text = "Sign Up", fontSize = 12.sp, color = colorResource(id = R.color.green1), fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onSignUpClick() })
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FarmerLoginScreenPreview() {
    MapaAni3Theme {
        FarmerLoginScreen(onBackClick = {}, onLoginClick = {}, onSignUpClick = {})
    }
}
