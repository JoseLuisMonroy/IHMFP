package com.nutria.mediloc.screens

import com.nutria.mediloc.R
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.time.LocalTime
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.nutria.mediloc.navigation.AppScreens

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LandingPage(navController: NavController, nombre:String?){

    Scaffold (){
        BodyContentLanding(navController, nombre)
    }
}

@Composable
fun BodyContentLanding (navController: NavController, name: String?) {
    val actionColor = Color(0xFF00b572)
    val secondaryColor = Color(0xFFA8EAA7)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Altura del banner
            .background(color = secondaryColor) // Color a8eaa7
    ) {
        val hora = obtenerHoraDelDia()

        Text(
            text = "¡Buen $hora $name!",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                navController.navigate(AppScreens.Camera.route)
            },
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 150.dp)
                .size(width = 200.dp, height = 200.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = actionColor
            ),
            shape = RoundedCornerShape(16.dp)

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                    contentDescription = "Camera Button",
                    modifier = Modifier.size(90.dp)
                )
                Text(
                    text = "Tomar foto a la receta o medicamento",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally), onTextLayout = {}
                )
            }
        }

    }
}

fun obtenerHoraDelDia(): String {
    val horaActual = LocalTime.now()

    return when {
        horaActual.isBefore(LocalTime.NOON) -> "día"
        horaActual.isBefore(LocalTime.of(19, 0)) -> "tarde"
        else -> "noche"
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewLanding () {
    LandingPage(navController = rememberNavController(), nombre = "")
}