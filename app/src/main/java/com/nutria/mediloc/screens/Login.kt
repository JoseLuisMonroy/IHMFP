package com.nutria.mediloc.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nutria.mediloc.Persona
import com.nutria.mediloc.navigation.AppScreens
import android.content.Context
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Login(navController: NavController){
    Scaffold {
        BodyContent(navController)
    }
}

@Composable
fun BodyContent (navController: NavController){
    val actionColor = remember { Color(0xFF00b572) }
    val secondaryColor = remember { Color(0xFFA8EAA7) }

    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var tieneWhatsapp by remember { mutableStateOf(false) }
    var numero by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }
    var showError2 by remember { mutableStateOf(false) }
    var showError3 by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Leer datos guardados
    LaunchedEffect(key1 = context) {
        val sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        nombre = sharedPreferences.getString("nombre", "") ?: ""
        tieneWhatsapp = sharedPreferences.getBoolean("tieneWhatsapp", false)
        numero = sharedPreferences.getString("numero", "") ?: ""


        if (!nombre.isNullOrBlank()) {
            navController.navigate(AppScreens.Serach.route + "/" + nombre)
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White) // Fondo blanco
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // Altura del banner
                .background(color = secondaryColor) // Color a8eaa7
        ) {
            Text(
                text = "¡Bienvenido!",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.align(Alignment.Center), onTextLayout = {}
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Por favor, ingrese su nombre:", onTextLayout = {}) },
            colors = TextFieldDefaults.colors(
                focusedTrailingIconColor = actionColor,
                unfocusedTrailingIconColor = secondaryColor,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedLabelColor = Color.Black,
                focusedLeadingIconColor = actionColor,
                focusedIndicatorColor = actionColor,
                unfocusedIndicatorColor = secondaryColor
            ),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null
                )
            }
        )
        if (showError) {
            Snackbar(
                containerColor = Color(0xFFFFCCCC), // Fondo rojo claro
            ) {
                Text(
                    errorMessage,
                    style = TextStyle(
                        color = Color(0xFFCC0000)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = numero,
            onValueChange = { numero = it },
            label = { Text("Introduzca su número:", onTextLayout = {}) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            colors = TextFieldDefaults.colors(
                focusedTrailingIconColor = actionColor,
                unfocusedTrailingIconColor = secondaryColor,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedLabelColor = Color.Black,
                focusedLeadingIconColor = actionColor,
                focusedIndicatorColor = actionColor,
                unfocusedIndicatorColor = secondaryColor
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = null
                )
            }
        )
        if (showError2) {
            Snackbar(
                containerColor = Color(0xFFFFCCCC), // Fondo rojo claro
            ) {
                Text(
                    errorMessage,
                    style = TextStyle(
                        color = Color(0xFFCC0000)
                    )
                )
            }
        }
        if (showError3) {
            Snackbar(
                containerColor = Color(0xFFFFCCCC), // Fondo rojo claro
            ) {
                Text(
                    errorMessage,
                    style = TextStyle(
                        color = Color(0xFFCC0000)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "¿Tiene WhatsApp?",
                modifier = Modifier.padding(start = 10.dp) // Padding izquierdo de 16dp
                , onTextLayout = {}
            )

            Checkbox(
                checked = tieneWhatsapp,
                onCheckedChange = {
                    tieneWhatsapp = it
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = actionColor
                )
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (nombre.isNotBlank() && numero.length == 10) {
                    val newUser = Persona(nombre, numero, tieneWhatsapp)
                    val sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString("nombre", nombre)
                        putBoolean("tieneWhatsapp", tieneWhatsapp)
                        putString("numero", numero)
                        apply()
                    }
                    navController.navigate(AppScreens.Serach.route + "/" + nombre)
                } else {
                    if(nombre.isBlank()) {
                        showError = true
                        errorMessage = "Favor de completar este campo."
                    }   else if (numero.isBlank()) {
                        showError = false
                        showError2  = true
                        errorMessage = "Favor de completar este campo."
                    } else {
                        showError = false
                        showError2  = false
                        showError3 = true
                        errorMessage = "El número debe tener una longitud de 10 números."
                    }
                }
            },

            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = actionColor
            )

        ) {
            Text("Ingresar", onTextLayout = {})
        }
    }

}