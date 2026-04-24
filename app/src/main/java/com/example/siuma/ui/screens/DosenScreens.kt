package com.example.siuma.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

@Composable
fun KelasScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    // State untuk menyimpan nama file yang berhasil "diupload" secara lokal
    var fileName by remember { mutableStateOf<String?>(null) }

    val pickPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Simulasi upload: Menyimpan file ke folder internal aplikasi
            val success = saveFileLocally(context, it)
            if (success) {
                fileName = "Materi_Berhasil_Diunggah.pdf"
            }
        }
    }

    BaseDosenScreen(title = "Daftar Kelas", onBack = onBack) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Daftar kelas yang diampu oleh Dosen.")
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { pickPdfLauncher.launch("application/pdf") }) {
                Icon(Icons.Default.UploadFile, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Upload Materi (PDF)")
            }

            fileName?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text("File: $it", color = Color(0xFF2E7D32), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
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

private fun saveFileLocally(context: Context, uri: Uri): Boolean {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "materi_dosen.pdf")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        true
    } catch (e: Exception) {
        false
    }
}
