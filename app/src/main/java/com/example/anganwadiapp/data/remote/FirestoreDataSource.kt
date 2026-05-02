package com.example.anganwadiapp.data.remote

import com.example.anganwadiapp.data.remote.dto.ChildDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
