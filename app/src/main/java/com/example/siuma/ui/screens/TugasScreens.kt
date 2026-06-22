package com.example.siuma.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.siuma.data.remote.dto.PengumpulanMahasiswaDto
import com.example.siuma.data.remote.dto.PengumpulanSayaDto
import com.example.siuma.data.remote.dto.TugasDetailDto
import com.example.siuma.data.remote.dto.TugasDto
import com.example.siuma.ui.ActionState
import com.example.siuma.ui.UiState

// ============================ Daftar Tugas ============================

@Composable
fun TugasListScreen(
    kelasId: Int,
    isDosen: Boolean,
    onBack: () -> Unit,
    onOpenTugas: (Int) -> Unit,
    onCreateTugas: (Int) -> Unit,
    viewModel: TugasViewModel = hiltViewModel()
) {
    LaunchedEffect(kelasId) { viewModel.loadList(kelasId) }

    BackScaffold(
        title = "Tugas",
        onBack = onBack,
        actions = {
            if (isDosen) {
                IconButton(onClick = { onCreateTugas(kelasId) }) {
                    Icon(Icons.Default.Add, contentDescription = "Buat tugas")
                }
            }
        }
    ) { padding ->
        when (val state = viewModel.listState) {
            is UiState.Loading -> LoadingBox(Modifier.padding(padding))
            is UiState.Error -> ErrorBox(state.message, onRetry = { viewModel.loadList(kelasId) }, modifier = Modifier.padding(padding))
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyBox("Belum ada tugas.", Modifier.padding(padding))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data) { tugas ->
                            TugasCard(tugas, isDosen, onClick = { onOpenTugas(tugas.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TugasCard(tugas: TugasDto, isDosen: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(tugas.judul, fontWeight = FontWeight.Bold, color = SimuaNavy, fontSize = 16.sp)
            tugas.deadline?.let {
                Text("Tenggat: ${formatTanggal(it)}", fontSize = 12.sp, color = Color(0xFFD32F2F))
            }
            tugas.deskripsi?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, fontSize = 13.sp, color = Color.Gray, maxLines = 2)
            }
            Spacer(Modifier.height(8.dp))
            if (isDosen) {
                StatusChip("${tugas.jumlahPengumpulan ?: 0} pengumpulan", Color(0xFFE3F2FD), Color(0xFF1565C0))
            } else {
                when {
                    tugas.nilai != null -> StatusChip("Nilai: ${tugas.nilai}", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                    tugas.sudahMengumpulkan == true -> StatusChip("Sudah dikumpulkan", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                    else -> StatusChip("Belum dikumpulkan", Color(0xFFFFF3E0), Color(0xFFEF6C00))
                }
            }
        }
    }
}

@Composable
fun StatusChip(text: String, bg: Color, fg: Color) {
    Surface(color = bg, shape = RoundedCornerShape(6.dp)) {
        Text(text, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 11.sp, color = fg, fontWeight = FontWeight.Medium)
    }
}

// ============================ Detail Tugas ============================

@Composable
fun TugasDetailScreen(
    tugasId: Int,
    isDosen: Boolean,
    onBack: () -> Unit,
    viewModel: TugasViewModel = hiltViewModel()
) {
    LaunchedEffect(tugasId) { viewModel.loadDetail(tugasId) }

    BackScaffold(title = "Detail Tugas", onBack = onBack) { padding ->
        when (val state = viewModel.detailState) {
            is UiState.Loading -> LoadingBox(Modifier.padding(padding))
            is UiState.Error -> ErrorBox(state.message, onRetry = { viewModel.loadDetail(tugasId) }, modifier = Modifier.padding(padding))
            is UiState.Success -> {
                val tugas = state.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TugasHeader(tugas)
                    if (isDosen) {
                        DosenPengumpulanSection(tugas, viewModel)
                    } else {
                        MahasiswaPengumpulanSection(tugas, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun TugasHeader(tugas: TugasDetailDto) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(tugas.judul, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = SimuaNavy)
            Text("${tugas.kelas.kode} • ${tugas.kelas.nama}", fontSize = 12.sp, color = Color.Gray)
            tugas.deadline?.let {
                Spacer(Modifier.height(8.dp))
                Text("Tenggat: ${formatTanggal(it)}", fontSize = 13.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Medium)
            }
            tugas.deskripsi?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, fontSize = 14.sp, color = Color.DarkGray)
            }
            if (tugas.fileUrl != null) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { openUrl(context, tugas.fileUrl) }) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Lampiran Tugas")
                }
            }
        }
    }
}

// ---- Sisi Mahasiswa: kumpul tugas ----

@Composable
private fun MahasiswaPengumpulanSection(tugas: TugasDetailDto, viewModel: TugasViewModel) {
    val context = LocalContext.current
    val pengumpulan = tugas.pengumpulanSaya
    var jawabanTeks by remember(tugas.id) { mutableStateOf(pengumpulan?.jawabanTeks ?: "") }
    var pickedUri by remember(tugas.id) { mutableStateOf<Uri?>(null) }
    var pickedName by remember(tugas.id) { mutableStateOf<String?>(null) }
    var submitted by remember(tugas.id) { mutableStateOf(false) }

    val action = viewModel.actionState
    val isLoading = action is ActionState.Loading

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) { pickedUri = uri; pickedName = uriDisplayName(context, uri) }
    }

    LaunchedEffect(action, submitted) {
        if (submitted && action is ActionState.Success) {
            android.widget.Toast.makeText(context, "Tugas dikumpulkan.", android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetAction()
            submitted = false
            pickedUri = null
            pickedName = null
        }
    }

    Text("Pengumpulan Saya", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SimuaNavy)

    if (pengumpulan != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Dikumpulkan: ${formatTanggal(pengumpulan.submittedAt)}", fontSize = 12.sp, color = Color(0xFF2E7D32))
                pengumpulan.nilai?.let {
                    Text("Nilai: $it", fontWeight = FontWeight.Bold, color = SimuaNavy, fontSize = 16.sp)
                }
                if (pengumpulan.fileUrl != null) {
                    TextButton(onClick = { openUrl(context, pengumpulan.fileUrl) }, contentPadding = PaddingValues(0.dp)) {
                        Text("Lihat berkas yang dikumpulkan")
                    }
                }
            }
        }
    }

    // Form kumpul/kumpul ulang
    Text(
        if (pengumpulan != null) "Perbarui Pengumpulan" else "Kumpulkan Tugas",
        fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.DarkGray
    )
    OutlinedTextField(
        value = jawabanTeks,
        onValueChange = { jawabanTeks = it },
        label = { Text("Jawaban teks (opsional)") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3,
        shape = RoundedCornerShape(12.dp)
    )
    OutlinedButton(onClick = { picker.launch("*/*") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Icon(if (pickedUri != null) Icons.Default.Check else Icons.Default.AttachFile, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(pickedName ?: "Lampirkan berkas (opsional)")
    }
    if (action is ActionState.Error) {
        Text(action.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
    }
    Button(
        onClick = {
            if (jawabanTeks.isBlank() && pickedUri == null) {
                android.widget.Toast.makeText(context, "Isi jawaban teks atau lampirkan berkas.", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                val uri = pickedUri
                submitted = true
                viewModel.submitPengumpulan(
                    tugasId = tugas.id,
                    jawabanTeks = jawabanTeks,
                    fileBytes = uri?.let { uriBytes(context, it) },
                    fileName = pickedName,
                    mimeType = uri?.let { uriMime(context, it) }
                )
            }
        },
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SimuaNavy)
    ) {
        if (isLoading) CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
        else Text(if (pengumpulan != null) "Perbarui" else "Kumpulkan", fontWeight = FontWeight.Bold)
    }
}

// ---- Sisi Dosen: daftar pengumpulan + beri nilai ----

@Composable
private fun DosenPengumpulanSection(tugas: TugasDetailDto, viewModel: TugasViewModel) {
    val context = LocalContext.current
    var gradeTarget by remember { mutableStateOf<PengumpulanMahasiswaDto?>(null) }
    var gradeSubmitted by remember { mutableStateOf(false) }
    val action = viewModel.actionState

    LaunchedEffect(action, gradeSubmitted) {
        if (gradeSubmitted && action is ActionState.Success) {
            android.widget.Toast.makeText(context, "Nilai tersimpan.", android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetAction()
            gradeSubmitted = false
        }
    }

    Text("Pengumpulan Mahasiswa", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SimuaNavy)

    val list = tugas.pengumpulan.orEmpty()
    if (list.isEmpty()) {
        Text("Belum ada peserta.", color = Color.LightGray, fontSize = 13.sp)
    } else {
        list.forEach { p ->
            PengumpulanRow(p, onGrade = { gradeTarget = p }, onOpenFile = { openUrl(context, p.fileUrl) })
        }
    }

    if (gradeTarget != null) {
        GradeDialog(
            pengumpulan = gradeTarget!!,
            onDismiss = { gradeTarget = null },
            onConfirm = { nilai ->
                gradeTarget?.pengumpulanId?.let { pid ->
                    gradeSubmitted = true
                    viewModel.grade(pid, nilai, tugas.id)
                }
                gradeTarget = null
            }
        )
    }
}

@Composable
private fun PengumpulanRow(p: PengumpulanMahasiswaDto, onGrade: () -> Unit, onOpenFile: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(p.nama, fontWeight = FontWeight.Bold, color = SimuaNavy)
                    p.nim?.let { Text("NIM: $it", fontSize = 12.sp, color = Color.Gray) }
                }
                if (p.sudahMengumpulkan) {
                    StatusChip(p.nilai?.let { "Nilai: $it" } ?: "Belum dinilai",
                        if (p.nilai != null) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                        if (p.nilai != null) Color(0xFF2E7D32) else Color(0xFFEF6C00))
                } else {
                    StatusChip("Belum kumpul", Color(0xFFFFEBEE), Color(0xFFC62828))
                }
            }
            if (p.sudahMengumpulkan) {
                p.jawabanTeks?.takeIf { it.isNotBlank() }?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(it, fontSize = 13.sp, color = Color.DarkGray, maxLines = 3)
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (p.fileUrl != null) {
                        OutlinedButton(onClick = onOpenFile, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                            Text("Berkas", fontSize = 12.sp)
                        }
                    }
                    Button(
                        onClick = onGrade,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SimuaNavy)
                    ) {
                        Text(if (p.nilai != null) "Ubah Nilai" else "Beri Nilai", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun GradeDialog(
    pengumpulan: PengumpulanMahasiswaDto,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var nilaiText by remember { mutableStateOf(pengumpulan.nilai?.toString() ?: "") }
    val nilai = nilaiText.toIntOrNull()
    val valid = nilai != null && nilai in 0..100

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Beri Nilai — ${pengumpulan.nama}") },
        text = {
            OutlinedTextField(
                value = nilaiText,
                onValueChange = { nilaiText = it.filter { c -> c.isDigit() }.take(3) },
                label = { Text("Nilai (0–100)") },
                singleLine = true,
                isError = nilaiText.isNotEmpty() && !valid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            TextButton(enabled = valid, onClick = { onConfirm(nilai!!) }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

// ============================ Buat Tugas (dosen) ============================

@Composable
fun TugasCreateScreen(
    kelasId: Int,
    onBack: () -> Unit,
    onCreated: () -> Unit,
    viewModel: TugasViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var pickedName by remember { mutableStateOf<String?>(null) }
    var submitted by remember { mutableStateOf(false) }

    val action = viewModel.actionState
    val isLoading = action is ActionState.Loading

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) { pickedUri = uri; pickedName = uriDisplayName(context, uri) }
    }

    LaunchedEffect(action, submitted) {
        if (submitted && action is ActionState.Success) {
            android.widget.Toast.makeText(context, "Tugas dibuat.", android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetAction()
            onCreated()
        }
    }

    BackScaffold(title = "Buat Tugas", onBack = onBack) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = judul, onValueChange = { judul = it },
                label = { Text("Judul tugas") }, singleLine = true,
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = deskripsi, onValueChange = { deskripsi = it },
                label = { Text("Deskripsi (opsional)") },
                modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = deadline, onValueChange = { deadline = it },
                label = { Text("Tenggat (opsional)") },
                placeholder = { Text("YYYY-MM-DD HH:MM") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            )
            OutlinedButton(onClick = { picker.launch("application/pdf") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Icon(if (pickedUri != null) Icons.Default.Check else Icons.Default.AttachFile, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(pickedName ?: "Lampiran PDF (opsional)")
            }
            if (action is ActionState.Error) {
                Text(action.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    if (judul.isBlank()) {
                        android.widget.Toast.makeText(context, "Judul wajib diisi.", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        val uri = pickedUri
                        submitted = true
                        viewModel.createTugas(
                            kelasId = kelasId,
                            judul = judul.trim(),
                            deskripsi = deskripsi,
                            deadline = deadline,
                            fileBytes = uri?.let { uriBytes(context, it) },
                            fileName = pickedName,
                            mimeType = uri?.let { uriMime(context, it) }
                        )
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SimuaNavy)
            ) {
                if (isLoading) CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("Buat Tugas", fontWeight = FontWeight.Bold)
            }
        }
    }
}
