package com.example.siuma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NewsItem(val title: String, val date: String, val category: String)

@Composable
fun NewsScreen(onBack: () -> Unit) {
    val newsList = listOf(
        NewsItem("UNS Peringkat 1 Nasional Versi Webometrics", "25 Okt 2023", "Prestasi"),
        NewsItem("Pendaftaran Wisuda Periode VI Dibuka", "20 Okt 2023", "Akademik"),
        NewsItem("Kuliah Umum: Masa Depan AI di Indonesia", "18 Okt 2023", "Seminar"),
        NewsItem("Lomba Inovasi Mahasiswa Nasional 2023", "15 Okt 2023", "Kompetisi")
    )

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
            text = "Berita Kampus",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B194C)
        )
        Text(
            text = "Informasi terbaru seputar Universitas Sebelas Maret.",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(newsList) { news ->
                NewsCard(news)
            }
        }
    }
}

@Composable
fun NewsCard(news: NewsItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFFE8EAF6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Newspaper, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color(0xFF3F51B5))
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Surface(
                    color = Color(0xFFFFE082),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = news.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF795548)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = news.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0B194C)
                )
                Text(
                    text = news.date,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
