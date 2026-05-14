package com.example.siuma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.CalendarToday
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PresensiScreen(isDosen: Boolean = false, onBack: () -> Unit) {
    val viewModel: AcademicViewModel = viewModel()
    val history = if (isDosen) viewModel.historyPresensiDosen else viewModel.historyPresensi
    val stats = viewModel.getPresensiStats(isDosen)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Presensi Mahasiswa",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B194C)
        )
        Text(
            text = "Riwayat kehadiran perkuliahan Anda.",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Presensi Stats Graph Simulation
        PresensiStatsSection(stats)

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (isDosen) "Riwayat Mengajar" else "Riwayat Presensi",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B194C)
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(history) { item ->
                PresensiCard(item)
            }
        }
    }
}

@Composable
fun PresensiCard(item: PresensiItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.subject, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0B194C))
                Text("${item.date} • ${item.time}", fontSize = 12.sp, color = Color.Gray)
                if (item.materi.isNotEmpty()) {
                    Text(
                        text = "Materi: ${item.materi}",
                        fontSize = 11.sp,
                        color = Color(0xFF3F51B5),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            Surface(
                color = if (item.status == "Hadir") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = item.status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    fontSize = 10.sp,
                    color = if (item.status == "Hadir") Color(0xFF2E7D32) else Color(0xFFEF6C00),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PresensiStatsSection(stats: Map<String, Int>) {
    val total = stats.values.sum().toFloat()
    val hadir = stats["Hadir"] ?: 0
    val izin = stats["Izin"] ?: 0
    val alpha = stats["Alpha"] ?: 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Statistik Kehadiran",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF0B194C)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Simple Horizontal Graph
                Column(modifier = Modifier.weight(1f)) {
                    StatBar(label = "Hadir", count = hadir, total = total, color = Color(0xFF4CAF50))
                    StatBar(label = "Izin", count = izin, total = total, color = Color(0xFFFF9800))
                    StatBar(label = "Alpha", count = alpha, total = total, color = Color(0xFFF44336))
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                // Percentage Box
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFE3F2FD), RoundedCornerShape(40.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val percentage = if (total > 0) (hadir / total * 100).toInt() else 0
                    Text(
                        "$percentage%",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color(0xFF1976D2)
                    )
                }
            }
        }
    }
}

@Composable
fun StatBar(label: String, count: Int, total: Float, color: Color) {
    val fraction = if (total > 0) count / total else 0f
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text("$count", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color(0xFFEEEEEE), RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(4.dp))
            )
        }
    }
}
