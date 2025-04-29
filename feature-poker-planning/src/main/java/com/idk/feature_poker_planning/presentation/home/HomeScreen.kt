package com.idk.feature_poker_planning.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.idk.feature.poker.planning.R
import com.idk.feature_poker_planning.domain.model.Room
import com.idk.feature_poker_planning.utils.FirestoreConstants.AVATAR_ID_01
import com.idk.feature_poker_planning.utils.FirestoreConstants.AVATAR_ID_02
import com.idk.feature_poker_planning.utils.FirestoreConstants.AVATAR_ID_03
import com.idk.feature_poker_planning.utils.FirestoreConstants.AVATAR_ID_04
import com.idk.feature_poker_planning.utils.FirestoreConstants.AVATAR_ID_05
import com.idk.feature_poker_planning.utils.FirestoreConstants.AVATAR_ID_06

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(), onRoomClick: (String, String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var roomName by rememberSaveable { mutableStateOf("") }

    HomeScreen(
        state = state,
        showDialog = showDialog,
        roomName = roomName,
        modifier = Modifier.fillMaxSize(),
        onCreateRoomClick = {
            roomName = ""
            showDialog = true
        },
        onRoomNameChange = { roomName = it },
        onDismissDialog = { showDialog = false },
        onConfirmCreateRoom = {
            viewModel.createRoom(roomName.takeIf { it.isNotBlank() })
            showDialog = false
        },
        onRoomClick = onRoomClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    showDialog: Boolean,
    roomName: String,
    modifier: Modifier = Modifier,
    onCreateRoomClick: () -> Unit,
    onRoomNameChange: (String) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmCreateRoom: () -> Unit,
    onRoomClick: (String, String) -> Unit
) {
    Scaffold(
        modifier = modifier, topBar = {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val avatarId by remember(state.userAvatar) {
                        derivedStateOf {
                            when (state.userAvatar) {
                                AVATAR_ID_01 -> R.drawable.avatar_1
                                AVATAR_ID_02 -> R.drawable.avatar_2
                                AVATAR_ID_03 -> R.drawable.avatar_3
                                AVATAR_ID_04 -> R.drawable.avatar_4
                                AVATAR_ID_05 -> R.drawable.avatar_5
                                AVATAR_ID_06 -> R.drawable.avatar_6
                                else -> R.drawable.unknow
                            }
                        }
                    }
                    Image(
                        painter = painterResource(avatarId),
                        contentDescription = stringResource(
                            R.string.avatar_content_description, state.userName
                        ),
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.avatar_size))
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                    Text(
                        text = stringResource(R.string.home_greeting, state.userName),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = onCreateRoomClick,
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.fab_create_room)
            )
        }
    }, containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(dimensionResource(R.dimen.padding_standard))
        ) {
            Text(
                text = stringResource(R.string.home_room_list_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small)),
                contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.spacing_small))
            ) {
                items(state.rooms) { room ->
                    ListItem(
                        headlineContent = { Text(room.name) },
                        supportingContent = {
                            Text(
                                text = stringResource(R.string.home_room_id_prefix, room.id),
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRoomClick(room.id, room.name) }
                            .padding(vertical = dimensionResource(R.dimen.spacing_small)))
                    Divider(color = MaterialTheme.colorScheme.outline)
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = { Text(stringResource(R.string.dialog_title_room_name)) },
                text = {
                    OutlinedTextField(
                        value = roomName,
                        onValueChange = onRoomNameChange,
                        singleLine = true,
                        placeholder = {
                            Text(stringResource(R.string.dialog_placeholder_room_name))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = onConfirmCreateRoom) {
                        Text(stringResource(R.string.dialog_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDialog) {
                        Text(stringResource(R.string.dialog_cancel))
                    }
                })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val sampleState = HomeUiState(
        userName = "Marcos",
        userAvatar = "avatar_1",
        rooms = listOf(Room(id = "123", name = "Sprint Planning"))
    )
    HomeScreen(
        state = sampleState,
        showDialog = true,
        roomName = "",
        onCreateRoomClick = {},
        onRoomNameChange = {},
        onDismissDialog = {},
        onConfirmCreateRoom = {},
        onRoomClick = { _, _ -> },
        modifier = Modifier.fillMaxSize()
    )
}
