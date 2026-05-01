package com.example.siuma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class KHSItem(val name: String,val semester: Int, val sks: Int, val grade: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KHSScreen(onBack: () -> Unit) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var selectedSemester by remember { mutableStateOf("Semester 5 (Gasal 2023)") }

    val matakuliahDoned = listOf(
        KHSItem("Pengembangan Aplikasi Bergerak",4, 3, "A"),
        KHSItem("Kecerdasan Buatan",4, 3, "A-"),
        KHSItem("Basis Data",3, 4, "B+"),
        KHSItem("Sistem Operasi",3, 3, "A"),
        KHSItem("Teori Bahasa & Automata",4, 3, "B"),
        KHSItem("Jaringan Komputer",3, 4, "B+"),
        KHSItem("Pemrograman Web",4, 3, "A")
    )

    val totalSKS = matakuliahDoned.sumOf { it.sks }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Kartu Hasil Studi (KHS)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B194C)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Semester Selector Button
            OutlinedButton(
                onClick = { showSheet = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0B194C))
            ) {
                Icon(Icons.Default.FilterList, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(selectedSemester)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // IPK Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B194C)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("IP Semester", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text("3.75", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    }

                    VerticalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = Color.White.copy(alpha = 0.2f)
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("SKS Diambil", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text("$totalSKS", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Daftar Nilai",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF0B194C),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Grade List (LazyColumn)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(matakuliahDoned) { item ->
                    GradeItem(item)
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }

        // BottomSheet for Semester Selection
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp, start = 24.dp, end = 24.dp, top = 8.dp)
                ) {
                    Text(
                        "Pilih Semester",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0B194C)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val semesters = listOf(
                        "Semester 1",
                        "Semester 2",
                        "Semester 3",
                        "Semester 4",
                        "Semester 5"
                    ).reversed()

                    semesters.forEach { semester ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSemester = semester
                                    showSheet = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = semester,
                                modifier = Modifier.weight(1f),
                                color = if (selectedSemester == semester) Color(0xFF0B194C) else Color.Gray,
                                fontWeight = if (selectedSemester == semester) FontWeight.Bold else FontWeight.Normal
                            )
                            if (selectedSemester == semester) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF0B194C))
                            }
                        }
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                    }
                }
            }
        }
    }
}

@Composable
fun GradeItem(item: KHSItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0B194C))
                Text("${item.sks} SKS", fontSize = 12.sp, color = Color.Gray)
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = when (item.grade) {
                            "A", "A-" -> Color(0xFFE8F5E9)
                            "B+", "B" -> Color(0xFFFFF3E0)
                            "C+", "C" -> Color(0xFFFDE7E8)
                            "D", "E" -> Color(0xFFFBCFE8)
                            else -> Color(0xFFFBE9E7)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    item.grade,
                    fontWeight = FontWeight.Bold,
                    color = when (item.grade) {
                        "A", "A-" -> Color(0xFF2E7D32)
                        "B+", "B" -> Color(0xFFEF6C00)
                        "C+", "C" -> Color(0xFFAB47BC)
                        "D", "E" -> Color(0xFF880E4F)
                        else -> Color(0xFFD84315)
                    }
                )
            }
        }
    }
}
