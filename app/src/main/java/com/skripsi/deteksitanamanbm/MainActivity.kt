package com.skripsi.deteksitanamanbm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.*
import com.skripsi.deteksitanamanbm.utama.AppNavigasi
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash screen dari XML otomatis tampil di sini
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContent {
            AppNavigasi() // Tampilkan navigasi utama langsung
        }
    }
}
