package com.example.siuma.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val SimuaNavy = Color(0xFF0B194C)
val SimuaGold = Color(0xFFFFC107)
val SimuaBg = Color(0xFFF8F9FA)

/** Kerangka layar dengan judul + tombol kembali (dipakai layar-layar detail). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackScaffold(
    title: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SimuaNavy,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = SimuaBg
    ) { padding ->
        content(padding)
    }
}

@Composable
fun LoadingBox(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = SimuaNavy)
    }
}

@Composable
fun ErrorBox(message: String, onRetry: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            if (onRetry != null) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = SimuaNavy)
                ) { Text("Coba Lagi") }
            }
        }
    }
}

@Composable
fun EmptyBox(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(message, color = Color.LightGray, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

/** Format tanggal ISO-8601 (mis. deadline / submitted_at) menjadi teks Indonesia ringkas. */
fun formatTanggal(iso: String?): String {
    if (iso.isNullOrBlank()) return "-"
    return try {
        val odt = java.time.OffsetDateTime.parse(iso)
        val bulan = listOf(
            "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
            "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
        )
        "${odt.dayOfMonth} ${bulan[odt.monthValue - 1]} ${odt.year}, " +
            "%02d:%02d".format(odt.hour, odt.minute)
    } catch (e: Exception) {
        // Bila hanya tanggal (YYYY-MM-DD) atau format lain, tampilkan apa adanya.
        iso.take(16).replace('T', ' ')
    }
}

/** Nama tampilan berkas dari content Uri (mis. "Tugas1.pdf"). */
fun uriDisplayName(context: Context, uri: Uri): String? =
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (idx >= 0 && cursor.moveToFirst()) cursor.getString(idx) else null
    }

/** Baca seluruh isi berkas dari content Uri menjadi bytes (untuk unggah multipart). */
fun uriBytes(context: Context, uri: Uri): ByteArray? = try {
    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
} catch (e: Exception) {
    null
}

fun uriMime(context: Context, uri: Uri): String =
    context.contentResolver.getType(uri) ?: "application/octet-stream"

/** Buka URL (mis. file_url materi/tugas) di aplikasi luar/peramban. */
fun openUrl(context: Context, url: String?) {
    if (url.isNullOrBlank()) {
        android.widget.Toast.makeText(context, "Berkas tidak tersedia.", android.widget.Toast.LENGTH_SHORT).show()
        return
    }
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: ActivityNotFoundException) {
        android.widget.Toast.makeText(context, "Tidak ada aplikasi untuk membuka berkas.", android.widget.Toast.LENGTH_SHORT).show()
    }
}
