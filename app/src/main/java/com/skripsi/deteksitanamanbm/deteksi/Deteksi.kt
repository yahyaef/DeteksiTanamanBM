package com.skripsi.deteksitanamanbm.deteksi

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.skripsi.deteksitanamanbm.R
import com.skripsi.deteksitanamanbm.solusipengendalian.TampilanSolusi
import com.skripsi.deteksitanamanbm.model.ImageClassifier

class TampilanDeteksi : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Deteksi()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Deteksi() {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraBitmap = remember { mutableStateOf<Bitmap?>(null) }

    var prediction by remember { mutableStateOf("Belum ada deteksi") }
    var accuracy by remember { mutableStateOf(0.0f) }
    var solusiText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            imageUri = it
            cameraBitmap.value = null
            prediction = "Belum ada deteksi"
            accuracy = 0.0f
            solusiText = ""
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            cameraBitmap.value = it
            imageUri = null
            prediction = "Belum ada deteksi"
            accuracy = 0.0f
            solusiText = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF00ACC1)),
                title = {
                    Text(
                        "Sistem Deteksi",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color(0xFF00ACC1)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "© 2025 Tugas Akhir Teknik Informatika",
                        fontSize = 20.sp,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                    )
                }
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.LightGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        imageUri != null -> {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Preview",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(8.dp)
                            )
                        }
                        cameraBitmap.value != null -> {
                            Image(
                                bitmap = cameraBitmap.value!!.asImageBitmap(),
                                contentDescription = "Camera Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(8.dp)
                            )
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .background(Color.Gray)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    prediction,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp, // perbesar sesuai kebutuhan
                    textAlign = TextAlign.Center,
                    color = when {
                        prediction.contains("bukan tanaman Bawang Merah", ignoreCase = true) -> Color.Red
                        prediction.contains("Tidak Diketahui", ignoreCase = true) -> Color.Red
                        else -> Color.Black
                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                )
                //akurasi
                if (accuracy > 0.0f) {
                    Text(
                        "Akurasi: ${"%.2f".format(accuracy)}%",
                        color = Color.Green,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Upload Gambar",
                            fontSize = 30.sp)
                    }

                    Button(
                        onClick = {
                            val bitmap: Bitmap? = imageUri?.let {
                                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                            } ?: cameraBitmap.value

                            if (bitmap != null) {
                                val imageClassifier = ImageClassifier(context)
                                val result = imageClassifier.classifyImage(bitmap)
                                val confidencePercent = result.confidence * 100f

                                if (confidencePercent < 60f) {
                                    prediction = "Deteksi Tidak Diketahui"
                                    //accuracy = confidencePercent
                                    solusiText = ""
                                } else {
                                    prediction = "Deteksi : ${result.label}"
                                    solusiText = imageClassifier.getSolution(result.label)
                                }

                                imageClassifier.close()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(70.dp),
                    ) {
                        Text(
                            "Proses Deteksi",
                            fontSize = 30.sp)
                    }

                    Button(
                        onClick = {
                            val label = prediction.removePrefix("Deteksi: ").trim()
                            val intent = Intent(context, TampilanSolusi::class.java)
                            intent.putExtra("label", label)
                            intent.putExtra("solusi", solusiText)
                            intent.putExtra("imageUri", imageUri.toString())

                            context.startActivity(intent)
                        },
                        enabled = prediction.startsWith("Deteksi : "),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            "Solusi Pengendalian",
                            fontSize = 30.sp)
                    }
                }

                if (showDialog) {
                    ModalBottomSheet(
                        onDismissRequest = { showDialog = false },
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        containerColor = Color(0xFFE0F7FA),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable {
                                            showDialog = false
                                            cameraLauncher.launch(null)
                                        }
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.care),
                                        contentDescription = "Kamera",
                                        modifier = Modifier.size(60.dp)
                                    )
                                    Text("kamera", fontSize = 16.sp, color = Color.Black)
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable {
                                            showDialog = false
                                            galleryLauncher.launch("image/*")
                                        }
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.file),
                                        contentDescription = "File",
                                        modifier = Modifier.size(60.dp)
                                    )
                                    Text("file", fontSize = 16.sp, color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}