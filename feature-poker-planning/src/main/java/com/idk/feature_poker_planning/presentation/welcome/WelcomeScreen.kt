package com.idk.feature_poker_planning.presentation.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.idk.feature.poker.planning.R
import com.idk.feature_poker_planning.utils.FirestoreConstants.AVATAR_ID_01
import com.idk.feature_poker_planning.utils.FirestoreConstants.AVATAR_ID_02
import com.idk.feature_poker_planning.utils.FirestoreConstants.AVATAR_ID_03
import com.idk.feature_poker_planning.utils.FirestoreConstants.AVATAR_ID_04
import com.idk.feature_poker_planning.utils.FirestoreConstants.AVATAR_ID_05
import com.idk.feature_poker_planning.utils.FirestoreConstants.AVATAR_ID_06

@Composable
fun WelcomeRoute(
    viewModel: WelcomeViewModel = hiltViewModel(), onNavigateNext: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    WelcomeScreen(
        uiState = uiState,
        avatars = viewModel.avatars,
        onAvatarSelected = viewModel::selectAvatar,
        onNameChange = viewModel::onNameChange,
        onStartClicked = { viewModel.onStartClicked(onNavigateNext) })
}

@Composable
fun WelcomeScreen(
    uiState: WelcomeUiState,
    avatars: List<String>,
    onAvatarSelected: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onStartClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.welcome_choose_avatar),
                style = MaterialTheme.typography.titleMedium
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(avatars, key = { it }) { avatarKey ->
                    val avatarRes: Int = remember(avatarKey) {
                        when (avatarKey) {
                            AVATAR_ID_01 -> R.drawable.avatar_1
                            AVATAR_ID_02 -> R.drawable.avatar_2
                            AVATAR_ID_03 -> R.drawable.avatar_3
                            AVATAR_ID_04 -> R.drawable.avatar_4
                            AVATAR_ID_05 -> R.drawable.avatar_5
                            AVATAR_ID_06 -> R.drawable.avatar_6
                            else -> R.drawable.unknow
                        }
                    }
                    val isSelected = avatarKey == uiState.selectedAvatar

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onAvatarSelected(avatarKey) }) {
                        Image(
                            painter = painterResource(avatarRes),
                            contentDescription = stringResource(R.string.avatar_description),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))

        Text(
            text = stringResource(R.string.welcome_enter_name),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

        OutlinedTextField(
            value = uiState.userName,
            onValueChange = onNameChange,
            placeholder = { Text(stringResource(R.string.hint_enter_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onStartClicked,
            enabled = uiState.isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.button_height))
        ) {
            Text(text = stringResource(R.string.button_start_game))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen(
            uiState = WelcomeUiState(
            selectedAvatar = "avatar_3", userName = "Bob", isFormValid = true
        ),
            avatars = listOf("avatar_1", "avatar_2", "avatar_3"),
            onAvatarSelected = {},
            onNameChange = {},
            onStartClicked = {})
    }
}
