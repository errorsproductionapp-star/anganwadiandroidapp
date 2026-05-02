package com.example.anganwadiapp.data.remote

import com.example.anganwadiapp.data.remote.dto.ChildDto
import com.example.anganwadiapp.data.remote.dto.StaffDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val childrenCollection = firestore.collection("children")
    private val staffCollection = firestore.collection("staff")

    // Staff Methods
    suspend fun registerStaff(staff: StaffDto) {
        val docRef = if (staff.id.isEmpty()) staffCollection.document() else staffCollection.document(staff.id)
        val staffWithId = if (staff.id.isEmpty()) staff.copy(id = docRef.id) else staff
        docRef.set(staffWithId).await()
    }

    suspend fun getStaffByEmail(email: String): StaffDto? {
        val query = staffCollection.whereEqualTo("email", email).get().await()
        return query.documents.firstOrNull()?.toObject(StaffDto::class.java)
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
