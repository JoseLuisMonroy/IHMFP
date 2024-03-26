package com.nutria.mediloc.navigation

sealed class AppScreens (val route : String) {
    object LogIn: AppScreens ("login")
    object Serach: AppScreens ("searcher")
    object Camera: AppScreens ("camera")
    object Results: AppScreens ("results")
}