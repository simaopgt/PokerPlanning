package com.idk.feature_poker_planning.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.idk.feature_poker_planning.domain.model.Participant
import com.idk.feature_poker_planning.domain.model.Room
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import com.idk.feature_poker_planning.utils.FirestoreConstants.CREATED_AT_FIELD
import com.idk.feature_poker_planning.utils.FirestoreConstants.PARTICIPANTS_FIELD
import com.idk.feature_poker_planning.utils.FirestoreConstants.ROOMS_COLLECTION
import com.idk.feature_poker_planning.utils.FirestoreConstants.ROOM_ID_FIELD
import com.idk.feature_poker_planning.utils.FirestoreConstants.ROOM_NAME_FIELD
import com.idk.feature_poker_planning.utils.FirestoreConstants.USER_AVATAR_FIELD
import com.idk.feature_poker_planning.utils.FirestoreConstants.USER_ID_FIELD
import com.idk.feature_poker_planning.utils.FirestoreConstants.USER_NAME_FIELD
import com.idk.feature_poker_planning.utils.FirestoreConstants.VOTE_FIELD
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class FirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RoomRepository {

    override suspend fun createRoom(room: Room) {
        val roomData = mapOf(
            ROOM_ID_FIELD to room.id,
            ROOM_NAME_FIELD to room.name,
            CREATED_AT_FIELD to room.createdAt,
            PARTICIPANTS_FIELD to room.participants.map { participant ->
                mapOf(
                    USER_ID_FIELD to participant.userId,
                    USER_NAME_FIELD to participant.name,
                    USER_AVATAR_FIELD to participant.avatar,
                    VOTE_FIELD to null
                )
            })
        firestore.collection(ROOMS_COLLECTION).document(room.id).set(roomData).await()
    }

    override fun observeRooms(): Flow<List<Room>> = callbackFlow {
        val listenerRegistration = firestore.collection(ROOMS_COLLECTION)
            .orderBy(CREATED_AT_FIELD, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, firestoreError ->
                if (firestoreError != null) {
                    close(firestoreError)
                } else {
                    val rooms = snapshots?.documents?.mapNotNull { documentSnapshot ->
                        documentSnapshot.toObject(Room::class.java)?.copy(id = documentSnapshot.id)
                    } ?: emptyList()
                    trySend(rooms)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun addParticipant(
        roomId: String, userId: String, name: String, avatar: String
    ) {
        val participantEntry = mapOf(
            USER_ID_FIELD to userId,
            USER_NAME_FIELD to name,
            USER_AVATAR_FIELD to avatar,
            VOTE_FIELD to null
        )
        firestore.collection(ROOMS_COLLECTION).document(roomId)
            .update(PARTICIPANTS_FIELD, FieldValue.arrayUnion(participantEntry)).await()
    }

    override fun observeParticipants(roomId: String): Flow<List<Participant>> = callbackFlow {
        val listenerRegistration = firestore.collection(ROOMS_COLLECTION).document(roomId)
            .addSnapshotListener { documentSnapshot, firestoreError ->
                if (firestoreError != null) {
                    close(firestoreError)
                } else {
                    val rawParticipants =
                        documentSnapshot?.get(PARTICIPANTS_FIELD) as? List<Map<String, Any>>
                            ?: emptyList()
                    val participants = rawParticipants.mapNotNull { data ->
                        val id = data[USER_ID_FIELD] as? String
                        val name = data[USER_NAME_FIELD] as? String
                        val avatar = data[USER_AVATAR_FIELD] as? String ?: ""
                        val vote = (data[VOTE_FIELD] as? Number)?.toInt()
                        if (id != null && name != null) Participant(id, name, avatar, vote)
                        else null
                    }
                    trySend(participants).isSuccess
                }
            }
        awaitClose { listenerRegistration.remove() }
    }.distinctUntilChanged()

    override suspend fun submitVote(
        roomId: String, userId: String, name: String, avatar: String, vote: Int
    ) {
        val documentRef = firestore.collection(ROOMS_COLLECTION).document(roomId)

        val documentSnapshot = documentRef.get().await()

        val rawParticipants =
            documentSnapshot.get(PARTICIPANTS_FIELD) as? List<Map<String, Any>> ?: emptyList()

        val updatedParticipants = rawParticipants.map { entry ->
            if (entry[USER_ID_FIELD] == userId) {
                mutableMapOf<String, Any?>().apply {
                    putAll(entry)
                    put(VOTE_FIELD, vote)
                }
            } else entry
        }

        documentRef.update(PARTICIPANTS_FIELD, updatedParticipants).await()
    }
}
