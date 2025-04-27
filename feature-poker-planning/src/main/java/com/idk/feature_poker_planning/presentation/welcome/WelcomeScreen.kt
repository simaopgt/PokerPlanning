package com.idk.feature_poker_planning.presentation.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.idk.feature.poker.planning.R

@Composable
fun WelcomeScreen(
    onNavigateNext: () -> Unit, viewModel: WelcomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = "Escolha seu avatar", style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()
        ) {
            items(viewModel.avatars) { avatarKey ->
                val avatarRes = when (avatarKey) {
                    "avatar_1" -> R.drawable.avatar_1
                    "avatar_2" -> R.drawable.avatar_2
                    "avatar_3" -> R.drawable.avatar_3
                    "avatar_4" -> R.drawable.avatar_4
                    "avatar_5" -> R.drawable.avatar_5
                    "avatar_6" -> R.drawable.avatar_6
                    else -> R.drawable.unknow
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (uiState.selectedAvatar == avatarKey) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface
                        )
                        .clickable { viewModel.selectAvatar(avatarKey) }) {
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = avatarKey,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Insira o seu nome", style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.userName,
            onValueChange = viewModel::onNameChange,
            placeholder = { Text("Insira aqui o seu nome") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(0.5f))

        Button(
            onClick = { viewModel.onStartClicked(onNavigateNext) },
            enabled = uiState.isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Comece a Jogar")
        }
    }
}
