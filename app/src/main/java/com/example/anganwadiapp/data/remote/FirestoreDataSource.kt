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

    suspend fun getStaffByUid(uid: String): StaffDto? {
        val centerDoc = firestore.collection("user_centers").document(uid).get().await()
        val centerId = centerDoc.getString("anganwadiCenterId")
        if (centerId == null) return null
        val userDoc = staffCollection
            .document(centerId)
            .collection("users")
            .document(uid)
            .get()
            .await()
        return userDoc.toObject(StaffDto::class.java)?.copy(id = uid)
    }

    suspend fun logStaffAttendance(centerId: String, staffName: String, date: String, loginTime: String) {
        // Path: /{centerId}/attendance_records/{date}/{staffName}
        // centerId is the collection name
        firestore.collection(centerId)
            .document("attendance_records") // This doc acts as a parent for the sub-collection
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
}
