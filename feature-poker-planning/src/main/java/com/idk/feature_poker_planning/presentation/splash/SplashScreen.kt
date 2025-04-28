package com.idk.feature_poker_planning.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.idk.feature.poker.planning.R
import com.idk.feature_poker_planning.navigation.PokerPlanningDestinations
import com.idk.feature_poker_planning.utils.FirestoreConstants.SPLASH_SCREEN_DELAY
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController, viewModel: SplashViewModel = hiltViewModel()
) {
    Image(
        painter = painterResource(id = R.drawable.splash),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.hasProfile) {
        delay(SPLASH_SCREEN_DELAY)
        val destination = if (uiState.hasProfile) {
            PokerPlanningDestinations.HOME
        } else {
            PokerPlanningDestinations.WELCOME
        }
        navController.navigate(destination) {
            popUpTo(PokerPlanningDestinations.SPLASH) { inclusive = true }
        }
    }
}
