package com.idk.feature_poker_planning.presentation.rooms

import androidx.compose.foundation.Image
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.idk.feature.poker.planning.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUserId = uiState.currentUserId
    val currentUser = uiState.participants.firstOrNull { it.userId == currentUserId }
    val others = uiState.participants.filter { it.userId != currentUserId }
    val displayList = listOfNotNull(currentUser) + others

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.roomName, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Team", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(displayList.take(6), key = { it.userId }) { participant ->
                    ParticipantAvatar(
                        avatar = participant.avatar,
                        name = participant.name,
                        vote = participant.vote,
                        isCurrentUser = participant.userId == currentUserId,
                        votesRevealed = uiState.votesRevealed
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            if (uiState.votesRevealed) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(displayList.take(6), key = { it.userId }) { participant ->
                        VoteBubble(vote = participant.vote)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            InfoCard(votesRevealed = uiState.votesRevealed)
            Spacer(Modifier.height(16.dp))

            VoteInputField(
                voteInput = uiState.currentVoteInput,
                onValueChange = viewModel::onVoteInputChange
            )
            Spacer(Modifier.height(16.dp))

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
private fun ParticipantAvatar(
    avatar: String,
    name: String,
    vote: Int?,
    isCurrentUser: Boolean,
    votesRevealed: Boolean
) {
    val context = LocalContext.current
    val resId = remember(avatar) {
        context.resources.getIdentifier(avatar, "drawable", context.packageName)
    }.takeIf { it != 0 } ?: R.drawable.unknow
    val painter = painterResource(id = resId)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painter,
            contentDescription = name,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    if (isCurrentUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.primaryContainer
                )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (isCurrentUser) "$name (Você)" else name,
            style = MaterialTheme.typography.bodySmall,
            color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onPrimaryContainer
        )
        if (isCurrentUser && votesRevealed) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = vote?.toString() ?: "-",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
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
        Text(text = "Insira aqui o seu voto:", style = MaterialTheme.typography.bodyMedium)
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
        Button(
            onClick = onVoteClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Votar")
        }
        Button(
            onClick = if (votesRevealed) onNewSessionClick else onRevealClick,
            modifier = Modifier.weight(1f)
        ) {
            Text(if (!votesRevealed) "Revelar Votos" else "Nova Sessão")
        }
    }
}