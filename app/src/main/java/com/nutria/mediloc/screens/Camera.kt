package com.nutria.mediloc.screens

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProcessor
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.privacysandbox.tools.core.model.Method
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.json.JSONObject
import java.io.File
import java.util.UUID
import java.util.concurrent.Executor
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import java.util.*
import com.android.volley.Request
import com.nutria.mediloc.navigation.AppScreens

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Camera(navController: NavController) {
    val secondaryColor = Color(0xFFA8EAA7)
    val actionColor = Color(0xFF00b572)

    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    val context = LocalContext.current
    val cameraController = remember {
        LifecycleCameraController(context)
    }
    val lifecycle = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val executor = ContextCompat.getMainExecutor(context)
                    takePicture(cameraController, executor, navController, context)
                },
                Modifier.size(width = 200.dp, height = 100.dp),
                containerColor = actionColor,
                contentColor = Color.White
            ) {
                Text(
                    text = "Tomar Foto",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    ) {
        if (permissionState.status.isGranted) {
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
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}


@Composable
fun CameraCompose(
    cameraController: LifecycleCameraController,
    lifecycle: LifecycleOwner,
    modifier: Modifier = Modifier
) {
    cameraController.bindToLifecycle(lifecycle)
    AndroidView(modifier = modifier, factory = { context ->
        val previewView = PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        previewView.controller = cameraController
        previewView
    })
}

private fun takePicture(
    cameraController: LifecycleCameraController,
    executor: Executor,
    navController: NavController,
    context: Context
) {
    val outputDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile("imageReceta", ".jpg", outputDirectory)

    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    cameraController.takePicture(
        outputOptions, executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName + ".provider",
                    file
                )
                uploadImageToCloudStorage(savedUri, context, navController)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("Error", "Error al guardar la imagen: ${exception.message}")
            }
        }
    )
}

private fun uploadImageToCloudStorage(
    imageUri: Uri,
    context: Context,
    navController: NavController
) {
    val loadingDialog = ProgressDialog(context)
    loadingDialog.setMessage("Por favor espere, estamos obteniendo sus medicamentos")
    loadingDialog.show()

    val storage = Firebase.storage
    val storageRef = storage.reference

    val imageName = "image_${UUID.randomUUID()}.jpg"
    val imageRef = storageRef.child("images/$imageName")

    val uploadTask = imageRef.putFile(imageUri)
    uploadTask.addOnSuccessListener { _ ->
        Log.d("Upload", "Image uploaded successfully")
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Log.d("Download URL", uri.toString())
            sendUrlForAnalysis(uri, context, loadingDialog, navController)
        }
    }.addOnFailureListener { e ->
        Log.e("Upload", "Image upload failed: ${e.message}")
        // Dismiss loading dialog
        loadingDialog.dismiss()
    }
}

private fun sendUrlForAnalysis(
    imageUrl: Uri,
    context: Context,
    loadingDialog: ProgressDialog,
    navController: NavController) {
    val requestQueue = Volley.newRequestQueue(context)
    val url = "https://nutriloc.azurewebsites.net/analyze-document"

    val imageUrlString = imageUrl.toString()

    val jsonBody = JSONObject()
    jsonBody.put("url", imageUrlString)
    println(jsonBody)

    val jsonObjectRequest = JsonObjectRequest(
        Request.Method.POST, url, jsonBody,
        { response ->
            val content = response.optString("content")
            loadingDialog.dismiss()
            navController.navigate(AppScreens.Results.route + "/" + content )
        },
        { error ->
            Log.e("API Request", "Error: ${error.message}")
            Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show()
            loadingDialog.dismiss()
        }
    )

    jsonObjectRequest.setRetryPolicy(
        DefaultRetryPolicy(
            120000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    )

    requestQueue.add(jsonObjectRequest)
}