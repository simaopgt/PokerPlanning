package com.idk.feature_poker_planning.navigation

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.idk.feature_poker_planning.presentation.home.HomeRoute
import com.idk.feature_poker_planning.presentation.rooms.RoomRoute
import com.idk.feature_poker_planning.presentation.splash.SplashScreen
import com.idk.feature_poker_planning.presentation.welcome.WelcomeRoute

object PokerPlanningDestinations {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val ROOM = "room/{roomId}/{roomName}"
    const val ARG_ROOM_ID = "roomId"
    const val ARG_ROOM_NAME = "roomName"

    fun createHomeRoute() = HOME
    fun createRoomRoute(id: String, name: String): String = "room/$id/${Uri.encode(name)}"
}

fun NavGraphBuilder.pokerPlanningNavGraph(
    navController: NavHostController
) {
    composable(PokerPlanningDestinations.SPLASH) {
        SplashScreen(navController)
    }

    composable(PokerPlanningDestinations.WELCOME) {
        WelcomeRoute(
            onNavigateNext = {
                navController.navigate(PokerPlanningDestinations.createHomeRoute()) {
                    popUpTo(PokerPlanningDestinations.WELCOME) { inclusive = true }
                }
            })
    }

    composable(PokerPlanningDestinations.HOME) {
        HomeRoute(
            onRoomClick = { roomId, roomName ->
                navController.navigate(
                    PokerPlanningDestinations.createRoomRoute(roomId, roomName)
                )
            })
    }

    composable(
        route = PokerPlanningDestinations.ROOM,
        arguments = listOf(navArgument(PokerPlanningDestinations.ARG_ROOM_ID) {
            type = NavType.StringType
        }, navArgument(PokerPlanningDestinations.ARG_ROOM_NAME) {
            type = NavType.StringType
        })
    ) { backStackEntry ->
        RoomRoute(
            onBack = { navController.popBackStack() },
            modifier = Modifier.fillMaxSize(),
            viewModel = hiltViewModel(backStackEntry)
        )
    }
}
