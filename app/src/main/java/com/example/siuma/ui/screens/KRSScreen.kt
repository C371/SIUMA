package com.example.siuma.ui.screens

import androidx.compose.foundation.clickable
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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun KRSScreen(onBack: () -> Unit) {
    val viewModel: KRSViewModel = viewModel()
    var selectedCourses by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Button(
                onClick = { /* Simpan KRS */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B194C)),
                enabled = selectedCourses.isNotEmpty()
            ) {
                Text("Simpan Rencana Studi (${selectedCourses.size} Mata Kuliah)")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Kartu Rencana Studi (KRS)",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B194C)
            )
            Text(
                text = "Pilih mata kuliah untuk semester ini.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(viewModel.availableCourses) { course ->
                    val isSelected = selectedCourses.contains(course.code)
                    KRSItemCard(course, isSelected) {
                        selectedCourses = if (isSelected) {
                            selectedCourses - course.code
                        } else {
                            selectedCourses + course.code
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KRSItemCard(course: KRSItem, isSelected: Boolean, onToggle: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE8EAF6) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Color(0xFF3F51B5) else Color(0xFFE0E0E0)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "[${course.code}] ${course.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0B194C)
                )
                Text(text = course.dosen, fontSize = 12.sp, color = Color.Gray)
                Text(text = "${course.sks} SKS", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3F51B5))
            }
            Icon(
                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Add,
                contentDescription = null,
                tint = if (isSelected) Color(0xFF3F51B5) else Color.Gray
            )
        }
    }
}
