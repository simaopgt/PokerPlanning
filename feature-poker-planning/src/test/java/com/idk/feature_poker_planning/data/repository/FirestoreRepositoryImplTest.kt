package com.idk.feature_poker_planning.data.repository

import app.cash.turbine.test
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.idk.feature_poker_planning.domain.model.Room
import com.idk.feature_poker_planning.utils.FirestoreConstants
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FirestoreRepositoryImplTest {
    private val firestore = mockk<FirebaseFirestore>()
    private val collection = mockk<CollectionReference>()
    private val query = mockk<Query>()
    private val document = mockk<DocumentReference>()
    private val roomReg = mockk<ListenerRegistration>()
    private val partReg = mockk<ListenerRegistration>()
    private val roomSnapshotSlot = slot<EventListener<QuerySnapshot>>()
    private val partSnapshotSlot = slot<EventListener<DocumentSnapshot>>()
    private lateinit var repository: FirestoreRepositoryImpl
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        every { firestore.collection(FirestoreConstants.ROOMS_COLLECTION) } returns collection
        every {
            collection.orderBy(
                FirestoreConstants.CREATED_AT_FIELD, Query.Direction.ASCENDING
            )
        } returns query
        every { query.addSnapshotListener(capture(roomSnapshotSlot)) } returns roomReg
        every { collection.document(TestDataProvider.TEST_ROOM_ID) } returns document
        every { document.addSnapshotListener(capture(partSnapshotSlot)) } returns partReg
        repository = FirestoreRepositoryImpl(firestore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun invoke_emitsListOfRooms_whenSnapshotListenerFires() = runTest {
        repository.observeRooms().test {
            val fakeDoc = mockk<DocumentSnapshot> {
                every { id } returns TestDataProvider.TEST_ROOM_ID
                every { toObject(Room::class.java) } returns TestDataProvider.defaultRoom.copy(id = "")
            }
            val snapshot = mockk<QuerySnapshot> {
                every { documents } returns listOf(fakeDoc)
            }

            roomSnapshotSlot.captured.onEvent(snapshot, null)
            dispatcher.scheduler.advanceUntilIdle()

            val rooms = awaitItem()
            assertEquals(1, rooms.size)
            assertEquals(TestDataProvider.TEST_ROOM_ID, rooms[0].id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun invoke_emitsListOfParticipants_whenSnapshotListenerFires() = runTest {
        repository.observeParticipants(TestDataProvider.TEST_ROOM_ID).test {
            val snapshot = mockk<DocumentSnapshot>()
            every {
                snapshot.get(FirestoreConstants.PARTICIPANTS_FIELD)
            } returns TestDataProvider.participantsMaps

            partSnapshotSlot.captured.onEvent(snapshot, null)
            dispatcher.scheduler.advanceUntilIdle()

            val parts = awaitItem()
            assertEquals(TestDataProvider.participants, parts)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun invoke_setsMapWithRoomFields_whenCreateRoomInvoked() = runTest {
        val mapSlot = slot<Map<String, Any?>>()
        every { firestore.collection(FirestoreConstants.ROOMS_COLLECTION) } returns collection
        every { collection.document(TestDataProvider.TEST_ROOM_ID) } returns document
        every { document.set(capture(mapSlot)) } returns Tasks.forResult(null)

        repository.createRoom(TestDataProvider.defaultRoom)
        dispatcher.scheduler.advanceUntilIdle()

        val captured = mapSlot.captured
        assertEquals(TestDataProvider.defaultRoom.id, captured[FirestoreConstants.ROOM_ID_FIELD])
        assertEquals(
            TestDataProvider.defaultRoom.name, captured[FirestoreConstants.ROOM_NAME_FIELD]
        )
        assertEquals(
            TestDataProvider.defaultRoom.createdAt, captured[FirestoreConstants.CREATED_AT_FIELD]
        )
        @Suppress("UNCHECKED_CAST") val participantsList =
            captured[FirestoreConstants.PARTICIPANTS_FIELD] as List<Map<String, Any?>>
        assertEquals(TestDataProvider.defaultRoom.participants.size, participantsList.size)
    }

    @Test
    fun invoke_resetVotes_clearsAllVotesAndUpdatesInFirestore() = runTest {
        val raw = TestDataProvider.participantsMaps
        val snapshot = mockk<DocumentSnapshot>()
        every { snapshot.get(FirestoreConstants.PARTICIPANTS_FIELD) } returns raw

        every { document.get() } returns Tasks.forResult(snapshot)
        val updateSlot = slot<List<Map<String, Any?>>>()
        every {
            document.update(
                FirestoreConstants.PARTICIPANTS_FIELD, capture(updateSlot)
            )
        } returns Tasks.forResult(null)

        repository.resetVotes(TestDataProvider.TEST_ROOM_ID)
        dispatcher.scheduler.advanceUntilIdle()

        val cleared = updateSlot.captured
        assertEquals(raw.size, cleared.size)
        cleared.forEach { entry ->
            assertTrue(entry[FirestoreConstants.VOTE_FIELD] == null)
        }
    }
}
