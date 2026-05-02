package com.example.anganwadiapp.data.remote

import com.example.anganwadiapp.data.remote.dto.ChildDto
import com.example.anganwadiapp.data.remote.dto.StaffDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
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
}
