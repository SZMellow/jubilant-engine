package com.example.mapaani3

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mapaani3.ui.theme.MapaAni3Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUpClick: (String, String, String, String, String?) -> Unit,
    onLoginClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var userType by remember { mutableStateOf("BUYER") }
    var identificationProof by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.green2))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 24.dp, end = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign Up",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Surface(
                modifier = Modifier.fillMaxSize().weight(1f),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create Account",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Role Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { userType = "BUYER" },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (userType == "BUYER") colorResource(id = R.color.green2) else colorResource(id = R.color.green2A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Buyer", color = if (userType == "BUYER") Color.White else colorResource(id = R.color.green1))
                        }
                        Button(
                            onClick = { userType = "FARMER" },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (userType == "FARMER") colorResource(id = R.color.green2) else colorResource(id = R.color.green2A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Farmer", color = if (userType == "FARMER") Color.White else colorResource(id = R.color.green1))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SignUpTextField(value = name, onValueChange = { name = it }, label = if (userType == "FARMER") "Farm Name / Full Name" else "Full Name", placeholder = "Juan Dela Cruz")
                    Spacer(modifier = Modifier.height(12.dp))
                    SignUpTextField(value = email, onValueChange = { email = it }, label = "Email Address", placeholder = "juan@example.com", keyboardType = KeyboardType.Email)
                    Spacer(modifier = Modifier.height(12.dp))
                    
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
                            focusedTextColor = colorResource(id = R.color.yellowrice),
                            unfocusedTextColor = colorResource(id = R.color.yellowrice),
                            focusedContainerColor = colorResource(id = R.color.green2A).copy(alpha = 0.5f),
                            unfocusedContainerColor = colorResource(id = R.color.green2A).copy(alpha = 0.5f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (userType == "FARMER") {
                        SignUpTextField(
                            value = identificationProof, 
                            onValueChange = { identificationProof = it }, 
                            label = "Farmer ID / RSBSA Number", 
                            placeholder = "Required for Farmers"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (showError) {
                        Text("Please fill all required fields correctly.", color = Color.Red, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { 
                            val isFarmerValid = userType == "BUYER" || (userType == "FARMER" && identificationProof.isNotEmpty())
                            if (password.isNotEmpty() && name.isNotEmpty() && email.isNotEmpty() && isFarmerValid) {
                                onSignUpClick(name, email, password, userType, if (userType == "FARMER") identificationProof else null)
                            } else {
                                showError = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(0.7f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green2)),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(text = "Sign Up", fontSize = 18.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Text(text = "Already have an account? ", fontSize = 12.sp, color = Color.Gray)
                        Text(text = "Log In", fontSize = 12.sp, color = colorResource(id = R.color.green2), fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onLoginClick() })
                    }
                }
            }
        }
    }
}

@Composable
fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF3E2723)
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF3E2723).copy(alpha = 0.5f)) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedTextColor = colorResource(id = R.color.yellowrice),
                unfocusedTextColor = colorResource(id = R.color.yellowrice),
                focusedContainerColor = colorResource(id = R.color.green2A).copy(alpha = 0.5f),
                unfocusedContainerColor = colorResource(id = R.color.green2A).copy(alpha = 0.5f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    MapaAni3Theme {
        SignUpScreen(onSignUpClick = { _, _, _, _, _ -> }, onLoginClick = {})
    }
}
