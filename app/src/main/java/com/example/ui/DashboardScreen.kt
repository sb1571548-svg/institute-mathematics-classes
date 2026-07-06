package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUserSession.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    var activeSubView by remember { mutableStateOf("home") } // "home", "live_class", "tests", "doubts", "materials"
    
    // Test simulator state
    var activeTestingPaper by remember { mutableStateOf<Test?>(null) }
    
    // Fee dialog payment state
    var selectedFeeForPayment by remember { mutableStateOf<Fee?>(null) }
    var showReceiptForFee by remember { mutableStateOf<Fee?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mathematics Classes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Gandhi Chowk • Mentor Nikhil Kumar",
                            fontSize = 11.sp,
                            color = PremiumGoldLight
                        )
                    }
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = com.example.R.drawable.img_institute_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .padding(start = 12.dp, end = 8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, PremiumGold, CircleShape)
                            .background(Color.White)
                    )
                },
                actions = {
                    // Quick Role Switcher
                    var roleMenuExpanded by remember { mutableStateOf(false) }
                    Box {
                        AssistChip(
                            onClick = { roleMenuExpanded = true },
                            label = { Text(currentUser?.role ?: "Guest", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.SwitchAccount, contentDescription = null, tint = PremiumGold) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = LightSecondary),
                            border = BorderStroke(1.dp, PremiumGold)
                        )
                        DropdownMenu(
                            expanded = roleMenuExpanded,
                            onDismissRequest = { roleMenuExpanded = false }
                        ) {
                            listOf("Student", "Teacher", "Parent", "Admin").forEach { role ->
                                DropdownMenuItem(
                                    text = { Text(role) },
                                    onClick = {
                                        viewModel.switchUserRole(role)
                                        roleMenuExpanded = false
                                        activeSubView = "home"
                                        activeTestingPaper = null
                                    }
                                )
                            }
                        }
                    }

                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightPrimary)
            )
        },
        bottomBar = {
            // Premium edge-to-edge adaptive bottom navigation
            NavigationBar(
                containerColor = LightSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeSubView == "home",
                    onClick = { activeSubView = "home"; activeTestingPaper = null },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LightPrimary,
                        selectedTextColor = LightPrimary,
                        indicatorColor = PremiumGold.copy(alpha = 0.2f),
                        unselectedIconColor = PremiumGray,
                        unselectedTextColor = PremiumGray
                    )
                )
                NavigationBarItem(
                    selected = activeSubView == "live_class",
                    onClick = { activeSubView = "live_class" },
                    icon = { Icon(Icons.Default.VideoCall, contentDescription = "Live Class") },
                    label = { Text("Live Studio", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LightPrimary,
                        selectedTextColor = LightPrimary,
                        indicatorColor = PremiumGold.copy(alpha = 0.2f),
                        unselectedIconColor = PremiumGray,
                        unselectedTextColor = PremiumGray
                    )
                )
                NavigationBarItem(
                    selected = activeSubView == "materials",
                    onClick = { activeSubView = "materials" },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Resources") },
                    label = { Text("Materials", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LightPrimary,
                        selectedTextColor = LightPrimary,
                        indicatorColor = PremiumGold.copy(alpha = 0.2f),
                        unselectedIconColor = PremiumGray,
                        unselectedTextColor = PremiumGray
                    )
                )
                NavigationBarItem(
                    selected = activeSubView == "tests",
                    onClick = { activeSubView = "tests"; activeTestingPaper = null },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = "Tests") },
                    label = { Text("Test Series", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LightPrimary,
                        selectedTextColor = LightPrimary,
                        indicatorColor = PremiumGold.copy(alpha = 0.2f),
                        unselectedIconColor = PremiumGray,
                        unselectedTextColor = PremiumGray
                    )
                )
                NavigationBarItem(
                    selected = activeSubView == "doubts",
                    onClick = { activeSubView = "doubts" },
                    icon = { Icon(Icons.Default.QuestionAnswer, contentDescription = "Doubts") },
                    label = { Text("Doubts Hub", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LightPrimary,
                        selectedTextColor = LightPrimary,
                        indicatorColor = PremiumGold.copy(alpha = 0.2f),
                        unselectedIconColor = PremiumGray,
                        unselectedTextColor = PremiumGray
                    )
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(LightBackground)
                .verticalScroll(rememberScrollState())
        ) {
            // Notice alert center banner if there are announcements
            if (notifications.isNotEmpty()) {
                val latestAnnouncement = notifications.find { it.type == "Announcement" }
                if (latestAnnouncement != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(LightPrimary, LightSecondary)
                                )
                            )
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = "Announcement", tint = PremiumGold, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Notice: ${latestAnnouncement.content}",
                                color = Color.White,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Central content router
            when (activeSubView) {
                "home" -> {
                    when (currentUser?.role) {
                        "Student" -> StudentDashboard(viewModel, onStartTest = { activeTestingPaper = it; activeSubView = "tests" })
                        "Teacher" -> TeacherDashboard(viewModel)
                        "Parent" -> ParentDashboard(viewModel, onPayFee = { selectedFeeForPayment = it }, onViewReceipt = { showReceiptForFee = it })
                        "Admin" -> AdminDashboard(viewModel)
                        else -> Text("Please sign in or switch roles to proceed.", modifier = Modifier.padding(16.dp))
                    }
                }
                "live_class" -> LiveClassRoom(viewModel)
                "materials" -> StudyMaterialsHub(viewModel)
                "tests" -> {
                    if (activeTestingPaper != null) {
                        TestTakerSimulation(
                            test = activeTestingPaper!!,
                            viewModel = viewModel,
                            onCompleted = {
                                activeTestingPaper = null
                                activeSubView = "home"
                            }
                        )
                    } else {
                        TestsCatalog(viewModel, onStartTest = { activeTestingPaper = it })
                    }
                }
                "doubts" -> DoubtsHub(viewModel)
            }

            // Detailed contact address banner at the bottom of the home screen to reinforce trust
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Institute Mathematics Classes",
                        fontWeight = FontWeight.Bold,
                        color = LightPrimary,
                        fontSize = 15.sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = PremiumGold, modifier = Modifier.size(16.dp))
                        Text("Gandhi Chowk, Near Nag Mandir, India", fontSize = 12.sp, color = PremiumGray)
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = PremiumGold, modifier = Modifier.size(14.dp))
                            Text("+91 7061157094", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = PremiumGold, modifier = Modifier.size(14.dp))
                            Text("sb1571548@gmail.com", fontSize = 11.sp, color = PremiumGray)
                        }
                    }
                }
            }
        }

        // UPI simulated gateway overlay dialog
        selectedFeeForPayment?.let { fee ->
            UpiCheckoutSimulation(
                fee = fee,
                viewModel = viewModel,
                onDismiss = { selectedFeeForPayment = null }
            )
        }

        // Printable Auto Fee Receipt dialog
        showReceiptForFee?.let { fee ->
            FeeReceiptDialog(
                fee = fee,
                onDismiss = { showReceiptForFee = null }
            )
        }
    }
}

