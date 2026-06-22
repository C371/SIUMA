package com.example.siuma.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.siuma.data.remote.dto.HasilKuisDto
import com.example.siuma.data.remote.dto.KuisDetailDto
import com.example.siuma.data.remote.dto.KuisDto
import com.example.siuma.data.remote.dto.SoalDto
import com.example.siuma.ui.ActionState
import com.example.siuma.ui.UiState

// ============================ Daftar Kuis ============================

@Composable
fun KuisListScreen(
    kelasId: Int,
    isDosen: Boolean,
    onBack: () -> Unit,
    onOpenKuis: (Int) -> Unit,
    onCreateKuis: (Int) -> Unit,
    viewModel: KuisViewModel = hiltViewModel()
) {
    LaunchedEffect(kelasId) { viewModel.loadList(kelasId) }

    BackScaffold(
        title = "Kuis",
        onBack = onBack,
        actions = {
            if (isDosen) {
                IconButton(onClick = { onCreateKuis(kelasId) }) {
                    Icon(Icons.Default.Add, contentDescription = "Buat kuis")
                }
            }
        }
    ) { padding ->
        when (val state = viewModel.listState) {
            is UiState.Loading -> LoadingBox(Modifier.padding(padding))
            is UiState.Error -> ErrorBox(state.message, onRetry = { viewModel.loadList(kelasId) }, modifier = Modifier.padding(padding))
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyBox("Belum ada kuis.", Modifier.padding(padding))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data) { kuis -> KuisCard(kuis, isDosen, onClick = { onOpenKuis(kuis.id) }) }
                    }
                }
            }
        }
    }
}

@Composable
private fun KuisCard(kuis: KuisDto, isDosen: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(kuis.judul, fontWeight = FontWeight.Bold, color = SimuaNavy, fontSize = 16.sp)
            kuis.deskripsi?.takeIf { it.isNotBlank() }?.let {
                Text(it, fontSize = 13.sp, color = Color.Gray, maxLines = 2)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip("${kuis.jumlahSoal} soal", Color(0xFFF0F4F8), SimuaNavy)
                if (!isDosen) {
                    when {
                        kuis.skor != null -> StatusChip("Skor: ${kuis.skor}", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                        kuis.sudahDikerjakan == true -> StatusChip("Selesai", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                        else -> StatusChip("Belum dikerjakan", Color(0xFFFFF3E0), Color(0xFFEF6C00))
                    }
                }
            }
        }
    }
}

// ============================ Detail Kuis ============================

