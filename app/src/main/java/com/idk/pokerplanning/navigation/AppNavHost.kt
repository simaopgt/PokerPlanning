package com.idk.pokerplanning.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.idk.feature_poker_planning.navigation.PokerPlanningDestinations
import com.idk.feature_poker_planning.navigation.pokerPlanningNavGraph

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController, startDestination = PokerPlanningDestinations.SPLASH
    ) {
        pokerPlanningNavGraph(navController)
    }
}
