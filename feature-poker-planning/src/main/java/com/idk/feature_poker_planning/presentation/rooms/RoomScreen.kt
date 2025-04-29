package com.idk.feature_poker_planning.presentation.rooms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.idk.feature.poker.planning.R
import com.idk.feature_poker_planning.domain.model.Participant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomRoute(
    onBack: () -> Unit, modifier: Modifier = Modifier, viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    RoomScreen(
        uiState = uiState,
        onBack = onBack,
        onVoteInputChange = viewModel::onVoteInputChange,
        onVoteClick = viewModel::submitVote,
        onRevealClick = viewModel::revealVotes,
        onNewSessionClick = viewModel::startNewSession,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomScreen(
    uiState: RoomUiState,
    onBack: () -> Unit,
    onVoteInputChange: (String) -> Unit,
    onVoteClick: () -> Unit,
    onRevealClick: () -> Unit,
    onNewSessionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUserId by remember { derivedStateOf { uiState.currentUserId } }
    val participantsDisplay by remember(uiState.participants, currentUserId) {
        derivedStateOf {
            val me = uiState.participants.firstOrNull { it.userId == currentUserId }
            val others = uiState.participants.filter { it.userId != currentUserId }
            listOfNotNull(me) + others
        }
    }
    val hasVoted by remember(uiState.participants, currentUserId) {
        derivedStateOf {
            uiState.participants.any { it.userId == currentUserId && it.vote != null }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                Text(
                    text = uiState.roomName, style = MaterialTheme.typography.titleLarge
                )
            }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_content_description)
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.padding_standard))
        ) {
            Text(
                text = stringResource(R.string.team_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
            ) {
                items(participantsDisplay, key = { it.userId }) { participant ->
                    ParticipantAvatar(
                        participant, isCurrentUser = participant.userId == currentUserId
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

            if (uiState.votesRevealed) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
                ) {
                    items(participantsDisplay, key = { it.userId }) { participant ->
                        VoteBubble(vote = participant.vote)
                    }
                }
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
            }

            InfoCard(
                votesRevealed = uiState.votesRevealed,
                aiSummary = uiState.aiSummary,
                isLoadingAi = uiState.isLoadingAi
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

            VoteInputField(
                voteInput = uiState.currentVoteInput,
                onValueChange = onVoteInputChange,
                enabled = !hasVoted && !uiState.votesRevealed
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

            ActionButtons(
                votesRevealed = uiState.votesRevealed,
                hasVoted = hasVoted,
                onVoteClick = onVoteClick,
                onRevealClick = onRevealClick,
                onNewSessionClick = onNewSessionClick
            )
        }
    }
}

@Composable
private fun ParticipantAvatar(
    participant: Participant, isCurrentUser: Boolean
) {
    val context = LocalContext.current
    val resId = remember(participant.avatar) {
        context.resources.getIdentifier(participant.avatar, "drawable", context.packageName)
    }.takeIf { it != 0 } ?: R.drawable.unknow

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(IntrinsicSize.Min)
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = stringResource(
                R.string.avatar_content_description, participant.name
            ),
            modifier = Modifier
                .size(dimensionResource(R.dimen.avatar_size))
                .clip(CircleShape)
                .background(
                    if (isCurrentUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.primaryContainer, shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xsmall)))
        Text(
            text = buildString {
                append(participant.name)
                if (isCurrentUser) append(stringResource(R.string.participant_you_suffix))
            },
            style = MaterialTheme.typography.bodySmall,
            color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xsmall)))
        participant.vote?.let {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = stringResource(R.string.has_voted_description),
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_small))
            )
        }
    }
}

@Composable
private fun VoteBubble(vote: Int?) {
    Box(
        modifier = Modifier
            .size(dimensionResource(R.dimen.bubble_size))
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = vote?.toString() ?: "-",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun InfoCard(
    votesRevealed: Boolean, aiSummary: String, isLoadingAi: Boolean
) {
    Card(
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_small)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_standard))) {
            if (!votesRevealed) {
                Text(
                    text = stringResource(R.string.vote_prompt),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xsmall)))
                Text(
                    text = stringResource(R.string.vote_instructions),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            } else {
                Text(
                    text = stringResource(R.string.summary_ai_label),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
                when {
                    isLoadingAi -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(dimensionResource(R.dimen.loading_indicator_size))
                        )
                    }

                    aiSummary.isNotBlank() -> {
                        Text(
                            text = aiSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    else -> {
                        Text(
                            text = stringResource(R.string.awaiting_suggestion),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VoteInputField(
    voteInput: String, onValueChange: (String) -> Unit, enabled: Boolean
) {
    Column {
        Text(
            text = stringResource(R.string.vote_input_label),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xsmall)))
        OutlinedTextField(
            value = voteInput,
            onValueChange = onValueChange,
            singleLine = true,
            enabled = enabled,
            placeholder = { Text(stringResource(R.string.vote_input_placeholder)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ActionButtons(
    votesRevealed: Boolean,
    hasVoted: Boolean,
    onVoteClick: () -> Unit,
    onRevealClick: () -> Unit,
    onNewSessionClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onVoteClick,
            enabled = !hasVoted && !votesRevealed,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(stringResource(R.string.button_vote))
        }
        Button(
            onClick = if (votesRevealed) onNewSessionClick else onRevealClick,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (!votesRevealed) stringResource(R.string.button_reveal_votes)
                else stringResource(R.string.button_new_session)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoomScreenPreview() {
    val sampleState = RoomUiState(
        roomName = "Sprint 42",
        participants = listOf(
            Participant(userId = "u1", name = "Ana", avatar = "avatar_1", vote = null),
            Participant(userId = "u2", name = "Bruno", avatar = "avatar_2", vote = 5)
        ),
        votesRevealed = false,
        currentVoteInput = "",
        currentUserId = "u1",
        aiSummary = "",
        isLoadingAi = false
    )
    RoomScreen(
        uiState = sampleState,
        onBack = {},
        onVoteInputChange = {},
        onVoteClick = {},
        onRevealClick = {},
        onNewSessionClick = {})
}
