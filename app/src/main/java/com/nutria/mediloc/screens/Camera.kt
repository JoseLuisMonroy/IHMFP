package com.nutria.mediloc.screens

import android.Manifest
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.nutria.mediloc.navigation.AppScreens
import java.io.File
import java.util.concurrent.Executor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Camera(navController: NavController) {
    var secondaryColor = remember { Color(0xFFA8EAA7) }
    var actionColor = remember { Color(0xFF00b572)}

    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    val context = LocalContext.current
    val cameraController = remember{
        LifecycleCameraController(context)
    }
    val lifecycle = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
    Scaffold (modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton( onClick = {
                val executor = ContextCompat.getMainExecutor(context)
                takePicture(cameraController, executor, navController)
            }, Modifier.size(width = 200.dp, height = 100.dp),
                containerColor = actionColor,
                contentColor = Color.White
                ){
                Text(
                    text = "Tomar Foto",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    , onTextLayout = {}
                )
    }
    }
    ) {
        if (permissionState.status.isGranted){
            CameraCompose(cameraController, lifecycle, modifier = Modifier.padding(it))
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // Altura del banner
                    .background(color = secondaryColor) // Color a8eaa7
            ) {
                Text(
                    text = "Favor de permitir el acceso a la cámara",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.Center), onTextLayout = {}
                )
            }
        }
    }

}

@Composable
fun CameraCompose(
    cameraController: LifecycleCameraController,
    lifecycle: LifecycleOwner,
    modifier: Modifier = Modifier){

    cameraController.bindToLifecycle(lifecycle)
    AndroidView(modifier = modifier, factory = { context ->
        val previewView = PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        previewView.controller= cameraController

        previewView
    })
}

private fun takePicture (
    cameraController: LifecycleCameraController,
    executor: Executor,
    navController: NavController
){
    val file = File.createTempFile("imageReceta", ".jpg")
    val outputDirectory = ImageCapture.OutputFileOptions.Builder(file).build()
    cameraController.takePicture(outputDirectory, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            //Para retornar en un futuro
            println(outputFileResults.savedUri)
            navController.navigate(AppScreens.Results.route)

        }

        override fun onError(exception: ImageCaptureException) {
            println("No se pudo guardar la foto")
        }
    },
    )
}