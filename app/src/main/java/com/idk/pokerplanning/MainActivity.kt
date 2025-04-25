package com.idk.pokerplanning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.idk.feature_poker_planning.presentation.home.HomeScreen
import com.idk.feature_poker_planning.presentation.rooms.RoomScreen
import com.idk.pokerplanning.ui.theme.PokerPlanningTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PokerPlanningTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController, startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            onRoomClick = { navController.navigate("room/$it") })
                    }
                    composable(
                        route = "room/{roomId}",
                        arguments = listOf(navArgument("roomId") { type = NavType.StringType })
                    ) { backStack ->
                        val roomId = backStack.arguments!!.getString("roomId")!!
//                        RoomScreen(roomId = roomId)
                    }
                }
            }
        }
    }
}

