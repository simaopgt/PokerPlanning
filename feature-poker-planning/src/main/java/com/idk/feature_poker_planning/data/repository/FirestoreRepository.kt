package com.idk.feature_poker_planning.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.idk.feature_poker_planning.domain.model.Room
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import com.idk.feature_poker_planning.utils.FirestoreConstants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : RoomRepository {

    override fun observeRooms(): Flow<List<Room>> = callbackFlow {
        val listenerRegistration = firestore.collection(FirestoreConstants.ROOMS_COLLECTION)
            .orderBy(FirestoreConstants.CREATED_AT_FIELD, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    close(error)
                } else {
                    val list = snapshots?.documents?.mapNotNull { documentSnapshot ->
                        documentSnapshot.toObject(Room::class.java)?.copy(id = documentSnapshot.id)
                    } ?: emptyList()
                    trySend(list)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun createRoom(room: Room) {
        firestore.collection(FirestoreConstants.ROOMS_COLLECTION).document(room.id).set(room)
            .await()
    }
}
