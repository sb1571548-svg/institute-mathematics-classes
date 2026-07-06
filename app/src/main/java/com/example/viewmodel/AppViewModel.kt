package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    
    // UI State for Current Session
    private val _currentUserSession = MutableStateFlow<User?>(null)
    val currentUserSession: StateFlow<User?> = _currentUserSession.asStateFlow()

    // Shared UI state for live class interactive whiteboard
    val whiteboardPaths = mutableStateListOf<Pair<Float, Float>>() // Simple path drawing coordinate simulation
    private val _liveClassChat = MutableStateFlow<List<Pair<String, String>>>(emptyList()) // Name, message
    val liveClassChat: StateFlow<List<Pair<String, String>>> = _liveClassChat.asStateFlow()
    
    private val _isScreenSharing = MutableStateFlow(false)
    val isScreenSharing: StateFlow<Boolean> = _isScreenSharing.asStateFlow()

    private val _isHandRaised = MutableStateFlow(false)
    val isHandRaised: StateFlow<Boolean> = _isHandRaised.asStateFlow()

    private val _isTeacherRecording = MutableStateFlow(false)
    val isTeacherRecording: StateFlow<Boolean> = _isTeacherRecording.asStateFlow()

    // OTP Verification simulator states
    private val _generatedOtp = MutableStateFlow<String?>(null)
    val generatedOtp: StateFlow<String?> = _generatedOtp.asStateFlow()

    // Exposed Flows from Room
    val users: StateFlow<List<User>>
    val batches: StateFlow<List<Batch>>
    val liveClasses: StateFlow<List<LiveClass>>
    val studyMaterials: StateFlow<List<StudyMaterial>>
    val tests: StateFlow<List<Test>>
    val testSubmissions: StateFlow<List<TestSubmission>>
    val homeworkSubmissions: StateFlow<List<HomeworkSubmission>>
    val attendanceList: StateFlow<List<Attendance>>
    val fees: StateFlow<List<Fee>>
    val doubts: StateFlow<List<Doubt>>
    val notifications: StateFlow<List<AppNotification>>

    init {
        val database = DatabaseModule.getDatabase(application)
        repository = AppRepository(database.appDao())

        users = repository.allUsers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        batches = repository.allBatches.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        liveClasses = repository.allLiveClasses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        studyMaterials = repository.allStudyMaterials.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        tests = repository.allTests.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        testSubmissions = repository.allTestSubmissions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        homeworkSubmissions = repository.allHomeworkSubmissions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        attendanceList = repository.allAttendance.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        fees = repository.allFees.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        doubts = repository.allDoubts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        notifications = repository.allNotifications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Check and pre-populate if needed
        viewModelScope.launch {
            batches.first { true } // Wait for flow collection to begin
            prepopulateIfNeeded()
        }
    }

    private suspend fun prepopulateIfNeeded() {
        val currentUsers = users.value
        if (currentUsers.isEmpty()) {
            // 1. Prepopulate default users
            repository.insertUser(User(name = "Nikhil Kumar", email = "sb1571548@gmail.com", phone = "7061157094", role = "Teacher", className = "All Classes"))
            repository.insertUser(User(name = "Admin Master", email = "admin@institute.com", phone = "1111111111", role = "Admin", className = "All Classes"))
            repository.insertUser(User(name = "Rahul Sharma", email = "rahul@gmail.com", phone = "9999999999", role = "Student", className = "Class 8"))
            repository.insertUser(User(name = "Sunita Sharma", email = "sunita@gmail.com", phone = "8888888888", role = "Parent", className = "Class 8"))

            // 2. Prepopulate default batches
            repository.insertBatch(Batch(name = "Class 8 Algebra Elite", gradeClass = "Class 8", timing = "05:00 PM - 06:30 PM"))
            repository.insertBatch(Batch(name = "Class 5 Mathematics Base", gradeClass = "Class 5", timing = "07:00 AM - 08:00 AM"))
            repository.insertBatch(Batch(name = "Class 6 Geometry Advance", gradeClass = "Class 6", timing = "02:00 PM - 03:00 PM"))

            // 3. Prepopulate default study materials
            repository.insertStudyMaterial(StudyMaterial(
                title = "Fractions & Decimals Concept Video",
                type = "Video",
                batchName = "Class 5 Mathematics Base",
                contentUrl = "https://youtube.com/fractions",
                description = "Master fractions visually with pie charts."
            ))
            repository.insertStudyMaterial(StudyMaterial(
                title = "Class 8 Quadratic Equations Cheatsheet",
                type = "PDF Notes",
                batchName = "Class 8 Algebra Elite",
                contentUrl = "quadratic_cheatsheet.pdf",
                description = "Formula summary and key Olympiad problem statements."
            ))
            repository.insertStudyMaterial(StudyMaterial(
                title = "DPP-1: Linear Equations in One Variable",
                type = "DPP",
                batchName = "Class 8 Algebra Elite",
                contentUrl = "dpp_linear_1.pdf",
                description = "Daily Practice Problems containing 10 critical sums."
            ))
            repository.insertStudyMaterial(StudyMaterial(
                title = "Homework-1: Class 6 Angles Drawing",
                type = "Homework",
                batchName = "Class 6 Geometry Advance",
                contentUrl = "hw_angles.pdf",
                description = "Draw obtuse, acute and reflex angles as described."
            ))

            // 4. Prepopulate default tests
            val class8Questions = """
                [
                  {"q":"Solve for x: 3x - 7 = 8","a":"x = 5","b":"x = 3","c":"x = 15","d":"x = -1","ans":"A"},
                  {"q":"What is the expansion of (a - b)^2?","a":"a^2 - b^2","b":"a^2 + b^2 - 2ab","c":"a^2 + b^2","d":"a^2 - 2ab - b^2","ans":"B"},
                  {"q":"If a number is multiplied by 3 and increased by 5, it is 26. What is the number?","a":"x = 6","b":"x = 7","c":"x = 8","d":"x = 9","ans":"B"},
                  {"q":"Find the degrees in a straight angle.","a":"90","b":"180","c":"360","d":"45","ans":"B"},
                  {"q":"Value of x inside 2x + 10 = 40 is?","a":"10","b":"15","c":"20","d":"25","ans":"B"}
                ]
            """.trimIndent()
            repository.insertTest(Test(
                title = "Linear Equations & Algebra Core Test",
                type = "Chapter Test",
                totalQuestions = 5,
                durationMinutes = 15,
                questionsJson = class8Questions,
                batchName = "Class 8 Algebra Elite"
            ))

            val olympiadQuestions = """
                [
                  {"q":"Find the unit digit of 3^2026.","a":"3","b":"9","c":"7","d":"1","ans":"B"},
                  {"q":"How many primes exist between 1 and 20?","a":"7","b":"8","c":"9","d":"10","ans":"B"},
                  {"q":"Find LCM of 12, 18, and 30.","a":"90","b":"180","c":"360","d":"120","ans":"B"},
                  {"q":"Sum of exterior angles of any convex polygon is?","a":"180","b":"360","c":"540","d":"720","ans":"B"},
                  {"q":"Simplify: (2^5 * 3^2) / 2^3","a":"36","b":"72","c":"18","d":"144","ans":"A"}
                ]
            """.trimIndent()
            repository.insertTest(Test(
                title = "Olympiad Practice: Number Theory Elite",
                type = "Olympiad Practice",
                totalQuestions = 5,
                durationMinutes = 20,
                questionsJson = olympiadQuestions,
                batchName = "Class 8 Algebra Elite"
            ))

            // 5. Prepopulate default live classes
            repository.insertLiveClass(LiveClass(
                title = "Algebra Foundations: Quadratic Formula",
                batchName = "Class 8 Algebra Elite",
                mentorName = "Nikhil Kumar",
                scheduledTime = "Today, 5:00 PM",
                isLive = true
            ))
            repository.insertLiveClass(LiveClass(
                title = "Class 5: Understanding Decimal Places",
                batchName = "Class 5 Mathematics Base",
                mentorName = "Nikhil Kumar",
                scheduledTime = "Tomorrow, 7:00 AM",
                isLive = false,
                recordingUrl = "https://youtube.com/recording_decimal"
            ))

            // 6. Prepopulate doubts
            repository.insertDoubt(Doubt(
                studentName = "Rahul Sharma",
                question = "How do we prove that the sum of angles in a triangle is 180 degrees using parallel lines?",
                answer = "Draw a line parallel to the base through the top vertex. The alternate interior angles are equal to the base angles, making a straight line of 180 degrees.",
                answeredBy = "Nikhil Kumar"
            ))

            // 7. Prepopulate fees
            repository.insertFee(Fee(
                studentName = "Rahul Sharma",
                amount = 1500.00,
                dueDate = "2026-07-15",
                status = "Due"
            ))

            // 8. Prepopulate notifications
            repository.insertNotification(AppNotification(
                title = "Welcome Alert",
                content = "Welcome to Institute Mathematics Classes by Mentor Nikhil Kumar! Let's conquer math together.",
                type = "Announcement"
            ))
            repository.insertNotification(AppNotification(
                title = "New Class Scheduled",
                content = "Teacher Nikhil Kumar scheduled 'Quadratic Formula' live class for Class 8 at 5:00 PM.",
                type = "Live Class"
            ))
        }
    }

    // Role switcher helper
    fun switchUserRole(role: String) {
        viewModelScope.launch {
            val user = users.value.find { it.role == role }
            if (user != null) {
                _currentUserSession.value = user
            } else {
                // Register a mock user for this role on-the-fly
                val newUser = when(role) {
                    "Admin" -> User(name = "Admin Master", email = "admin@institute.com", phone = "1111111111", role = "Admin", className = "All")
                    "Teacher" -> User(name = "Nikhil Kumar", email = "sb1571548@gmail.com", phone = "7061157094", role = "Teacher", className = "All")
                    "Student" -> User(name = "Rahul Sharma", email = "rahul@gmail.com", phone = "9999999999", role = "Student", className = "Class 8")
                    "Parent" -> User(name = "Sunita Sharma", email = "sunita@gmail.com", phone = "8888888888", role = "Parent", className = "Class 8")
                    else -> User(name = "Guest User", email = "guest@gmail.com", phone = "1234567890", role = "Student", className = "Class 1")
                }
                repository.insertUser(newUser)
                _currentUserSession.value = newUser
            }
        }
    }

    // AUTH ACTIONS
    fun loginOrRegister(name: String, email: String, phone: String, role: String, className: String) {
        viewModelScope.launch {
            val existing = repository.getUserByPhone(phone)
            if (existing != null) {
                _currentUserSession.value = existing
            } else {
                val newUser = User(name = name, email = email, phone = phone, role = role, className = className)
                repository.insertUser(newUser)
                _currentUserSession.value = newUser
            }
            _generatedOtp.value = null
        }
    }

    fun logout() {
        _currentUserSession.value = null
    }

    fun sendMockOtp(phone: String) {
        _generatedOtp.value = (1000..9999).random().toString()
    }

    // LIVE CLASS PANEL SIMULATOR ACTIONS
    fun sendLiveMessage(name: String, message: String) {
        val updated = _liveClassChat.value.toMutableList()
        updated.add(Pair(name, message))
        _liveClassChat.value = updated
    }

    fun clearWhiteboard() {
        whiteboardPaths.clear()
    }

    fun addWhiteboardPoint(x: Float, y: Float) {
        whiteboardPaths.add(Pair(x, y))
    }

    fun toggleScreenShare() {
        _isScreenSharing.value = !_isScreenSharing.value
    }

    fun toggleHandRaise() {
        _isHandRaised.value = !_isHandRaised.value
    }

    fun toggleTeacherRecording() {
        _isTeacherRecording.value = !_isTeacherRecording.value
    }

    // TEACHER & ADMIN CREATION ACTIONS
    fun addBatch(name: String, gradeClass: String, timing: String) {
        viewModelScope.launch {
            repository.insertBatch(Batch(name = name, gradeClass = gradeClass, timing = timing))
            repository.insertNotification(AppNotification(
                title = "New Batch Formed",
                content = "A new batch '$name' is now open for $gradeClass admissions.",
                type = "Announcement"
            ))
        }
    }

    fun createLiveClass(title: String, batchName: String, scheduledTime: String) {
        viewModelScope.launch {
            repository.insertLiveClass(LiveClass(
                title = title,
                batchName = batchName,
                mentorName = _currentUserSession.value?.name ?: "Nikhil Kumar",
                scheduledTime = scheduledTime,
                isLive = true
            ))
            repository.insertNotification(AppNotification(
                title = "Live Class Scheduled",
                content = "Live class scheduled: '$title' for batch $batchName.",
                type = "Live Class"
            ))
        }
    }

    fun deleteLiveClass(liveClass: LiveClass) {
        viewModelScope.launch {
            repository.deleteLiveClass(liveClass)
        }
    }

    fun uploadStudyMaterial(title: String, type: String, batchName: String, contentUrl: String, description: String) {
        viewModelScope.launch {
            repository.insertStudyMaterial(StudyMaterial(
                title = title,
                type = type,
                batchName = batchName,
                contentUrl = contentUrl,
                description = description
            ))
            repository.insertNotification(AppNotification(
                title = "Study Material Uploaded",
                content = "New $type uploaded: '$title' for batch $batchName.",
                type = "Homework"
            ))
        }
    }

    fun createTest(title: String, type: String, batchName: String, duration: Int, questionsJson: String) {
        viewModelScope.launch {
            repository.insertTest(Test(
                title = title,
                type = type,
                batchName = batchName,
                totalQuestions = 5, // Simulated 5 questions
                durationMinutes = duration,
                questionsJson = questionsJson,
            ))
            repository.insertNotification(AppNotification(
                title = "New Test Available",
                content = "A new $type: '$title' has been posted.",
                type = "Test"
            ))
        }
    }

    // STUDENT ACTIONS
    fun submitTestResult(testId: Int, testTitle: String, score: Int, totalQuestions: Int) {
        viewModelScope.launch {
            val studentName = _currentUserSession.value?.name ?: "Student"
            repository.insertTestSubmission(TestSubmission(
                testId = testId,
                testTitle = testTitle,
                studentName = studentName,
                score = score,
                totalQuestions = totalQuestions
            ))
            repository.insertNotification(AppNotification(
                title = "Test Submitted",
                content = "Student $studentName scored $score/$totalQuestions on $testTitle.",
                type = "Test"
            ))
        }
    }

    fun submitHomework(materialId: Int, materialTitle: String) {
        viewModelScope.launch {
            val studentName = _currentUserSession.value?.name ?: "Student"
            repository.insertHomeworkSubmission(HomeworkSubmission(
                materialId = materialId,
                materialTitle = materialTitle,
                studentName = studentName,
                status = "Submitted"
            ))
        }
    }

    fun submitAdmissionForm(name: String, email: String, phone: String, className: String) {
        viewModelScope.launch {
            val newUser = User(name = name, email = email, phone = phone, role = "Student", className = className)
            repository.insertUser(newUser)
            _currentUserSession.value = newUser
            
            // Auto schedule a welcome fee
            repository.insertFee(Fee(
                studentName = name,
                amount = 2000.0,
                dueDate = "Today + 10 Days",
                status = "Due"
            ))

            repository.insertNotification(AppNotification(
                title = "New Admission",
                content = "Welcome student $name to $className! Batch selection unlocked.",
                type = "Announcement"
            ))
        }
    }

    // TEACHER GRADING & ATTENDANCE ACTIONS
    fun markStudentAttendance(studentName: String, date: String, status: String, batchName: String) {
        viewModelScope.launch {
            repository.insertAttendance(Attendance(
                studentName = studentName,
                date = date,
                status = status,
                batchName = batchName
            ))
        }
    }

    fun gradeHomework(submission: HomeworkSubmission, grade: String) {
        viewModelScope.launch {
            val updated = submission.copy(status = "Graded", grade = grade)
            repository.insertHomeworkSubmission(updated)
            repository.insertNotification(AppNotification(
                title = "Homework Graded",
                content = "Homework for ${submission.studentName} graded: $grade.",
                type = "Homework"
            ))
        }
    }

    // PARENT FEE PAYMENT SIMULATOR
    fun payStudentFee(fee: Fee, paymentMethod: String) {
        viewModelScope.launch {
            val transactionId = "TXN" + (100000..999999).random()
            val updatedFee = fee.copy(
                status = "Paid",
                paidDate = "Today",
                paymentMethod = paymentMethod,
                transactionId = transactionId
            )
            repository.updateFee(updatedFee)
            repository.insertNotification(AppNotification(
                title = "Fee Received Successfully",
                content = "Fee of ₹${fee.amount} paid by ${fee.studentName} via $paymentMethod.",
                type = "Fee Due"
            ))
        }
    }

    fun createDoubt(question: String) {
        viewModelScope.launch {
            val studentName = _currentUserSession.value?.name ?: "Student"
            repository.insertDoubt(Doubt(
                studentName = studentName,
                question = question
            ))
        }
    }

    fun replyToDoubt(doubt: Doubt, reply: String) {
        viewModelScope.launch {
            val teacherName = _currentUserSession.value?.name ?: "Nikhil Kumar"
            val updatedDoubt = doubt.copy(
                answer = reply,
                answeredBy = teacherName
            )
            // Save updated doubt
            val doubtId = repository.insertDoubt(updatedDoubt)
            repository.insertNotification(AppNotification(
                title = "Doubt Resolved",
                content = "$teacherName answered a doubt by ${doubt.studentName}.",
                type = "Announcement"
            ))
        }
    }

    fun postNotice(title: String, content: String, type: String) {
        viewModelScope.launch {
            repository.insertNotification(AppNotification(
                title = title,
                content = content,
                type = type
            ))
        }
    }
}
