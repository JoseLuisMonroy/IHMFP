package com.nutria.mediloc.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nutria.mediloc.screens.Camera
import com.nutria.mediloc.screens.LandingPage
import com.nutria.mediloc.screens.Login
import com.nutria.mediloc.screens.Results

@Composable
fun AppNavigation (){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.LogIn.route){
        composable(route = AppScreens.LogIn.route){
            Login(navController)
        }
        composable(route = AppScreens.Serach.route + "/{nombre}",
            arguments = listOf(navArgument(name = "nombre") {
                type = NavType.StringType
            })){
            LandingPage(navController, it.arguments?.getString("nombre"))
        }
        composable(route = AppScreens.Camera.route){
            Camera(navController)
        }
        composable(route = AppScreens.Results.route +"/{content}",
            arguments = listOf(navArgument(name = "content") {
                type = NavType.StringType
            })){
            Results(navController, it.arguments?.getString("content"))
        }
    }
}