package com.example.repository

import com.example.data.*
import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    val allUsers: Flow<List<User>> = appDao.getAllUsers()
    val allBatches: Flow<List<Batch>> = appDao.getAllBatches()
    val allLiveClasses: Flow<List<LiveClass>> = appDao.getAllLiveClasses()
    val allStudyMaterials: Flow<List<StudyMaterial>> = appDao.getAllStudyMaterials()
    val allTests: Flow<List<Test>> = appDao.getAllTests()
    val allTestSubmissions: Flow<List<TestSubmission>> = appDao.getAllTestSubmissions()
    val allHomeworkSubmissions: Flow<List<HomeworkSubmission>> = appDao.getAllHomeworkSubmissions()
    val allAttendance: Flow<List<Attendance>> = appDao.getAllAttendance()
    val allFees: Flow<List<Fee>> = appDao.getAllFees()
    val allDoubts: Flow<List<Doubt>> = appDao.getAllDoubts()
    val allNotifications: Flow<List<AppNotification>> = appDao.getAllNotifications()

    suspend fun getUserByPhone(phone: String): User? = appDao.getUserByPhone(phone)
    suspend fun getUserByEmail(email: String): User? = appDao.getUserByEmail(email)

    suspend fun insertUser(user: User): Long = appDao.insertUser(user)
    suspend fun deleteUser(user: User) = appDao.deleteUser(user)

    suspend fun insertBatch(batch: Batch): Long = appDao.insertBatch(batch)

    suspend fun insertLiveClass(liveClass: LiveClass): Long = appDao.insertLiveClass(liveClass)
    suspend fun deleteLiveClass(liveClass: LiveClass) = appDao.deleteLiveClass(liveClass)

    suspend fun insertStudyMaterial(material: StudyMaterial): Long = appDao.insertStudyMaterial(material)

    suspend fun insertTest(test: Test): Long = appDao.insertTest(test)

    suspend fun insertTestSubmission(submission: TestSubmission): Long = appDao.insertTestSubmission(submission)

    suspend fun insertHomeworkSubmission(submission: HomeworkSubmission): Long = appDao.insertHomeworkSubmission(submission)

    suspend fun insertAttendance(attendance: Attendance): Long = appDao.insertAttendance(attendance)

    suspend fun insertFee(fee: Fee): Long = appDao.insertFee(fee)
    suspend fun updateFee(fee: Fee) = appDao.updateFee(fee)

    suspend fun insertDoubt(doubt: Doubt): Long = appDao.insertDoubt(doubt)

    suspend fun insertNotification(notification: AppNotification): Long = appDao.insertNotification(notification)
}
