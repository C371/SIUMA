package com.example.siuma.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.siuma.data.remote.dto.AkademikDto
import com.example.siuma.data.remote.dto.KhsItemDto
import com.example.siuma.data.remote.dto.KrsItemDto
import com.example.siuma.data.remote.dto.RekapBarisDto
import com.example.siuma.ui.UiState

// ============================ Akademik (KRS + KHS) ============================

@Composable
fun AkademikScreen(
    onBack: () -> Unit,
    viewModel: AkademikViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.loadAkademik() }

    BackScaffold(title = "Akademik", onBack = onBack) { padding ->
        when (val state = viewModel.akademikState) {
            is UiState.Loading -> LoadingBox(Modifier.padding(padding))
            is UiState.Error -> ErrorBox(state.message, onRetry = { viewModel.loadAkademik() }, modifier = Modifier.padding(padding))
            is UiState.Success -> AkademikContent(state.data, Modifier.padding(padding))
        }
    }
}

@Composable
private fun AkademikContent(data: AkademikDto, modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { RingkasanAkademikCard(data) }

        item { SectionTitle("Rencana Studi (KRS)") }
        if (data.krs.isEmpty()) {
            item { Text("Belum ada mata kuliah.", color = Color.LightGray, fontSize = 13.sp) }
        } else {
            items(data.krs) { KrsCard(it) }
        }

        item { Spacer(Modifier.height(4.dp)); SectionTitle("Hasil Studi (KHS)") }
        if (data.khs.isEmpty()) {
            item { Text("Belum ada nilai.", color = Color.LightGray, fontSize = 13.sp) }
        } else {
            items(data.khs) { KhsCard(it) }
        }
    }
}

@Composable
private fun RingkasanAkademikCard(data: AkademikDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SimuaNavy),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            Modifier.padding(20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total SKS", color = SimuaGold, fontSize = 13.sp)
                Text("${data.totalSks}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("IP Kumulatif", color = SimuaGold, fontSize = 13.sp)
                Text("%.2f".format(data.ip), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SimuaNavy)
}

@Composable
private fun KrsCard(item: KrsItemDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(item.nama, fontWeight = FontWeight.Bold, color = SimuaNavy)
                Text(
                    listOfNotNull(item.kode, item.dosen, item.semester).joinToString(" • "),
                    fontSize = 12.sp, color = Color.Gray
                )
            }
            StatusChip("${item.sks} SKS", Color(0xFFF0F4F8), SimuaNavy)
        }
    }
}

@Composable
private fun KhsCard(item: KhsItemDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(item.nama, fontWeight = FontWeight.Bold, color = SimuaNavy)
                Text("${item.kode} • ${item.sks} SKS", fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(item.nilaiHuruf ?: "-", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = SimuaNavy)
                item.nilaiAngka?.let { Text("%.2f".format(it), fontSize = 11.sp, color = Color.Gray) }
            }
        }
    }
}

// ============================ Rekap Presensi ============================

@Composable
fun RekapPresensiScreen(
    isDosen: Boolean,
    onBack: () -> Unit,
    viewModel: AkademikViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.loadRekap() }

    BackScaffold(title = "Rekap Presensi", onBack = onBack) { padding ->
        when (val state = viewModel.rekapState) {
            is UiState.Loading -> LoadingBox(Modifier.padding(padding))
            is UiState.Error -> ErrorBox(state.message, onRetry = { viewModel.loadRekap() }, modifier = Modifier.padding(padding))
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyBox("Belum ada data rekap.", Modifier.padding(padding))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data) { baris ->
                            if (isDosen) RekapKelasCard(baris) else RekapMataKuliahCard(baris)
                        }
                    }
                }
            }
        }
    }
}

/** Sisi mahasiswa: satu kartu per mata kuliah. */
@Composable
private fun RekapMataKuliahCard(b: RekapBarisDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(b.nama ?: "-", fontWeight = FontWeight.Bold, color = SimuaNavy)
            b.kode?.let { Text(it, fontSize = 12.sp, color = Color.Gray) }
            Spacer(Modifier.height(8.dp))
            RekapCountRow(b)
            Spacer(Modifier.height(8.dp))
            PersentaseBar(b.persentase)
        }
    }
}

/** Sisi dosen: satu kartu per kelas berisi daftar mahasiswa. */
@Composable
private fun RekapKelasCard(b: RekapBarisDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(b.nama ?: "-", fontWeight = FontWeight.Bold, color = SimuaNavy, fontSize = 16.sp)
            b.kode?.let { Text(it, fontSize = 12.sp, color = Color.Gray) }
            Spacer(Modifier.height(8.dp))
            val mhs = b.mahasiswa.orEmpty()
            if (mhs.isEmpty()) {
                Text("Belum ada mahasiswa.", color = Color.LightGray, fontSize = 13.sp)
            } else {
                mhs.forEach { m ->
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Column(Modifier.padding(vertical = 8.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(m.nama ?: "-", fontWeight = FontWeight.Medium, color = SimuaNavy)
                            Text("${m.persentase}%", fontWeight = FontWeight.Bold, color = SimuaNavy)
                        }
                        Spacer(Modifier.height(4.dp))
                        RekapCountRow(m)
                    }
                }
            }
        }
    }
}

@Composable
private fun RekapCountRow(b: RekapBarisDto) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MiniCount("H", b.hadir, Color(0xFF2E7D32))
        MiniCount("I", b.izin, Color(0xFFEF6C00))
        MiniCount("S", b.sakit, Color(0xFF1565C0))
        MiniCount("A", b.alpa, Color(0xFFC62828))
        Spacer(Modifier.weight(1f))
        Text("Total ${b.total}", fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun MiniCount(label: String, count: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label:", fontSize = 12.sp, color = Color.Gray)
        Spacer(Modifier.width(2.dp))
        Text("$count", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun PersentaseBar(persen: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        LinearProgressIndicator(
            progress = { persen / 100f },
            modifier = Modifier.weight(1f).height(8.dp),
            color = SimuaNavy,
            trackColor = Color(0xFFEEEEEE)
        )
        Spacer(Modifier.width(8.dp))
        Text("$persen%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SimuaNavy)
    }
}
