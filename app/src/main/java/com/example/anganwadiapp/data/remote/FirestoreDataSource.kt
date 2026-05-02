package com.example.anganwadiapp.data.remote

import com.example.anganwadiapp.data.remote.dto.ChildDto
import com.example.anganwadiapp.data.remote.dto.DietPlanDto
import com.example.anganwadiapp.data.remote.dto.StaffDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val childrenCollection = firestore.collection("children")
    private val staffCollection = firestore.collection("staff")

    // Staff Methods
    suspend fun registerStaffWithAuth(email: String, password: String, staff: StaffDto): String {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: throw IllegalStateException("Failed to get UID")
        val staffWithId = staff.copy(id = uid)
        staffCollection
            .document(staff.anganwadiCenterId)
            .collection("users")
            .document(uid)
            .set(staffWithId)
            .await()
        firestore.collection("user_centers").document(uid)
            .set(mapOf("anganwadiCenterId" to staff.anganwadiCenterId))
            .await()
        return uid
    }

    suspend fun loginStaffWithAuth(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    fun getCurrentUid(): String? = auth.currentUser?.uid

    suspend fun getAnganwadiCenterId(uid: String): String? {
        val centerDoc = firestore.collection("user_centers").document(uid).get().await()
        return centerDoc.getString("anganwadiCenterId")
    }

    suspend fun getStaffByUid(uid: String): StaffDto? {
        val centerId = getAnganwadiCenterId(uid) ?: return null
        val userDoc = staffCollection
            .document(centerId)
            .collection("users")
            .document(uid)
            .get()
            .await()
        return userDoc.toObject(StaffDto::class.java)?.copy(id = uid)
    }

    suspend fun logStaffAttendance(centerId: String, staffName: String, date: String, loginTime: String) {
        firestore.collection(centerId)
            .document("attendance_records")
            .collection(date)
            .document(staffName)
            .set(mapOf("loginTime" to loginTime))
            .await()
    }

    // Children Methods
    fun getChildrenFlow(): Flow<List<ChildDto>> = callbackFlow {
        val listener = childrenCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val children = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChildDto::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(children)
            }

        awaitClose { listener.remove() }
    }

    fun getChildByIdFlow(id: String): Flow<ChildDto?> = callbackFlow {
        val listener = childrenCollection.document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val child = snapshot?.toObject(ChildDto::class.java)?.copy(id = snapshot.id)
                trySend(child)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addChild(child: ChildDto): String {
        val docRef = childrenCollection.document()
        val childWithId = child.copy(id = docRef.id)
        docRef.set(childWithId).await()
        return docRef.id
    }

    suspend fun updateChild(child: ChildDto) {
        childrenCollection.document(child.id).set(child).await()
    }

    suspend fun deleteChild(id: String) {
        childrenCollection.document(id).delete().await()
    }

    fun getChildrenByCenterFlow(centerId: String): Flow<List<ChildDto>> = callbackFlow {
        val listener = firestore.collection(centerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val children = snapshot?.documents?.filter { doc ->
                    !doc.id.startsWith("students") && !doc.id.startsWith("attendance") && !doc.id.startsWith("student_attendance")
                }?.mapNotNull { doc ->
                    doc.toChildDto()
                } ?: emptyList()
                trySend(children)
            }

        awaitClose { listener.remove() }
    }

    suspend fun saveStudentAttendance(
        centerId: String,
        date: String,
        totalStudents: Int,
        totalPresent: Int,
        totalAbsent: Int,
        markedBy: String,
        presentStudents: List<Map<String, Any>>,
        absentStudents: List<Map<String, Any>>
    ) {
        firestore.collection(centerId)
            .document("student_attendance")
            .collection(date)
            .document("attendance_data")
            .set(
                mapOf(
                    "date" to date,
                    "totalStudents" to totalStudents,
                    "totalPresent" to totalPresent,
                    "totalAbsent" to totalAbsent,
                    "markedBy" to markedBy,
                    "presentStudents" to presentStudents,
                    "absentStudents" to absentStudents,
                    "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
            )
            .await()
    }

    suspend fun getTodayAttendance(centerId: String, date: String): Map<String, Any>? {
        val doc = firestore.collection(centerId)
            .document("student_attendance")
            .collection(date)
            .document("attendance_data")
            .get()
            .await()
        return if (doc.exists()) doc.data else null
    }

    private fun DocumentSnapshot.toChildDto(): ChildDto {
        val dobValue = get("dateOfBirth")
        val admissionValue = get("admissionDate")

        val dateOfBirth = when (dobValue) {
            is Long -> formatTimestamp(dobValue)
            is String -> dobValue
            else -> ""
        }

        val admissionDate = when (admissionValue) {
            is Long -> formatTimestamp(admissionValue)
            is String -> admissionValue
            else -> ""
        }

        return ChildDto(
            id = id,
            name = getString("name") ?: "",
            dateOfBirth = dateOfBirth,
            admissionDate = admissionDate,
            age = getString("age") ?: "",
            gender = getString("gender") ?: "OTHER",
            fatherName = getString("fatherName") ?: "",
            motherName = getString("motherName") ?: "",
            fatherMobile = getString("fatherMobile") ?: "",
            motherMobile = getString("motherMobile") ?: "",
            placeOfBirth = getString("placeOfBirth") ?: "",
            bloodGroup = getString("bloodGroup") ?: "",
            physicallyChallenged = getBoolean("physicallyChallenged") ?: false,
            height = getDouble("height")?.toFloat(),
            weight = getDouble("weight")?.toFloat(),
            allergies = getString("allergies") ?: "",
            healthNotes = getString("healthNotes") ?: "",
            anganwadiCenterId = getString("anganwadiCenterId") ?: "",
            photoUrl = getString("photoUrl")
        )
    }

    private fun DocumentSnapshot.getBoolean(field: String): Boolean? {
        val value = get(field)
        return when (value) {
            is Boolean -> value
            is Long -> value != 0L
            is Int -> value != 0
            else -> null
        }
    }

    private fun DocumentSnapshot.getDouble(field: String): Double? {
        val value = get(field)
        return when (value) {
            is Double -> value
            is Long -> value.toDouble()
            is Int -> value.toDouble()
            is Float -> value.toDouble()
            else -> null
        }
    }

    suspend fun updateChildEnrollment(child: ChildDto) {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User not authenticated")

        val anganwadiCenterId = getAnganwadiCenterId(uid)
            ?: throw IllegalStateException("Anganwadi center ID not found for user")

        val childData = child.copy(anganwadiCenterId = anganwadiCenterId)
        firestore.collection(anganwadiCenterId).document(child.id).set(childData).await()
    }

    suspend fun deleteChildEnrollment(childId: String) {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User not authenticated")

        val anganwadiCenterId = getAnganwadiCenterId(uid)
            ?: throw IllegalStateException("Anganwadi center ID not found for user")

        firestore.collection(anganwadiCenterId).document(childId).delete().await()
    }

    // Enrollment Methods
    suspend fun saveChildEnrollment(child: ChildDto): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User not authenticated")

        val anganwadiCenterId = getAnganwadiCenterId(uid)
            ?: throw IllegalStateException("Anganwadi center ID not found for user")

        val randomId = generateRandom10DigitId()

        val anganwadiCollection = firestore.collection(anganwadiCenterId)

        val childData = child.copy(id = randomId, anganwadiCenterId = anganwadiCenterId)
        anganwadiCollection.document(randomId).set(childData).await()

        val studentEntry = mapOf(
            "childId" to randomId,
            "name" to child.name,
            "fatherName" to child.fatherName,
            "fatherMobile" to child.fatherMobile
        )

        anganwadiCollection.document("students")
            .collection("records")
            .document(randomId)
            .set(studentEntry)
            .await()

        return randomId
    }

    private fun generateRandom10DigitId(): String {
        return (1_000_000_000L..9_999_999_999L).random().toString()
    }

    private fun formatTimestamp(millis: Long): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(java.util.Date(millis))
    }

    // Diet Plan Methods
    suspend fun saveDietPlan(anganwadiCenterId: String, date: String, dietPlan: DietPlanDto) {
        firestore.collection(anganwadiCenterId)
            .document("diet_plan")
            .collection(date)
            .document("day_log")
            .set(dietPlan)
            .await()
    }

    suspend fun getDietPlan(anganwadiCenterId: String, date: String): DietPlanDto? {
        val doc = firestore.collection(anganwadiCenterId)
            .document("diet_plan")
            .collection(date)
            .document("day_log")
            .get()
            .await()
        return if (doc.exists()) doc.toObject(DietPlanDto::class.java) else null
    }

    // Stock Management Methods
    suspend fun saveStockItem(
        anganwadiCenterId: String,
        date: String,
        stockType: String,
        itemData: Map<String, Any>
    ) {
        firestore.collection(anganwadiCenterId)
            .document("stocks")
            .collection(stockType)
            .document(date)
            .collection("items")
            .add(itemData)
            .await()
    }
}
