package com.idk.feature_poker_planning.navigation

import android.net.Uri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.idk.feature_poker_planning.presentation.home.HomeScreen
import com.idk.feature_poker_planning.presentation.rooms.RoomScreen
import com.idk.feature_poker_planning.presentation.splash.SplashScreen
import com.idk.feature_poker_planning.presentation.welcome.WelcomeScreen

object PokerPlanningDestinations {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val ROOM = "room/{roomId}/{roomName}"
    const val ARG_ROOM_ID = "roomId"
    const val ARG_ROOM_NAME = "roomName"

    fun createHomeRoute() = HOME
    fun createRoomRoute(id: String, name: String): String =
        "room/$id/${Uri.encode(name)}"
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
            })
    }

    composable(PokerPlanningDestinations.HOME) {
        HomeScreen(
            onRoomClick = { roomId, roomName ->
                navController.navigate(
                    PokerPlanningDestinations.createRoomRoute(roomId, roomName)
                )
            }
        )
    }

    composable(
        route = PokerPlanningDestinations.ROOM,
        arguments = listOf(navArgument(PokerPlanningDestinations.ARG_ROOM_ID) {
            type = NavType.StringType
        }, navArgument(PokerPlanningDestinations.ARG_ROOM_NAME) {
            type = NavType.StringType
        })) { backStackEntry ->
        RoomScreen(
            onBack = { navController.popBackStack() }, viewModel = hiltViewModel(backStackEntry)
        )
    }
}
