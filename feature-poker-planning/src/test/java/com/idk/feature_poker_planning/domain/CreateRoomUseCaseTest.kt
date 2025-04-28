package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.model.Room
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertSame
import junit.framework.Assert.assertTrue
import junit.framework.Assert.fail
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
    fun invoke_callsRepositoryWithProvidedName_whenNameIsNotBlank() = runTest {
        val customName = TestDataProvider.defaultRoom.name
        useCase(customName)

        val slot = slot<Room>()
        coVerify { repository.createRoom(capture(slot)) }

        val room = slot.captured
        assertEquals(customName, room.name)
        assertTrue(room.id.isNotBlank())
        assertEquals(room.id, UUID.fromString(room.id).toString())
        assertNotNull(room.createdAt)
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

    @Test
    fun invoke_callsRepositoryWithEmptyName_whenNoNameProvided() = runTest {
        useCase()

        val slot = slot<Room>()
        coVerify { repository.createRoom(capture(slot)) }

        assertEquals("", slot.captured.name)
    }

    @Test
    fun invoke_propagatesException_whenRepositoryThrows() = runTest {
        val error = TestDataProvider.testError
        coEvery { repository.createRoom(any()) } throws error

        try {
            useCase("whatever")
            fail("Expected exception")
        } catch (e: RuntimeException) {
            assertSame(error, e)
        }
    }
}
