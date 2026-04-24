package com.example.siuma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KelasScreen(onBack: () -> Unit) {
    BaseDosenScreen(title = "Daftar Kelas", onBack = onBack) {
        Text("Daftar kelas yang diampu oleh Dosen.")
    }
}

@Composable
fun MahasiswaScreen(onBack: () -> Unit) {
    BaseDosenScreen(title = "Daftar Mahasiswa", onBack = onBack) {
        Text("Daftar mahasiswa bimbingan atau mahasiswa di kelas.")
    }
}

@Composable
fun PenelitianScreen(onBack: () -> Unit) {
    BaseDosenScreen(title = "Penelitian", onBack = onBack) {
        Text("Halaman Penelitian dan Pengabdian.")
    }
}

@Composable
fun BaseDosenScreen(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B194C)
        )
        Spacer(modifier = Modifier.height(24.dp))
        content()
    }
}
