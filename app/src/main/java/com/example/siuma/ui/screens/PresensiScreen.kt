package com.example.siuma.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
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
import com.example.siuma.data.remote.dto.PesertaPresensiDto
import com.example.siuma.data.remote.dto.PresensiMahasiswaDto
import com.example.siuma.data.remote.dto.PresensiPertemuanDto
import com.example.siuma.ui.ActionState
import com.example.siuma.ui.UiState

private val STATUS_OPTS = listOf("hadir", "izin", "sakit", "alpa")

private fun statusLabel(s: String?): String = when (s) {
    "hadir" -> "Hadir"; "izin" -> "Izin"; "sakit" -> "Sakit"; "alpa" -> "Alpa"; else -> "-"
}

private fun statusColors(s: String?): Pair<Color, Color> = when (s) {
    "hadir" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
    "izin" -> Color(0xFFFFF8E1) to Color(0xFFEF6C00)
    "sakit" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
    "alpa" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    else -> Color(0xFFEEEEEE) to Color.Gray
}

@Composable
fun PresensiScreen(
    kelasId: Int,
    isDosen: Boolean,
    onBack: () -> Unit,
    viewModel: PresensiViewModel = hiltViewModel()
) {
    LaunchedEffect(kelasId, isDosen) {
        if (isDosen) viewModel.loadDosen(kelasId, null) else viewModel.loadMahasiswa(kelasId)
    }

    BackScaffold(title = "Presensi", onBack = onBack) { padding ->
        if (isDosen) DosenPresensi(kelasId, viewModel, Modifier.padding(padding))
        else MahasiswaPresensi(kelasId, viewModel, Modifier.padding(padding))
    }
}

// ---- Mahasiswa ----

@Composable
private fun MahasiswaPresensi(kelasId: Int, viewModel: PresensiViewModel, modifier: Modifier) {
    when (val state = viewModel.mahasiswaState) {
        is UiState.Loading -> LoadingBox(modifier)
        is UiState.Error -> ErrorBox(state.message, onRetry = { viewModel.loadMahasiswa(kelasId) }, modifier = modifier)
        is UiState.Success -> {
            val data = state.data
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { RekapCard(data) }
                if (data.pertemuan.isEmpty()) {
                    item { EmptyBox("Belum ada catatan kehadiran.") }
                } else {
                    item {
                        Text("Riwayat Kehadiran", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SimuaNavy)
                    }
                    items(data.pertemuan) { p -> PertemuanRow(p) }
                }
            }
        }
    }
}

@Composable
private fun RekapCard(data: PresensiMahasiswaDto) {
    val r = data.rekap
    val total = r.hadir + r.izin + r.sakit + r.alpa
    val persen = if (total > 0) r.hadir * 100 / total else 0
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Rekap Kehadiran", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SimuaNavy)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                RekapCount("Hadir", r.hadir, Color(0xFF2E7D32))
                RekapCount("Izin", r.izin, Color(0xFFEF6C00))
                RekapCount("Sakit", r.sakit, Color(0xFF1565C0))
                RekapCount("Alpa", r.alpa, Color(0xFFC62828))
            }
            Spacer(Modifier.height(12.dp))
            Text("Persentase kehadiran: $persen%", fontWeight = FontWeight.Medium, color = SimuaNavy)
        }
    }
}

@Composable
private fun RekapCount(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$count", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = color)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun PertemuanRow(p: PresensiPertemuanDto) {
    val (bg, fg) = statusColors(p.status)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Pertemuan ke-${p.pertemuanKe}", fontWeight = FontWeight.Bold, color = SimuaNavy)
                p.tanggal?.let { Text(it, fontSize = 12.sp, color = Color.Gray) }
            }
            StatusChip(statusLabel(p.status), bg, fg)
        }
    }
}

// ---- Dosen ----

@Composable
private fun DosenPresensi(kelasId: Int, viewModel: PresensiViewModel, modifier: Modifier) {
    val context = LocalContext.current
    var saveSubmitted by remember { mutableStateOf(false) }
    val action = viewModel.actionState

    LaunchedEffect(action, saveSubmitted) {
        if (saveSubmitted && action is ActionState.Success) {
            android.widget.Toast.makeText(context, "Presensi disimpan.", android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetAction()
            saveSubmitted = false
        }
    }

    when (val state = viewModel.dosenState) {
        is UiState.Loading -> LoadingBox(modifier)
        is UiState.Error -> ErrorBox(state.message, onRetry = { viewModel.loadDosen(kelasId, null) }, modifier = modifier)
        is UiState.Success -> {
            val data = state.data
            Column(modifier.fillMaxSize()) {
                // Pemilih pertemuan
                Column(Modifier.padding(16.dp)) {
                    Text("Pertemuan ke-${viewModel.selectedPertemuan}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SimuaNavy)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        data.pertemuanList.forEach { n ->
                            FilterChip(
                                selected = viewModel.selectedPertemuan == n,
                                onClick = { viewModel.selectPertemuan(kelasId, n) },
                                label = { Text("Ke-$n") }
                            )
                        }
                        AssistChip(onClick = { viewModel.newPertemuan() }, label = { Text("+ Baru") })
                    }
                }
                HorizontalDivider()
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (data.peserta.isEmpty()) {
                        item { EmptyBox("Belum ada peserta.") }
                    } else {
                        items(data.peserta) { p ->
                            PesertaPresensiRow(
                                peserta = p,
                                selected = viewModel.statuses[p.krsId.toString()],
                                onSelect = { viewModel.setStatus(p.krsId, it) }
                            )
                        }
                    }
                }
                Button(
                    onClick = { saveSubmitted = true; viewModel.save(kelasId) },
                    enabled = action !is ActionState.Loading && data.peserta.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SimuaNavy)
                ) {
                    if (action is ActionState.Loading) {
                        CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Simpan Presensi", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PesertaPresensiRow(peserta: PesertaPresensiDto, selected: String?, onSelect: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(peserta.nama, fontWeight = FontWeight.Bold, color = SimuaNavy)
            peserta.nim?.let { Text("NIM: $it", fontSize = 12.sp, color = Color.Gray) }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                STATUS_OPTS.forEach { opt ->
                    val isSel = selected == opt
                    val (bg, fg) = statusColors(opt)
                    FilterChip(
                        selected = isSel,
                        onClick = { onSelect(opt) },
                        label = { Text(statusLabel(opt), fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = bg,
                            selectedLabelColor = fg
                        )
                    )
                }
            }
        }
    }
}
