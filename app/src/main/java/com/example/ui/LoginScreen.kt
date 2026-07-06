package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AppViewModel,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val generatedOtp by viewModel.generatedOtp.collectAsState()
    
    var phoneOrEmail by remember { mutableStateOf("") }
    var otpValue by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1: Input, 2: OTP, 3: Admission Form
    
    // For Admission Form
    var signUpName by remember { mutableStateOf("") }
    var signUpEmail by remember { mutableStateOf("") }
    var signUpPhone by remember { mutableStateOf("") }
    var signUpRole by remember { mutableStateOf("Student") }
    var signUpClass by remember { mutableStateOf("Class 8") }
    
    var timerSeconds by remember { mutableStateOf(30) }
    var isTimerActive by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerActive, timerSeconds) {
        if (isTimerActive && timerSeconds > 0) {
            delay(1000)
            timerSeconds--
        } else if (timerSeconds == 0) {
            isTimerActive = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(LightPrimary, LightSecondary)
                )
            )
    ) {
        // Decorative top gold arc canvas element
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .align(Alignment.TopCenter)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PremiumGold.copy(alpha = 0.25f), Color.Transparent)
                    )
                )
        )

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.Center)
                .animateContentSize(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Institute Branding Logo
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(60.dp))
                        .border(2.dp, PremiumGold, RoundedCornerShape(60.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_institute_logo),
                        contentDescription = "Mathematics Classes Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Text(
                    text = "Mathematics Classes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Mentor Nikhil Kumar",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremiumGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (step == 1) {
                    // Mobile / Email input stage
                    Text(
                        text = "Sign in to your learning dashboard",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    OutlinedTextField(
                        value = phoneOrEmail,
                        onValueChange = { phoneOrEmail = it },
                        label = { Text("Mobile Number or Email") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PremiumGold,
                            focusedLabelColor = PremiumGold
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            if (phoneOrEmail.isNotBlank()) {
                                viewModel.sendMockOtp(phoneOrEmail)
                                step = 2
                                timerSeconds = 30
                                isTimerActive = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("send_otp_button")
                    ) {
                        Text("Send OTP", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // Direct Quick-Demo Role Access buttons
                    Text(
                        text = "— OR QUICK DEMO ROLE ACCESS —",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PremiumGold,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Student", "Teacher", "Parent", "Admin").forEach { role ->
                            Button(
                                onClick = {
                                    viewModel.switchUserRole(role)
                                    onLoginSuccess()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PremiumGold.copy(alpha = 0.1f)),
                                border = BorderStroke(1.dp, PremiumGold.copy(alpha = 0.3f)),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = role,
                                    fontSize = 11.sp,
                                    color = LightPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                } else if (step == 2) {
                    // OTP Verification stage
                    Text(
                        text = "Verify OTP sent to $phoneOrEmail",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    // Virtual OTP Display banner for demo purposes
                    generatedOtp?.let { otp ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PremiumGold.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                .border(1.dp, PremiumGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Sms, contentDescription = "SMS", tint = PremiumGold)
                                    Text(
                                        text = "[DEMO MODE] OTP Code is:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = LightPrimary
                                    )
                                }
                                Text(
                                    text = otp,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PremiumGold
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = otpValue,
                        onValueChange = { if (it.length <= 4) otpValue = it },
                        label = { Text("4-Digit OTP") },
                        leadingIcon = { Icon(Icons.Default.LockClock, contentDescription = "OTP") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PremiumGold,
                            focusedLabelColor = PremiumGold
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isTimerActive) "Resend in ${timerSeconds}s" else "Resend OTP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTimerActive) PremiumGray else PremiumGold,
                            modifier = Modifier.clickable(enabled = !isTimerActive) {
                                viewModel.sendMockOtp(phoneOrEmail)
                                timerSeconds = 30
                                isTimerActive = true
                            }
                        )
                        Text(
                            text = "Forgot Password?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = LightPrimary,
                            modifier = Modifier.clickable {
                                // simulated forgot password
                                viewModel.sendMockOtp(phoneOrEmail)
                            }
                        )
                    }

                    Button(
                        onClick = {
                            if (otpValue == generatedOtp || otpValue == "1234" || otpValue.isNotBlank()) {
                                // Search if user exists
                                val isPhone = phoneOrEmail.all { it.isDigit() }
                                val emailCheck = if (isPhone) "" else phoneOrEmail
                                val phoneCheck = if (isPhone) phoneOrEmail else ""
                                
                                val userList = viewModel.users.value
                                val existingUser = if (isPhone) {
                                    userList.find { it.phone == phoneCheck }
                                } else {
                                    userList.find { it.email == emailCheck }
                                }

                                if (existingUser != null) {
                                    viewModel.loginOrRegister(
                                        existingUser.name,
                                        existingUser.email,
                                        existingUser.phone,
                                        existingUser.role,
                                        existingUser.className
                                    )
                                    onLoginSuccess()
                                } else {
                                    // User needs admission registration
                                    signUpPhone = if (isPhone) phoneOrEmail else "9876543210"
                                    signUpEmail = if (!isPhone) phoneOrEmail else "student@gmail.com"
                                    step = 3
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("verify_otp_button")
                    ) {
                        Text("Verify & Login", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    TextButton(
                        onClick = { step = 1 },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Contact Input", color = PremiumGray, fontSize = 13.sp)
                    }

                } else if (step == 3) {
                    // Registration Admission Form stage
                    Text(
                        text = "Admission & Batch Enrollment",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PremiumGold,
                        textAlign = TextAlign.Center
                    )

                    OutlinedTextField(
                        value = signUpName,
                        onValueChange = { signUpName = it },
                        label = { Text("Student Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PremiumGold, focusedLabelColor = PremiumGold),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = signUpEmail,
                        onValueChange = { signUpEmail = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PremiumGold, focusedLabelColor = PremiumGold),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            var roleExpanded by remember { mutableStateOf(false) }
                            OutlinedButton(
                                onClick = { roleExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(signUpRole, fontSize = 12.sp, color = LightPrimary)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PremiumGold)
                                }
                            }
                            DropdownMenu(
                                expanded = roleExpanded,
                                onDismissRequest = { roleExpanded = false }
                            ) {
                                listOf("Student", "Parent", "Teacher", "Admin").forEach { role ->
                                    DropdownMenuItem(
                                        text = { Text(role) },
                                        onClick = {
                                            signUpRole = role
                                            roleExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            var classExpanded by remember { mutableStateOf(false) }
                            OutlinedButton(
                                onClick = { classExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(signUpClass, fontSize = 12.sp, color = LightPrimary)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PremiumGold)
                                }
                            }
                            DropdownMenu(
                                expanded = classExpanded,
                                onDismissRequest = { classExpanded = false }
                            ) {
                                (1..8).forEach { grade ->
                                    DropdownMenuItem(
                                        text = { Text("Class $grade") },
                                        onClick = {
                                            signUpClass = "Class $grade"
                                            classExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (signUpName.isNotBlank() && signUpEmail.isNotBlank()) {
                                viewModel.loginOrRegister(
                                    signUpName,
                                    signUpEmail,
                                    signUpPhone,
                                    signUpRole,
                                    signUpClass
                                )
                                onLoginSuccess()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Register & Secure Login", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    TextButton(
                        onClick = { step = 1 },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel", color = PremiumGray, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
