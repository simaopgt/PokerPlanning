package com.idk.feature_poker_planning.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.idk.feature.poker.planning.R
import com.idk.feature_poker_planning.navigation.PokerPlanningDestinations

@Composable
fun SplashScreen(
    navController: NavHostController, viewModel: SplashViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }

    val uiState by viewModel.uiState.collectAsState()
    uiState.hasProfile.let { exists ->
        LaunchedEffect(exists) {
            val destination = if (exists) PokerPlanningDestinations.HOME
            else PokerPlanningDestinations.WELCOME

            navController.navigate(destination) {
                popUpTo(PokerPlanningDestinations.SPLASH) {
                    inclusive = true
                }
            }
        }
    }
}
