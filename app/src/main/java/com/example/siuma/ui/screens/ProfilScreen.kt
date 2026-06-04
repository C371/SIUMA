package com.example.siuma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.siuma.R

@Composable
fun ProfilScreen(isDosen: Boolean, userName: String, userId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0B194C))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.pasfoto)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto Profil",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    userName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    if (isDosen) "Dosen Informatika" else "Mahasiswa Informatika",
                    color = Color(0xFFFFC107),
                    fontSize = 14.sp
                )
            }
        }

        // Profile Details
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileInfoCard(
                title = "Informasi Pribadi",
                items = if (isDosen) listOf(
                    "Nama" to userId,
                    "Email" to "$userId@staff.uns.ac.id",
                    "Jabatan" to "Dosen",
                    "Keahlian" to "Kecerdasan Buatan"
                ) else listOf(
                    "Nama" to userId,
                    "Email" to "$userId@student.uns.ac.id",
                    "Semester" to "5 (Gasal)",
                    "Dosen PA" to "Arif Rohmadi S.Kom., M.Cs"
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ProfileInfoCard(
                title = "Akademik",
                items = if (isDosen) listOf(
                    "Fakultas" to "Teknologi Informasi dan Data",
                    "Program Studi" to "Informatika",
                    "Status" to "Aktif Mengajar"
                ) else listOf(
                    "Fakultas" to "Teknologi Informasi dan Data",
                    "Program Studi" to "Informatika",
                    "Angkatan" to "2021",
                    "Status" to "Mahasiswa Aktif"
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedButton(
                onClick = { /* Ubah Password */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0B194C))
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ubah Kata Sandi")
            }
        }
    }
}

@Composable
fun ProfileInfoCard(title: String, items: List<Pair<String, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF0B194C)
            )
            Spacer(modifier = Modifier.height(12.dp))
            items.forEachIndexed { index, (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, color = Color.Gray, fontSize = 14.sp)
                    Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.Black)
                }
                if (index < items.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFF5F5F5))
                }
            }
        }
    }
}
