package com.skripsi.deteksitanamanbm.solusipengendalian

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TampilanSolusi : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val label = intent.getStringExtra("label") ?: "Label tidak tersedia"
        val solusi = intent.getStringExtra("solusi") ?: "Solusi belum tersedia"
        val imageUriString = intent.getStringExtra("imageUri")

        setContent {
            SolusiScreen(label, solusi, imageUriString)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolusiScreen(label: String, solusi: String, imageUriString: String?) {
    val context = LocalContext.current
    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val cleanedLabel = label.replace("Deteksi :", "").trim()

    LaunchedEffect(imageUriString) {
        imageUriString?.let {
            try {
                val uri = Uri.parse(it)
                bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF00ACC1)),
                title = {
                    Text(
                        "Solusi Pengendalian",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Gambar Penyakit",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(bottom = 16.dp)
                )
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color.Gray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Gambar Tidak Tersedia", color = Color.White, fontSize = 18.sp)
                }
            }

            Text(
                text = "Nama Jenis :",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = cleanedLabel,
                fontSize = 30.sp,
                color = Color.Black
            )

            Text(
                text = "Solusi Pengendalian :",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = solusi.replace("\\n", "\n"),
                fontSize = 35.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth(),
                softWrap = true,
                maxLines = Int.MAX_VALUE
            )
        }
    }
}