// -----------------------------------------------------------------------------------
// 1. STUDENT DASHBOARD
// -----------------------------------------------------------------------------------
@Composable
fun StudentDashboard(viewModel: AppViewModel, onStartTest: (Test) -> Unit) {
    val currentUser by viewModel.currentUserSession.collectAsState()
    val liveClasses by viewModel.liveClasses.collectAsState()
    val materials by viewModel.studyMaterials.collectAsState()
    val testSubmissions by viewModel.testSubmissions.collectAsState()
    val tests by viewModel.tests.collectAsState()
    val fees by viewModel.fees.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LightPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Hello, ${currentUser?.name}!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Enrollment: ${currentUser?.className} Batch", fontSize = 13.sp, color = PremiumGoldLight)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Conquer mathematics with daily practice problems!", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(PremiumGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }

        // Live Class Indicator if any is active
        val activeLive = liveClasses.find { it.isLive }
        if (activeLive != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, PremiumGold),
                colors = CardDefaults.cardColors(containerColor = PremiumGold.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.Red, CircleShape)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text("CLASS IS LIVE NOW!", fontSize = 11.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                        Text(activeLive.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
                        Text("Mentor: ${activeLive.mentorName}", fontSize = 12.sp, color = PremiumGray)
                    }
                    Button(
                        onClick = { /* simulated, the tab switch does this */ },
                        colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Join Now", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        // Quick Stats Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = LightSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("My Attendance", fontSize = 11.sp, color = PremiumGray)
                    Text("92%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = LightSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tests Given", fontSize = 11.sp, color = PremiumGray)
                    Text("${testSubmissions.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PremiumGold)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = LightSurface)
            ) {
                val pendingFees = fees.filter { it.status == "Due" }
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Fees Pending", fontSize = 11.sp, color = PremiumGray)
                    Text(if (pendingFees.isEmpty()) "Nil" else "₹${pendingFees.sumOf { it.amount }.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                }
            }
        }

        // Homework & DPP Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Daily Practice Problems & Homework", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
        }

        materials.take(3).forEach { material ->
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(LightPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when(material.type) {
                                "Video" -> Icons.Default.PlayCircle
                                "PDF Notes" -> Icons.Default.Description
                                "DPP" -> Icons.Default.Assignment
                                "Homework" -> Icons.Default.HomeWork
                                else -> Icons.Default.Attachment
                            },
                            contentDescription = null,
                            tint = LightPrimary
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(material.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        Text("${material.type} • ${material.batchName}", fontSize = 11.sp, color = PremiumGray)
                    }
                    TextButton(
                        onClick = { viewModel.submitHomework(material.id, material.title) }
                    ) {
                        Text("Action", fontSize = 11.sp, color = PremiumGold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Test Series & Rankings List
        Text("Active Math Test Series", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
        tests.take(2).forEach { test ->
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(test.title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("${test.type} • ${test.durationMinutes} mins • 5 Qs", fontSize = 11.sp, color = PremiumGray)
                    }
                    Button(
                        onClick = { onStartTest(test) },
                        colors = ButtonDefaults.buttonColors(containerColor = PremiumGold),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Start", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// 2. TEACHER DASHBOARD
// -----------------------------------------------------------------------------------
@Composable
fun TeacherDashboard(viewModel: AppViewModel) {
    val liveClasses by viewModel.liveClasses.collectAsState()
    val materials by viewModel.studyMaterials.collectAsState()
    val testSubmissions by viewModel.testSubmissions.collectAsState()
    val doubts by viewModel.doubts.collectAsState()

    var showLiveDialog by remember { mutableStateOf(false) }
    var showMaterialDialog by remember { mutableStateOf(false) }
    var showTestDialog by remember { mutableStateOf(false) }

    // Dialog Input states
    var liveTitle by remember { mutableStateOf("") }
    var liveBatch by remember { mutableStateOf("Class 8 Algebra Elite") }
    var liveTime by remember { mutableStateOf("Today, 5:00 PM") }

    var matTitle by remember { mutableStateOf("") }
    var matType by remember { mutableStateOf("DPP") }
    var matBatch by remember { mutableStateOf("Class 8 Algebra Elite") }
    var matDesc by remember { mutableStateOf("") }

    var testTitle by remember { mutableStateOf("") }
    var testType by remember { mutableStateOf("Chapter Test") }
    var testBatch by remember { mutableStateOf("Class 8 Algebra Elite") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Teacher Welcome Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LightPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Teacher Console", fontSize = 14.sp, color = PremiumGoldLight, fontWeight = FontWeight.SemiBold)
                Text("Nikhil Kumar", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Managing Class 1–8 Batches & DPP Schedules", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showLiveDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text("Live Class", fontSize = 11.sp, color = Color.White)
                }
            }
            Button(
                onClick = { showMaterialDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Upload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text("Upload PDF", fontSize = 11.sp, color = Color.White)
                }
            }
            Button(
                onClick = { showTestDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text("Create Test", fontSize = 11.sp, color = Color.White)
                }
            }
        }

        // Active Classes Monitor
        Text("Scheduled Live Rooms", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
        liveClasses.forEach { live ->
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(if (live.isLive) Color.Red else PremiumGray, CircleShape))
                            Text(live.title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("${live.batchName} • ${live.scheduledTime}", fontSize = 11.sp, color = PremiumGray)
                    }
                    IconButton(onClick = { viewModel.deleteLiveClass(live) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                    }
                }
            }
        }

        // Doubts to resolve
        Text("Unanswered Doubts", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
        val unanswered = doubts.filter { it.answer == null }
        if (unanswered.isEmpty()) {
            Text("All clear! No doubts pending.", fontSize = 12.sp, color = PremiumGray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        } else {
            unanswered.forEach { doubt ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = LightSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(doubt.studentName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
                            Text("Doubt pending", fontSize = 10.sp, color = PremiumGold)
                        }
                        Text(doubt.question, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        
                        var replyText by remember { mutableStateOf("") }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = replyText,
                                onValueChange = { replyText = it },
                                label = { Text("Write answer...") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            IconButton(
                                onClick = {
                                    if (replyText.isNotBlank()) {
                                        viewModel.replyToDoubt(doubt, replyText)
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Reply", tint = PremiumGold)
                            }
                        }
                    }
                }
            }
        }

        // Student Test Submissions Gradebook
        Text("Recent Test Scores Report", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
        testSubmissions.forEach { submission ->
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(submission.studentName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(submission.testTitle, fontSize = 11.sp, color = PremiumGray)
                    }
                    Box(
                        modifier = Modifier
                            .background(LightPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Score: ${submission.score}/${submission.totalQuestions}", fontSize = 12.sp, color = LightPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // CREATE LIVE CLASS DIALOG
    if (showLiveDialog) {
        AlertDialog(
            onDismissRequest = { showLiveDialog = false },
            title = { Text("Schedule Live Video Class") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = liveTitle, onValueChange = { liveTitle = it }, label = { Text("Topic/Chapter Title") })
                    OutlinedTextField(value = liveBatch, onValueChange = { liveBatch = it }, label = { Text("Class Batch (e.g. Class 8)") })
                    OutlinedTextField(value = liveTime, onValueChange = { liveTime = it }, label = { Text("Time/Date") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (liveTitle.isNotBlank()) {
                        viewModel.createLiveClass(liveTitle, liveBatch, liveTime)
                        showLiveDialog = false
                    }
                }) { Text("Schedule") }
            },
            dismissButton = {
                TextButton(onClick = { showLiveDialog = false }) { Text("Cancel") }
            }
        )
    }

    // UPLOAD MATERIAL DIALOG
    if (showMaterialDialog) {
        AlertDialog(
            onDismissRequest = { showMaterialDialog = false },
            title = { Text("Upload Study Resource") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = matTitle, onValueChange = { matTitle = it }, label = { Text("Resource Title") })
                    OutlinedTextField(value = matType, onValueChange = { matType = it }, label = { Text("Type (DPP, Notes, Video)") })
                    OutlinedTextField(value = matBatch, onValueChange = { matBatch = it }, label = { Text("Batch") })
                    OutlinedTextField(value = matDesc, onValueChange = { matDesc = it }, label = { Text("Brief Instructions") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (matTitle.isNotBlank()) {
                        viewModel.uploadStudyMaterial(matTitle, matType, matBatch, "math_res.pdf", matDesc)
                        showMaterialDialog = false
                    }
                }) { Text("Upload") }
            },
            dismissButton = {
                TextButton(onClick = { showMaterialDialog = false }) { Text("Cancel") }
            }
        )
    }

    // CREATE TEST DIALOG
    if (showTestDialog) {
        AlertDialog(
            onDismissRequest = { showTestDialog = false },
            title = { Text("Create Mathematics Test") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = testTitle, onValueChange = { testTitle = it }, label = { Text("Test Chapter/Name") })
                    OutlinedTextField(value = testType, onValueChange = { testType = it }, label = { Text("Type (Olympiad, Mock)") })
                    OutlinedTextField(value = testBatch, onValueChange = { testBatch = it }, label = { Text("Target Batch") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (testTitle.isNotBlank()) {
                        val mockQs = """
                            [
                              {"q":"What is LCM of 12 & 15?","a":"60","b":"30","c":"15","d":"90","ans":"A"},
                              {"q":"Degrees in a straight line angle?","a":"90","b":"180","c":"360","d":"100","ans":"B"}
                            ]
                        """.trimIndent()
                        viewModel.createTest(testTitle, testType, testBatch, 15, mockQs)
                        showTestDialog = false
                    }
                }) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showTestDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// -----------------------------------------------------------------------------------
// 3. PARENT DASHBOARD
// -----------------------------------------------------------------------------------
@Composable
fun ParentDashboard(viewModel: AppViewModel, onPayFee: (Fee) -> Unit, onViewReceipt: (Fee) -> Unit) {
    val fees by viewModel.fees.collectAsState()
    val testSubmissions by viewModel.testSubmissions.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Parent
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LightPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Parent Portal", fontSize = 14.sp, color = PremiumGoldLight, fontWeight = FontWeight.SemiBold)
                Text("Sunita Sharma", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Child: Rahul Sharma (Class 8 Batch)", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }

        // Child Attendance
        Card(
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Daily School Attendance", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
                    Text("Total Present: 18 Days | Absent: 1 Day", fontSize = 12.sp, color = PremiumGray)
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Green.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("94%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Green)
                }
            }
        }

        // Fee Due & Payments with printable auto receipts
        Text("Fee Balance Management", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
        fees.forEach { fee ->
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Monthly Tuition: ${fee.studentName}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Amount: ₹${fee.amount} • Due: ${fee.dueDate}", fontSize = 12.sp, color = PremiumGray)
                    }
                    if (fee.status == "Due") {
                        Button(
                            onClick = { onPayFee(fee) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Pay Now", fontSize = 11.sp, color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = { onViewReceipt(fee) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Receipt", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Progress Performance Tracker
        Text("Child Test Progress Report", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
        testSubmissions.forEach { sub ->
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(sub.testTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Submitted: Today", fontSize = 11.sp, color = PremiumGray)
                    }
                    Text(
                        "Result: ${sub.score}/${sub.totalQuestions}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sub.score >= 3) Color.Green else Color.Red
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// 4. ADMIN DASHBOARD
// -----------------------------------------------------------------------------------
@Composable
fun AdminDashboard(viewModel: AppViewModel) {
    val users by viewModel.users.collectAsState()
    val batches by viewModel.batches.collectAsState()
    val fees by viewModel.fees.collectAsState()

    var showBatchDialog by remember { mutableStateOf(false) }
    var batchNameInput by remember { mutableStateOf("") }
    var batchGradeInput by remember { mutableStateOf("Class 8") }
    var batchTimeInput by remember { mutableStateOf("04:00 PM - 05:00 PM") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Summary
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LightPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Central Administrative Panel", fontSize = 13.sp, color = PremiumGoldLight, fontWeight = FontWeight.SemiBold)
                Text("Institute Control Board", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Gandhi Chowk, Nag Mandir Branch", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }

        // Revenue progress bar (UPI vs Cash)
        val collected = fees.filter { it.status == "Paid" }.sumOf { it.amount }
        val pending = fees.filter { it.status == "Due" }.sumOf { it.amount }
        val collectionPercentage = if (collected + pending > 0) (collected / (collected + pending)).toFloat() else 0f

        Card(
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Online Fee Collection Analytics", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Collected: ₹${collected.toInt()}", fontSize = 12.sp, color = Color.Green, fontWeight = FontWeight.Bold)
                    Text("Pending: ₹${pending.toInt()}", fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                }
                LinearProgressIndicator(
                    progress = { collectionPercentage },
                    color = PremiumGold,
                    trackColor = LightBackground,
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
                )
            }
        }

        // Add Batch Button
        Button(
            onClick = { showBatchDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = PremiumGold),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Text("Form New Batch (Class 1-8)", color = Color.White)
            }
        }

        // Active Batches List
        Text("Active Batches Monitor", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
        batches.forEach { batch ->
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(batch.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Timing: ${batch.timing} • Grade: ${batch.gradeClass}", fontSize = 11.sp, color = PremiumGray)
                    }
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = PremiumGold)
                }
            }
        }

        // Registered Roster List
        Text("Registered Students & Teachers", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
        users.forEach { user ->
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(user.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Phone: ${user.phone} • ${user.className}", fontSize = 11.sp, color = PremiumGray)
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                color = when (user.role) {
                                    "Admin" -> Color.Red.copy(alpha = 0.1f)
                                    "Teacher" -> PremiumGold.copy(alpha = 0.15f)
                                    "Parent" -> Color.Magenta.copy(alpha = 0.1f)
                                    else -> LightPrimary.copy(alpha = 0.1f)
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = user.role,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (user.role) {
                                "Admin" -> Color.Red
                                "Teacher" -> PremiumGold
                                "Parent" -> Color.Magenta
                                else -> LightPrimary
                            }
                        )
                    }
                }
            }
        }
    }

    if (showBatchDialog) {
        AlertDialog(
            onDismissRequest = { showBatchDialog = false },
            title = { Text("Form New Class 1-8 Batch") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = batchNameInput, onValueChange = { batchNameInput = it }, label = { Text("Batch Name (e.g. Algebra Masters)") })
                    OutlinedTextField(value = batchGradeInput, onValueChange = { batchGradeInput = it }, label = { Text("Target Class (Class 1 to 8)") })
                    OutlinedTextField(value = batchTimeInput, onValueChange = { batchTimeInput = it }, label = { Text("Batch Timing") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (batchNameInput.isNotBlank()) {
                        viewModel.addBatch(batchNameInput, batchGradeInput, batchTimeInput)
                        showBatchDialog = false
                    }
                }) { Text("Create Batch") }
            },
            dismissButton = {
                TextButton(onClick = { showBatchDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// -----------------------------------------------------------------------------------
// 5. LIVE VIDEO CLASS & DIGITAL WHITEBOARD SIMULATION
// -----------------------------------------------------------------------------------
@Composable
fun LiveClassRoom(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUserSession.collectAsState()
    val chatMessages by viewModel.liveClassChat.collectAsState()
    
    val isScreenSharing by viewModel.isScreenSharing.collectAsState()
    val isHandRaised by viewModel.isHandRaised.collectAsState()
    val isRecording by viewModel.isTeacherRecording.collectAsState()

    var chatInputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mock Video Panel Display with screen share and equation text overlays
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
                .border(2.dp, PremiumGold, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (isScreenSharing) {
                    Icon(Icons.Default.ScreenShare, contentDescription = null, tint = PremiumGold, modifier = Modifier.size(40.dp))
                    Text("Nikhil Kumar's Screen: Geometry Proofs", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape))
                        Text("LIVE VIDEO TRANSIT", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Nikhil Kumar (Mentor)", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Topic: Expanding algebraic expressions with variables", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }

            // Controls overlays (Recording / Hand)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isRecording) {
                    Box(
                        modifier = Modifier
                            .background(Color.Red, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("REC", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                if (isHandRaised) {
                    Box(
                        modifier = Modifier
                            .background(PremiumGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("HAND RAISED", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Digital Whiteboard Draw Canvas Section
        Text("Digital Whiteboard (Touch to Draw Equations)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(LightPrimary.copy(alpha = 0.05f))
                .border(1.dp, LightPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            // Drag Draw whiteboard canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val pt = change.position
                            viewModel.addWhiteboardPoint(pt.x, pt.y)
                        }
                    }
            ) {
                // Clear background equation grids
                drawContext.canvas.nativeCanvas.let { _ ->
                    // pre-drawn mathematical curves in gold to make it look premium
                    for (i in 0 until viewModel.whiteboardPaths.size) {
                        if (i > 0) {
                            val p1 = viewModel.whiteboardPaths[i - 1]
                            val p2 = viewModel.whiteboardPaths[i]
                            drawLine(
                                color = PremiumGold,
                                start = Offset(p1.first, p1.second),
                                end = Offset(p2.first, p2.second),
                                strokeWidth = 4f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
            }

            // Quick presetted Math formulas uploader button
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = {
                        // Quick equations presets
                        viewModel.addWhiteboardPoint(100f, 150f)
                        viewModel.addWhiteboardPoint(150f, 100f)
                        viewModel.addWhiteboardPoint(200f, 150f)
                        viewModel.addWhiteboardPoint(250f, 150f)
                        viewModel.addWhiteboardPoint(300f, 100f)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PremiumGold),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("Draw Equation Presets", fontSize = 9.sp, color = Color.White)
                }

                Button(
                    onClick = { viewModel.clearWhiteboard() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("Clear Canvas", fontSize = 9.sp, color = Color.White)
                }
            }
        }

        // Live Chat Feed with raise hand & screen sharing buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { viewModel.toggleScreenShare() },
                modifier = Modifier.background(LightPrimary.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.Default.ScreenShare, contentDescription = "Share Screen", tint = LightPrimary)
            }
            IconButton(
                onClick = { viewModel.toggleHandRaise() },
                modifier = Modifier.background(PremiumGold.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(Icons.Default.BackHand, contentDescription = "Raise Hand", tint = PremiumGold)
            }
            if (currentUser?.role == "Teacher") {
                IconButton(
                    onClick = { viewModel.toggleTeacherRecording() },
                    modifier = Modifier.background(Color.Red.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.RadioButtonChecked, contentDescription = "Record", tint = Color.Red)
                }
            }
        }

        // Live chat dialogue box
        Card(
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Live Classroom Chat Panel", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
                
                // Messages List
                Box(modifier = Modifier.height(100.dp).verticalScroll(rememberScrollState())) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        chatMessages.forEach { msg ->
                            Row {
                                Text("${msg.first}: ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = LightPrimary)
                                Text(msg.second, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Chat Input box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatInputText,
                        onValueChange = { chatInputText = it },
                        placeholder = { Text("Ask a question...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    IconButton(
                        onClick = {
                            if (chatInputText.isNotBlank()) {
                                viewModel.sendLiveMessage(currentUser?.name ?: "Student", chatInputText)
                                chatInputText = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = PremiumGold)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// 6. STUDY RESOURCES HUB
// -----------------------------------------------------------------------------------
@Composable
fun StudyMaterialsHub(viewModel: AppViewModel) {
    val materials by viewModel.studyMaterials.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Resource Download center", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
        materials.forEach { material ->
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(PremiumGold.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (material.type) {
                                "Video" -> Icons.Default.PlayCircle
                                "E-Book" -> Icons.Default.MenuBook
                                "PDF Notes" -> Icons.Default.Book
                                else -> Icons.Default.Description
                            },
                            contentDescription = null,
                            tint = LightPrimary
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(material.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("${material.type} • Batch: ${material.batchName}", fontSize = 12.sp, color = PremiumGray)
                        Text(material.description, fontSize = 11.sp, color = PremiumGray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    IconButton(
                        onClick = { /* simulated download complete action */ }
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download", tint = PremiumGold)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// 7. TESTS CATALOG & INTERACTIVE TEST TAKING PANEL
// -----------------------------------------------------------------------------------
@Composable
fun TestsCatalog(viewModel: AppViewModel, onStartTest: (Test) -> Unit) {
    val tests by viewModel.tests.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Mathematics Test Series Portal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
        tests.forEach { test ->
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(test.title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Category: ${test.type}", fontSize = 12.sp, color = PremiumGray)
                            Text("Duration: ${test.durationMinutes} minutes • 5 Sums", fontSize = 12.sp, color = PremiumGray)
                        }
                        Button(
                            onClick = { onStartTest(test) },
                            colors = ButtonDefaults.buttonColors(containerColor = PremiumGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Attempt Test", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TestTakerSimulation(
    test: Test,
    viewModel: AppViewModel,
    onCompleted: () -> Unit
) {
    // 5 custom preset math sums parser simulation
    val mockQuestions = listOf(
        Triple("Evaluate: 12% of 150", listOf("15", "18", "20", "22"), 1), // B
        Triple("If 3x + 9 = 27, find x.", listOf("x = 5", "x = 6", "x = 7", "x = 8"), 1), // B
        Triple("LCM of 12, 18, and 30 is:", listOf("90", "180", "240", "360"), 1), // B
        Triple("Find perimeter of square with side 15cm.", listOf("45 cm", "60 cm", "90 cm", "120 cm"), 1), // B
        Triple("Simplify: (2^5 * 3^2) / 2^3", listOf("36", "72", "18", "144"), 0) // A
    )

    var currentQIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var scoreValue by remember { mutableStateOf(0) }
    var testSubmitted by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (!testSubmitted) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(test.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LightPrimary, modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .background(PremiumGold.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Q: ${currentQIndex + 1}/5", fontSize = 11.sp, color = LightPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                // Question Statement
                val currentQ = mockQuestions[currentQIndex]
                Text(currentQ.first, fontSize = 15.sp, fontWeight = FontWeight.Medium)

                // Option Radio Buttons
                currentQ.second.forEachIndexed { optIndex, optText ->
                    val isChosen = selectedOption == optIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isChosen) 2.dp else 1.dp,
                                color = if (isChosen) PremiumGold else LightPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(
                                color = if (isChosen) PremiumGold.copy(alpha = 0.08f) else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedOption = optIndex }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RadioButton(
                            selected = isChosen,
                            onClick = { selectedOption = optIndex },
                            colors = RadioButtonDefaults.colors(selectedColor = PremiumGold)
                        )
                        Text(optText, fontSize = 13.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if (selectedOption != null) {
                                if (selectedOption == currentQ.third) {
                                    scoreValue++
                                }
                                if (currentQIndex < 4) {
                                    currentQIndex++
                                    selectedOption = null
                                } else {
                                    viewModel.submitTestResult(test.id, test.title, scoreValue, 5)
                                    testSubmitted = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                        shape = RoundedCornerShape(10.dp),
                        enabled = selectedOption != null
                    ) {
                        Text(if (currentQIndex == 4) "Finish Test" else "Next Sum", color = Color.White)
                    }
                }
            } else {
                // Score card display with golden award design
                Column(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(PremiumGold.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = PremiumGold, modifier = Modifier.size(52.dp))
                    }
                    Text("Instant Test Result", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
                    Text("Your Score: $scoreValue / 5 Correct Answers", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    
                    Text(
                        text = if (scoreValue >= 4) "Excellent Work! Keep up the great logic." else "Good try. Practice DPP notes to improve.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = PremiumGray
                    )

                    Button(
                        onClick = onCompleted,
                        colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Return to Dashboard", color = Color.White)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// 8. DOUBTS HUB
// -----------------------------------------------------------------------------------
@Composable
fun DoubtsHub(viewModel: AppViewModel) {
    val doubts by viewModel.doubts.collectAsState()
    var doubtText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Doubt Clarification Desk", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LightPrimary)

        // Post doubt box
        Card(
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Stuck with a complex sum? Post it below", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = doubtText,
                    onValueChange = { doubtText = it },
                    placeholder = { Text("Describe your doubt or write equation query...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Button(
                    onClick = {
                        if (doubtText.isNotBlank()) {
                            viewModel.createDoubt(doubtText)
                            doubtText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PremiumGold),
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Post Doubt", color = Color.White)
                }
            }
        }

        // List doubts
        doubts.forEach { doubt ->
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(doubt.studentName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
                        Box(
                            modifier = Modifier
                                .background(if (doubt.answer == null) Color.Red.copy(alpha = 0.1f) else Color.Green.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (doubt.answer == null) "Pending" else "Resolved",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (doubt.answer == null) Color.Red else Color.Green
                            )
                        }
                    }
                    Text("Q: ${doubt.question}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    if (doubt.answer != null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = LightBackground)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier.size(16.dp))
                            Column {
                                Text("A: ${doubt.answer}", fontSize = 12.sp)
                                Text("Answered by: ${doubt.answeredBy}", fontSize = 10.sp, color = PremiumGray)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// 9. UPI GATEWAY ONLINE PAYMENT CHECKOUT SIMULATION
// -----------------------------------------------------------------------------------
@Composable
fun UpiCheckoutSimulation(
    fee: Fee,
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf("GPay") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = PremiumGold, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text("Secure UPI checkout gateway", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Paying Tuition Fee: ${fee.studentName}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text("Transaction Amount: ₹${fee.amount}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Green)

                Text("Select Payment Gateway Mode:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PremiumGray)
                listOf("Google Pay", "PhonePe", "Paytm", "Credit/Debit Card").forEach { method ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (selectedMethod == method) 2.dp else 1.dp,
                                color = if (selectedMethod == method) PremiumGold else LightPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedMethod = method }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(selected = selectedMethod == method, onClick = { selectedMethod = method }, colors = RadioButtonDefaults.colors(selectedColor = PremiumGold))
                        Text(method, fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.payStudentFee(fee, selectedMethod)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Pay Securely", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// -----------------------------------------------------------------------------------
// 10. PREMIUM AUTO-GENERATED FEE RECEIPT
// -----------------------------------------------------------------------------------
@Composable
fun FeeReceiptDialog(
    fee: Fee,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.Green)
                Text("Auto-Generated Fee Receipt", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Card(
                border = BorderStroke(1.dp, PremiumGold),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "INSTITUTE MATHEMATICS CLASSES",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Gandhi Chowk, Nag Mandir • Mentor Nikhil Kumar",
                        fontSize = 9.sp,
                        color = PremiumGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider(color = PremiumGold, thickness = 1.dp)

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Student Name:", fontSize = 11.sp, color = PremiumGray)
                        Text(fee.studentName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Paid Date:", fontSize = 11.sp, color = PremiumGray)
                        Text(fee.paidDate ?: "Today", fontSize = 11.sp)
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Transaction ID:", fontSize = 11.sp, color = PremiumGray)
                        Text(fee.transactionId ?: "TXN889721", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Payment Mode:", fontSize = 11.sp, color = PremiumGray)
                        Text(fee.paymentMethod ?: "UPI-GPay", fontSize = 11.sp)
                    }

                    HorizontalDivider(color = LightBackground)

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Total Amount Paid:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LightPrimary)
                        Text("₹${fee.amount}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Green)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "★ Secured & Verified Digitally by Nikhil Kumar ★",
                        fontSize = 9.sp,
                        color = PremiumGold,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
            ) {
                Text("Dismiss Receipt", color = Color.White)
            }
        }
    )
}
