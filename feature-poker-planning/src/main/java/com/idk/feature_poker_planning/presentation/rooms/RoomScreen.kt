package com.idk.feature_poker_planning.presentation.rooms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.idk.feature_poker_planning.domain.model.Participant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoomViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.roomName, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(text = "Team", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.participants.take(6)) { participant ->
                    ParticipantAvatar(participant)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.votesRevealed) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.participants.take(6)) { participant ->
                        VoteBubble(vote = participant.vote)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            InfoCard(votesRevealed = uiState.votesRevealed)
            Spacer(modifier = Modifier.height(16.dp))

            VoteInputField(
                voteInput = uiState.currentVoteInput,
                onValueChange = viewModel::onVoteInputChange
            )
            Spacer(modifier = Modifier.height(16.dp))

            ActionButtons(
                votesRevealed = uiState.votesRevealed,
                onVoteClick = viewModel::submitVote,
                onRevealClick = viewModel::revealVotes,
                onNewSessionClick = viewModel::startNewSession
            )
        }
    }
}

@Composable
private fun ParticipantAvatar(participant: Participant) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // TODO: replace placeholder with actual image loader
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = participant.name, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun VoteBubble(vote: Int?) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(text = vote?.toString() ?: "-", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun InfoCard(votesRevealed: Boolean) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!votesRevealed) {
                Text(text = "Vote na complexidade da tarefa", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pense no nível de esforço ou incerteza envolvido e insira um número que represente sua avaliação. Depois, todos os votos serão discutidos em equipe.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(text = "Resultado:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "A média foi xx, quem deu a menor nota foi Fulano, quem deu a maior nota foi Cicrano.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun VoteInputField(voteInput: String, onValueChange: (String) -> Unit) {
    Column {
        Text(text = "Input your vote here", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = voteInput,
            onValueChange = onValueChange,
            singleLine = true,
            placeholder = { Text("ex: 5") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ActionButtons(
    votesRevealed: Boolean,
    onVoteClick: () -> Unit,
    onRevealClick: () -> Unit,
    onNewSessionClick: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Button(onClick = onVoteClick, modifier = Modifier.weight(1f)) {
            Text("Vote")
        }
        Button(
            onClick = if (votesRevealed) onNewSessionClick else onRevealClick,
            modifier = Modifier.weight(1f)
        ) {
            Text(if (!votesRevealed) "Reveal Votes" else "New Session")
        }
    }
}
