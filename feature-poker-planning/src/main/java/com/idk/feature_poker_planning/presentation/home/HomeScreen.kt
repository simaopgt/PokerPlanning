package com.idk.feature_poker_planning.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.idk.feature.poker.planning.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onRoomClick: (roomId: String, roomName: String) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var roomName by remember { mutableStateOf("") }

    BackHandler { /* nothing to do */ }

    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val avatarId = remember(state.userAvatar) {
                        when (state.userAvatar) {
                            "avatar_1" -> R.drawable.avatar_1
                            "avatar_2" -> R.drawable.avatar_2
                            "avatar_3" -> R.drawable.avatar_3
                            "avatar_4" -> R.drawable.avatar_4
                            "avatar_5" -> R.drawable.avatar_5
                            "avatar_6" -> R.drawable.avatar_6
                            else -> R.drawable.unknow
                        }
                    }
                    Image(
                        painter = painterResource(id = avatarId),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Olá, ${state.userName}")
                }
            })
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            roomName = ""
            showDialog = true
        }) {
            Icon(Icons.Default.Add, contentDescription = "Create Room")
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Room list", style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(state.rooms) { room ->
                    ListItem(
                        headlineContent = { Text(room.name) },
                        supportingContent = {
                            Text(
                                text = "ID: ${room.id}", style = MaterialTheme.typography.bodySmall
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRoomClick(room.id, room.name) }
                            .padding(vertical = 4.dp))
                    HorizontalDivider()
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Room Name") },
                text = {
                    OutlinedTextField(
                        value = roomName,
                        onValueChange = { roomName = it },
                        singleLine = true,
                        placeholder = { Text("ex: JIRA-TASK-0001") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.createRoom(roomName.takeIf { it.isNotBlank() })
                        showDialog = false
                    }) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                })
        }
    }
}
