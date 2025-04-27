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
import com.idk.pokerplanning.navigation.AppNavHost
import com.idk.pokerplanning.ui.theme.PokerPlanningTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PokerPlanningTheme {
                AppNavHost()
            }
        }
    }
}
