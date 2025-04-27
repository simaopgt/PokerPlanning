package com.idk.feature_poker_planning.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.idk.feature_poker_planning.domain.model.Participant
import com.idk.feature_poker_planning.domain.model.Room
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import com.idk.feature_poker_planning.utils.FirestoreConstants.CREATED_AT_FIELD
import com.idk.feature_poker_planning.utils.FirestoreConstants.NAME_FIELD
import com.idk.feature_poker_planning.utils.FirestoreConstants.PARTICIPANTS_FIELD
import com.idk.feature_poker_planning.utils.FirestoreConstants.ROOMS_COLLECTION
import com.idk.feature_poker_planning.utils.FirestoreConstants.USER_ID_FIELD
import com.idk.feature_poker_planning.utils.FirestoreConstants.VOTE_FIELD
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RoomRepository {

    override fun observeRooms(): Flow<List<Room>> = callbackFlow {
        val listenerRegistration = firestore.collection(ROOMS_COLLECTION)
            .orderBy(CREATED_AT_FIELD, Query.Direction.ASCENDING)
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
        firestore.collection(ROOMS_COLLECTION).document(room.id).set(room)
            .await()
    }

    override fun observeParticipants(roomId: String): Flow<List<Participant>> = callbackFlow {
        val registration =
            firestore.collection(ROOMS_COLLECTION).document(roomId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                    } else {
                        val rawList =
                            snapshot?.get(PARTICIPANTS_FIELD) as? List<Map<String, Any>>
                                ?: emptyList()
                        val participants = rawList.mapNotNull { data ->
                            val id = data[USER_ID_FIELD] as? String
                            val name = data[NAME_FIELD] as? String
                            val vote = (data[VOTE_FIELD] as? Long)?.toInt()
                            if (id != null && name != null) Participant(id, name, vote) else null
                        }
                        trySend(participants).isSuccess
                    }
                }
        awaitClose { registration.remove() }
    }.distinctUntilChanged()

    override suspend fun submitVote(roomId: String, userId: String, vote: Int) {
        val docRef = firestore.collection(ROOMS_COLLECTION).document(roomId)
        val removeEntry = mapOf(
            USER_ID_FIELD to userId, NAME_FIELD to (userNamePlaceholder()), VOTE_FIELD to null
        )
        val unionEntry = mapOf(
            USER_ID_FIELD to userId,
            NAME_FIELD to (userNamePlaceholder()),
            VOTE_FIELD to vote
        )
        docRef
            .update(
                "participants",
                FieldValue.arrayRemove(removeEntry),
                "participants",
                FieldValue.arrayUnion(unionEntry)
            )
            .await()
    }

    private fun userNamePlaceholder(): String = ""
}
