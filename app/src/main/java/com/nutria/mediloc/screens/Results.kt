package com.nutria.mediloc.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.nutria.mediloc.navigation.AppScreens


val actionColor = Color(0xFF00b572)
val secondaryColor = Color(0xFFA8EAA7)
val textInButton = Color.Black
val textoBanner = 24.sp
val texto = 18.sp

@Composable
fun Results (navController: NavController, contenidoReceta:String?){
    MaterialTheme {
        val json = """
        {
          "Medicamentos": [
            {
              "Nombre": "paracetamol",
              "Presentaciones": [
                {
                  "Precio": 546.12,
                  "Farmacia": "Guadalajara",
                  "Cantidad": "10 pastillas"
                },
                {
                  "Precio": 123.15,
                  "Farmacia": "Guadalajara",
                  "Cantidad": "20 pastillas"
                },
                {
                  "Precio": 546.12,
                  "Farmacia": "Walmart",
                  "Cantidad": "10 pastillas"
                }
              ]
            },
            {
              "Nombre": "ibuprofeno",
              "Presentaciones": [
                {
                  "Precio": 1546,
                  "Farmacia": "Guadalajara",
                  "Cantidad": "20 pastillas"
                },
                {
                  "Precio": 1231,
                  "Farmacia": "Guadalajara",
                  "Cantidad": "20 pastillas"
                },
                {
                  "Precio": 456,
                  "Farmacia": "Walmart",
                  "Cantidad": "10 pastillas"
                }
              ]
            }
          ]
        }
    """.trimIndent()
        if (contenidoReceta == "No se pudieron encontrar medicamentos"){
            NoMed(navController = navController)
        } else {
            val medicamentos: List<Medicamento> = jsonAMedicamentos(json)
            val content = contenidoReceta ?: "Contenido predeterminado"
            Text(text = content)
            //Provisional en lo que queda el web scrapping
        // ResultsList(medicamentos = medicamentos, navController)
        }
    }
}

data class Presentacion(
    @SerializedName("Precio") val precio: Double,
    @SerializedName("Farmacia") val farmacia: String,
    @SerializedName("Cantidad") val cantidad: String
)

data class Medicamento(
    @SerializedName("Nombre") val nombre: String,
    @SerializedName("Presentaciones") val presentaciones: List<Presentacion>
)

data class Datos(
    @SerializedName("Medicamentos") val medicamentos: List<Medicamento>
)

fun jsonAMedicamentos(json: String): List<Medicamento> {
    val gson = Gson()
    val datos = gson.fromJson(json, Datos::class.java)
    return datos.medicamentos
}

@Composable
fun NoMed(navController: NavController){
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }

    LaunchedEffect(key1 = context) {
        val sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        nombre = sharedPreferences.getString("nombre", "") ?: ""

    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Altura del banner
            .background(color = secondaryColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No se pudieron encontrar medicamentos",
            style = TextStyle(
                fontSize = textoBanner,
                fontWeight = FontWeight.Bold
            ),
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Porfavor intente tomando la foto mas cerca.\n - Procure que la foto se vea clara",
            modifier = Modifier.padding(top = 100.dp),
            style = TextStyle(
                fontSize = texto,
                fontWeight = FontWeight.Bold)
        )
        Button(
            onClick = {
                navController.navigate(AppScreens.Serach.route + "/" + nombre)
            },
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = actionColor
            ),
            shape = RoundedCornerShape(16.dp),

            ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .padding(1.dp),
                tint = Color.Black
            )
            Text(
                text = "Volver al inicio",
                style = TextStyle(
                    fontSize = textoBanner,
                    fontWeight = FontWeight.Bold
                ),
                color = textInButton
            )
        }
    }
}

@Composable
fun ResultsList(medicamentos: List<Medicamento>, navController: NavController) {
    var indice by remember { mutableStateOf(0) }

    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }

    LaunchedEffect(key1 = context) {
        val sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        nombre = sharedPreferences.getString("nombre", "") ?: ""

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(color = secondaryColor),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = " Medicamentos Encontrados",
                style = TextStyle(
                    fontSize = textoBanner,
                    fontWeight = FontWeight.Bold
                ),
            )
        }

        PantallaMedicamento(
            medicamento = medicamentos[indice],
            indi = indice,
            fullList = medicamentos,
            onAnterior = { indice = (indice - 1).coerceAtLeast(0) },
            onSiguiente = { indice = (indice + 1).coerceAtMost(medicamentos.size - 1) },
            navController,
            nombre
        )
    }
}

@Composable
fun PantallaMedicamento(
    medicamento: Medicamento,
    indi: Int,
    fullList: List<Medicamento>,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit,
    navController: NavController,
    nombre: String
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Medicamento: ${medicamento.nombre}",
            fontWeight = FontWeight.Bold,
            fontSize = textoBanner,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        medicamento.presentaciones.forEachIndexed { index, presentacion ->
            Column {
                Text(text = "Precio: ${presentacion.precio}", fontWeight = FontWeight.Bold, fontSize = texto)
                Text(text = "Cantidad: ${presentacion.cantidad}", fontSize = texto)
                Text(text = "Farmacia: ${presentacion.farmacia}", fontSize = texto)
                Spacer(modifier = Modifier.height(15.dp))
                Divider(color = secondaryColor, thickness = 2.dp)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onAnterior,
                enabled = indi > 0,
                colors = ButtonDefaults.buttonColors(containerColor = actionColor),
                modifier = Modifier.size(width = 150.dp, height = 80.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(text = "Anterior medicamento",
                    color = textInButton)
            }
            Button(
                onClick = onSiguiente,
                enabled = indi < fullList.size - 1,
                colors = ButtonDefaults.buttonColors(containerColor = actionColor),
                modifier = Modifier.size(width = 150.dp, height = 80.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(text = "Siguiente medicamento",
                    color = textInButton)
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
        Button(
            onClick = {
                navController.navigate(AppScreens.Serach.route + "/" + nombre)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = actionColor
            ),
            shape = RoundedCornerShape(16.dp),

            ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .padding(1.dp),
                tint = Color.Black
            )
            Text(
                text = "Buscar otros medicamentos",
                style = TextStyle(
                    fontSize = textoBanner,
                    fontWeight = FontWeight.Bold
                ),
                color = textInButton
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreviewMain (){
    val navController = rememberNavController()
    val conten = "No se pudieron encontrar medicamentos"
    Results(navController, conten)
}