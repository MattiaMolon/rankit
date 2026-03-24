package com.example.rankit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.rankit.ui.navigation.RankItNavGraph
import com.example.rankit.ui.theme.RankItTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RankItTheme {
                val navController = rememberNavController()
                RankItNavGraph(navController = navController)
            }
        }
    }
}
