package com.example.rankit.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

@Composable
fun RankItNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            // TODO Task 7: replace with HomeScreen(navController)
            Text("Home — stub")
        }

        composable<CreateList> {
            // TODO Task 8: replace with CreateListScreen(navController)
            Text("Create List — stub")
        }

        composable<ListDetail> { backStackEntry ->
            val route: ListDetail = backStackEntry.toRoute()
            // TODO Task 10: replace with ListDetailScreen(route.listId, navController)
            Text("List Detail — listId=${route.listId}")
        }

        composable<AddItem> { backStackEntry ->
            val route: AddItem = backStackEntry.toRoute()
            // TODO Task 9: replace with AddItemScreen(route.listId, navController)
            Text("Add Item — listId=${route.listId}")
        }

        composable<ItemDetail> { backStackEntry ->
            val route: ItemDetail = backStackEntry.toRoute()
            // TODO Task 11: replace with ItemDetailScreen(route.listId, route.itemId, navController)
            Text("Item Detail — listId=${route.listId}, itemId=${route.itemId}")
        }
    }
}
