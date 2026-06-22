package com.example.siuma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.siuma.ui.ActionState

@Composable
fun KuisCreateScreen(
    kelasId: Int,
    onBack: () -> Unit,
    onCreated: () -> Unit,
    viewModel: KuisViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    val action = viewModel.actionState
    val isLoading = action is ActionState.Loading

    LaunchedEffect(action, submitted) {
        if (submitted && action is ActionState.Success) {
            android.widget.Toast.makeText(context, "Kuis dibuat.", android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetAction()
            onCreated()
        }
    }

    BackScaffold(title = "Buat Kuis", onBack = onBack) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = judul, onValueChange = { judul = it },
                label = { Text("Judul kuis") }, singleLine = true,
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = deskripsi, onValueChange = { deskripsi = it },
                label = { Text("Deskripsi (opsional)") },
                modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(12.dp)
            )
            if (action is ActionState.Error) {
                Text(action.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    if (judul.isBlank()) {
                        android.widget.Toast.makeText(context, "Judul wajib diisi.", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        submitted = true
                        viewModel.createKuis(kelasId, judul.trim(), deskripsi)
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SimuaNavy)
            ) {
                if (isLoading) CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("Buat Kuis", fontWeight = FontWeight.Bold)
            }
            Text("Setelah kuis dibuat, tambahkan soal dari halaman detail kuis.", fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SoalCreateScreen(
    kuisId: Int,
    onBack: () -> Unit,
    onAdded: () -> Unit,
    viewModel: KuisViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var pertanyaan by remember { mutableStateOf("") }
    var opsiA by remember { mutableStateOf("") }
    var opsiB by remember { mutableStateOf("") }
    var opsiC by remember { mutableStateOf("") }
    var opsiD by remember { mutableStateOf("") }
    var kunci by remember { mutableStateOf("A") }
    var submitted by remember { mutableStateOf(false) }
    val action = viewModel.actionState
    val isLoading = action is ActionState.Loading

    LaunchedEffect(action, submitted) {
        if (submitted && action is ActionState.Success) {
            android.widget.Toast.makeText(context, "Soal ditambahkan.", android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetAction()
            onAdded()
        }
    }

    BackScaffold(title = "Tambah Soal", onBack = onBack) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = pertanyaan, onValueChange = { pertanyaan = it },
                label = { Text("Pertanyaan") },
                modifier = Modifier.fillMaxWidth(), minLines = 2, shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(opsiA, { opsiA = it }, label = { Text("Opsi A") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(opsiB, { opsiB = it }, label = { Text("Opsi B") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(opsiC, { opsiC = it }, label = { Text("Opsi C") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(opsiD, { opsiD = it }, label = { Text("Opsi D") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

            Text("Jawaban Benar", fontWeight = FontWeight.Medium, color = SimuaNavy)
            Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("A", "B", "C", "D").forEach { opt ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = kunci == opt, onClick = { kunci = opt })
                        Text(opt)
                    }
                }
            }

            if (action is ActionState.Error) {
                Text(action.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    when {
                        pertanyaan.isBlank() -> toast(context, "Pertanyaan wajib diisi.")
                        opsiA.isBlank() || opsiB.isBlank() || opsiC.isBlank() || opsiD.isBlank() -> toast(context, "Semua opsi wajib diisi.")
                        else -> {
                            submitted = true
                            viewModel.addSoal(kuisId, pertanyaan.trim(), opsiA.trim(), opsiB.trim(), opsiC.trim(), opsiD.trim(), kunci)
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SimuaNavy)
            ) {
                if (isLoading) CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("Simpan Soal", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun toast(context: android.content.Context, msg: String) {
    android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
}
