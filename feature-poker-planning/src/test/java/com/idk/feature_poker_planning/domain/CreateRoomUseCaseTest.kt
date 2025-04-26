package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.model.Room
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID

class CreateRoomUseCaseTest {
    private val repository: RoomRepository = mockk()
    private val useCase = CreateRoomUseCase(repository)

    @Before
    fun setup() {
        coJustRun { repository.createRoom(any()) }
    }

    @Test
    fun invoke_callsRepositoryWithProvidedName() = runTest {
        val customNameStub = TestDataProvider.defaultRoom.name
        useCase(customNameStub)

        val slot = slot<Room>()
        coVerify { repository.createRoom(capture(slot)) }

        val room = slot.captured
        assertEquals(customNameStub, room.name)
        assertTrue(room.id.isNotBlank())
        assertTrue(UUID.fromString(room.id).toString() == room.id)
        assertTrue(room.createdAt != null)
    }

    @Test
    fun invoke_callsRepositoryWithEmptyName_whenNameIsBlank() = runTest {
        useCase(TestDataProvider.emptyNameRoom.name)

        val slot = slot<Room>()
        coVerify { repository.createRoom(capture(slot)) }

        assertEquals("", slot.captured.name)
    }

    @Test
    fun invoke_callsRepositoryWithEmptyName_whenNameIsNull() = runTest {
        useCase(null)

        val slot = slot<Room>()
        coVerify { repository.createRoom(capture(slot)) }

        assertEquals("", slot.captured.name)
    }
}