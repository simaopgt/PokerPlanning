package com.idk.feature_poker_planning.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.idk.feature_poker_planning.domain.model.Room
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRoomRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : RoomRepository {

    override fun observeRooms(): Flow<List<Room>> = callbackFlow {
        val sub = firestore.collection("rooms")
            .orderBy("createdAt")
            .addSnapshotListener { snaps, err ->
                if (err != null) close(err)
                else {
                    val list = snaps?.documents
                        ?.mapNotNull { doc ->
                            Room(
                                id = doc.id,
                                createdAt = doc.getTimestamp("createdAt")
                            )
                        } ?: emptyList()
                    trySend(list)
                }
            }
        awaitClose { sub.remove() }
    }

    override suspend fun createRoom() {
        val data = mapOf("createdAt" to FieldValue.serverTimestamp())
        firestore.collection("rooms").add(data).await()
    }
}