@Composable
fun KuisDetailScreen(
    kuisId: Int,
    isDosen: Boolean,
    onBack: () -> Unit,
    onAddSoal: (Int) -> Unit,
    viewModel: KuisViewModel = hiltViewModel()
) {
    LaunchedEffect(kuisId) { viewModel.loadDetail(kuisId) }

    BackScaffold(
        title = "Detail Kuis",
        onBack = onBack,
        actions = {
            if (isDosen) {
                IconButton(onClick = { onAddSoal(kuisId) }) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah soal")
                }
            }
        }
    ) { padding ->
        when (val state = viewModel.detailState) {
            is UiState.Loading -> LoadingBox(Modifier.padding(padding))
            is UiState.Error -> ErrorBox(state.message, onRetry = { viewModel.loadDetail(kuisId) }, modifier = Modifier.padding(padding))
            is UiState.Success -> {
                val kuis = state.data
                if (isDosen) DosenKuisContent(kuis, Modifier.padding(padding))
                else MahasiswaKuisContent(kuis, viewModel, Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun KuisHeaderCard(kuis: KuisDetailDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SimuaNavy),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(kuis.judul, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("${kuis.kelas.kode} • ${kuis.jumlahSoal} soal", color = SimuaGold, fontSize = 13.sp)
            kuis.deskripsi?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

// ---- Mahasiswa: kerjakan / lihat hasil ----

@Composable
private fun MahasiswaKuisContent(kuis: KuisDetailDto, viewModel: KuisViewModel, modifier: Modifier) {
    val context = LocalContext.current
    val soal = kuis.soal.orEmpty()
    val jawaban = remember(kuis.id) { mutableStateMapOf<String, String>() }
    var submitted by remember(kuis.id) { mutableStateOf(false) }
    val action = viewModel.actionState
    val isLoading = action is ActionState.Loading

    LaunchedEffect(action, submitted) {
        if (submitted && action is ActionState.Success) {
            android.widget.Toast.makeText(context, "Kuis selesai dikerjakan.", android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetAction()
            submitted = false
        }
    }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        KuisHeaderCard(kuis)

        if (kuis.sudahDikerjakan == true && kuis.hasilSaya != null) {
            // Sudah dikerjakan → tampilkan hasil
            val h = kuis.hasilSaya
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Skor Anda", fontSize = 14.sp, color = Color(0xFF2E7D32))
                    Text("${h.skor ?: 0}", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    Text("Benar ${h.benar ?: 0} dari ${h.total ?: 0} soal", fontSize = 13.sp, color = Color(0xFF2E7D32))
                    h.submittedAt?.let { Text("Dikerjakan: ${formatTanggal(it)}", fontSize = 11.sp, color = Color.Gray) }
                }
            }
        } else if (soal.isEmpty()) {
            EmptyBox("Kuis ini belum memiliki soal.")
        } else {
            // Form pengerjaan
            soal.forEachIndexed { index, s ->
                SoalKerjakanCard(index + 1, s, selected = jawaban[s.id.toString()], onSelect = { jawaban[s.id.toString()] = it })
            }
            if (action is ActionState.Error) {
                Text(action.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }
            Button(
                onClick = {
                    submitted = true
                    viewModel.kerjakan(kuis.id, jawaban.toMap())
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SimuaNavy)
            ) {
                if (isLoading) CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("Kumpulkan Jawaban", fontWeight = FontWeight.Bold)
            }
            Text(
                "Soal yang tidak dijawab dianggap salah. Kuis hanya dapat dikerjakan sekali.",
                fontSize = 11.sp, color = Color.Gray
            )
        }
    }
}

@Composable
private fun SoalKerjakanCard(nomor: Int, soal: SoalDto, selected: String?, onSelect: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("$nomor. ${soal.pertanyaan}", fontWeight = FontWeight.Bold, color = SimuaNavy, fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))
            OpsiRadio("A", soal.opsiA, selected, onSelect)
            OpsiRadio("B", soal.opsiB, selected, onSelect)
            OpsiRadio("C", soal.opsiC, selected, onSelect)
            OpsiRadio("D", soal.opsiD, selected, onSelect)
        }
    }
}

@Composable
private fun OpsiRadio(kode: String, teks: String, selected: String?, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected == kode, onClick = { onSelect(kode) })
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected == kode, onClick = { onSelect(kode) })
        Text("$kode. $teks", fontSize = 14.sp, color = Color.DarkGray)
    }
}

// ---- Dosen: lihat soal + hasil ----

@Composable
private fun DosenKuisContent(kuis: KuisDetailDto, modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        KuisHeaderCard(kuis)

        Text("Soal", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SimuaNavy)
        val soal = kuis.soal.orEmpty()
        if (soal.isEmpty()) {
            Text("Belum ada soal. Ketuk + untuk menambah.", color = Color.LightGray, fontSize = 13.sp)
        } else {
            soal.forEachIndexed { index, s -> SoalDosenCard(index + 1, s) }
        }

        Spacer(Modifier.height(8.dp))
        Text("Hasil Mahasiswa", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SimuaNavy)
        val hasil = kuis.hasil.orEmpty()
        if (hasil.isEmpty()) {
            Text("Belum ada yang mengerjakan.", color = Color.LightGray, fontSize = 13.sp)
        } else {
            hasil.forEach { HasilRow(it) }
        }
    }
}

@Composable
private fun SoalDosenCard(nomor: Int, soal: SoalDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("$nomor. ${soal.pertanyaan}", fontWeight = FontWeight.Bold, color = SimuaNavy, fontSize = 15.sp)
            Spacer(Modifier.height(6.dp))
            OpsiKunci("A", soal.opsiA, soal.jawabanBenar)
            OpsiKunci("B", soal.opsiB, soal.jawabanBenar)
            OpsiKunci("C", soal.opsiC, soal.jawabanBenar)
            OpsiKunci("D", soal.opsiD, soal.jawabanBenar)
        }
    }
}

@Composable
private fun OpsiKunci(kode: String, teks: String, kunci: String?) {
    val benar = kode.equals(kunci, ignoreCase = true)
    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        if (benar) {
            Icon(Icons.Default.CheckCircle, contentDescription = "Kunci", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
        } else {
            Spacer(Modifier.size(16.dp))
        }
        Spacer(Modifier.width(8.dp))
        Text(
            "$kode. $teks",
            fontSize = 14.sp,
            color = if (benar) Color(0xFF2E7D32) else Color.DarkGray,
            fontWeight = if (benar) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun HasilRow(h: HasilKuisDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(h.mahasiswa ?: "-", fontWeight = FontWeight.Bold, color = SimuaNavy)
                Text("Benar ${h.benar ?: 0}/${h.total ?: 0}", fontSize = 12.sp, color = Color.Gray)
            }
            Text("${h.skor ?: 0}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = SimuaNavy)
        }
    }
}
