package com.idk.feature_poker_planning.data.repository

import app.cash.turbine.test
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class FirestoreRepositoryTest {
    private val firestore = mockk<FirebaseFirestore>()
    private val collection = mockk<CollectionReference>()
    private val query = mockk<Query>()
    private val registration = mockk<ListenerRegistration>()
    private val snapshotListenerSlot = slot<EventListener<QuerySnapshot>>()

    private val roomDocumentSnapshot: DocumentSnapshot = mockk {
        every { toObject(Room::class.java) } returns TestDataProvider.defaultRoom.copy(id = "")
        every { id } returns TestDataProvider.defaultRoom.id
    }
    private val roomsSnapshot: QuerySnapshot = mockk {
        every { documents } returns listOf(roomDocumentSnapshot)
    }

    private lateinit var repository: FirestoreRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { firestore.collection(FirestoreConstants.ROOMS_COLLECTION) } returns collection
        every {
            collection.orderBy(
                FirestoreConstants.CREATED_AT_FIELD, Query.Direction.ASCENDING
            )
        } returns query
        every { query.addSnapshotListener(capture(snapshotListenerSlot)) } returns registration
        repository = FirestoreRepository(firestore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun observeRooms_emitsRoomsList_whenSnapshotIsValid() = runTest {
        val flow = repository.observeRooms()
        flow.test {
            snapshotListenerSlot.captured.onEvent(roomsSnapshot, null)
            testDispatcher.scheduler.advanceUntilIdle()

            val rooms = awaitItem()
            assertEquals(1, rooms.size)
            assertEquals(TestDataProvider.defaultRoom.id, rooms[0].id)
            assertEquals(TestDataProvider.defaultRoom.name, rooms[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun createRoom_invokesSetWithCorrectRoom() = runTest {
        val idSlot = slot<String>()
        val roomSlot = slot<Room>()

        every {
            firestore.collection(FirestoreConstants.ROOMS_COLLECTION).document(capture(idSlot))
                .set(capture(roomSlot))
        } returns Tasks.forResult(null)

        val generatedId = UUID.randomUUID().toString()
        val testRoom =
            TestDataProvider.defaultRoom.copy(id = generatedId, createdAt = Timestamp.now())

        repository.createRoom(testRoom)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(generatedId, idSlot.captured)
        assertEquals(TestDataProvider.defaultRoom.name, roomSlot.captured.name)
    }
}
