package com.skripsi.deteksitanamanbm.utama

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skripsi.deteksitanamanbm.R
import com.skripsi.deteksitanamanbm.deteksi.Deteksi

@Composable
fun AppNavigasi(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "menu") {
        composable("menu") { MenuUtama(navController) }
        composable("mulai_deteksi") { Deteksi() }
        composable("tentang_aplikasi") { TentangAplikasi() }
    }
}

@Composable
fun MenuUtama(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF4DD0E1))
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo), // Ganti sesuai logo kamu
                contentDescription = "Logo Sistem",
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Selamat Datang di Aplikasi",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF006064)
            )

            Text(
                text = "Deteksi Hama dan Penyakit Tanaman Bawang Merah",
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF004D40),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            MenuButton(text = "Mulai Deteksi", iconId = R.drawable.loupe) {
                navController.navigate("mulai_deteksi")
            }


            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(text = "Tentang Aplikasi", iconId = R.drawable.about) {
                navController.navigate("tentang_aplikasi")
            }
        }
    }
}


@Composable
fun MenuButton(
    text: String,
    iconId: Int,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(65.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF00ACC1))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 16.dp),
                tint = Color.Unspecified
            )
            Text(
                text = text,
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
