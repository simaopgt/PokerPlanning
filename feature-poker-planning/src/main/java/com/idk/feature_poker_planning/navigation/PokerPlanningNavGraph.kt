package com.idk.feature_poker_planning.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.idk.feature_poker_planning.presentation.home.HomeScreen
import com.idk.feature_poker_planning.presentation.rooms.RoomScreen
import com.idk.feature_poker_planning.presentation.rooms.RoomViewModel
import com.idk.feature_poker_planning.presentation.splash.SplashScreen
import com.idk.feature_poker_planning.presentation.welcome.WelcomeScreen

object PokerPlanningDestinations {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val ROOM = "room/{roomId}"
    const val ARG_ROOM = "roomId"

    fun createHomeRoute() = HOME
    fun createRoomRoute(roomId: String) = "room/$roomId"
}

fun NavGraphBuilder.pokerPlanningNavGraph(
    navController: NavHostController
) {
    composable(PokerPlanningDestinations.SPLASH) {
        SplashScreen(navController = navController)
    }

    composable(PokerPlanningDestinations.WELCOME) {
        WelcomeScreen(
            onNavigateNext = {
                navController.navigate(PokerPlanningDestinations.createHomeRoute()) {
                    popUpTo(PokerPlanningDestinations.WELCOME) { inclusive = true }
                }
            }
        )
    }

    composable(PokerPlanningDestinations.HOME) {
        HomeScreen(
            onRoomClick = { roomId ->
                navController.navigate(PokerPlanningDestinations.createRoomRoute(roomId))
            }
        )
    }

    composable(
        route = PokerPlanningDestinations.ROOM,
        arguments = listOf(
            navArgument(PokerPlanningDestinations.ARG_ROOM) {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val viewModel: RoomViewModel = hiltViewModel(backStackEntry)
        RoomScreen(
            viewModel = viewModel,
            onBack = { navController.popBackStack() }
        )
    }
}
