package com.example.mapaani3

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mapaani3.ui.theme.MapaAni3Theme

@Composable
fun FAQScreen(onExit: () -> Unit) {
    var selectedCategory by remember { mutableStateOf("General") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.green2))
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onExit) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Exit", tint = Color.White)
                }
            }
            Text(
                text = "Help & FAQs",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "How Can We Help You?",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }

        // Content Card
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Category Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("General", "Account", "Services").forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colorResource(id = R.color.green2),
                                selectedLabelColor = Color.White,
                                containerColor = colorResource(id = R.color.green2A).copy(alpha = 0.5f),
                                labelColor = colorResource(id = R.color.green1)
                            ),
                            border = null,
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar
                TextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = { Icon(Icons.Outlined.Tune, contentDescription = null, tint = colorResource(id = R.color.green2)) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // FAQ List
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    val faqs = listOf(
                        FAQData("General", "How do i track my order status in real-time?", "You can track your order in real-time by going to the 'History' tab and clicking on an active order to see its current progress and delivery updates."),
                        FAQData("General", "Are there any service fees?", "We charge a minimal service fee of 2% to help maintain the platform and ensure a smooth experience for both farmers and buyers."),
                        FAQData("Account", "How do i report bad crops", "If you receive crops that do not meet quality standards, go to the order details in your History and click 'Report Issue'. Please include photos of the crops."),
                        FAQData("Account", "How do i reset my password?", "Go to the Login screen and click on 'Forgot Password'. Follow the instructions sent to your registered email address to set a new password."),
                        FAQData("Services", "What payment methods do you accept?", "We currently accept Cash on Delivery (COD), GCash, and Maya for all transactions."),
                        FAQData("Services", "What is the policy for cancellation or refunds?", "Cancellations are allowed before the order is marked as 'Processing'. Refunds are processed within 3-5 business days for valid quality complaints."),
                        FAQData("Services", "How are delivery fees calculated?", "Delivery fees are calculated based on the distance between the farmer's location and your delivery address, as well as the total weight of the order.")
                    )

                    faqs.filter { it.category == selectedCategory }.forEach { faq ->
                        FAQItem(
                            question = faq.question,
                            answer = faq.answer
                        )
                    }
                }
            }
        }
    }
}

data class FAQData(val category: String, val question: String, val answer: String)

@Composable
fun TabButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) colorResource(id = R.color.green2) else colorResource(id = R.color.green2A).copy(alpha = 0.5f),
            contentColor = if (selected) Color.White else colorResource(id = R.color.green1)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = question,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.green1),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = colorResource(id = R.color.green1)
            )
        }
        AnimatedVisibility(visible = expanded) {
            Text(
                text = answer,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
    }
}

@Preview(showBackground = true)
@Composable
fun FAQScreenPreview() {
    MapaAni3Theme {
        FAQScreen(onExit = {})
    }
}
