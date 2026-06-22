package com.example.siuma.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.siuma.data.remote.ApiResult
import com.example.siuma.data.repository.KelasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MateriViewModel @Inject constructor(
    private val repo: KelasRepository
) : ViewModel() {

    sealed interface UploadState {
        data object Idle : UploadState
        data object Loading : UploadState
        data object Success : UploadState
        data class Error(val message: String) : UploadState
    }

    var uploadState by mutableStateOf<UploadState>(UploadState.Idle)
        private set

    fun upload(
        kelasId: Int,
        judul: String,
        keterangan: String?,
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String
    ) {
        viewModelScope.launch {
            uploadState = UploadState.Loading
            uploadState = when (val res = repo.uploadMateri(kelasId, judul, keterangan, fileBytes, fileName, mimeType)) {
                is ApiResult.Success -> UploadState.Success
                is ApiResult.Error -> UploadState.Error(res.message)
            }
        }
    }

    fun resetError() {
        if (uploadState is UploadState.Error) uploadState = UploadState.Idle
    }

    /** Kembalikan ke Idle (mis. setelah sukses dikonsumsi UI). */
    fun reset() {
        uploadState = UploadState.Idle
    }
}

@Composable
fun MateriUploadScreen(
    kelasId: Int,
    onBack: () -> Unit,
    onUploaded: () -> Unit,
    viewModel: MateriViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var judul by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var pickedName by remember { mutableStateOf<String?>(null) }
    var submitted by remember { mutableStateOf(false) }

    val state = viewModel.uploadState
    val isLoading = state is MateriViewModel.UploadState.Loading

    val pickPdf = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            pickedUri = uri
            pickedName = uriDisplayName(context, uri)
        }
    }

    // Hanya bereaksi pada sukses unggahan yang dilakukan di layar ini.
    LaunchedEffect(state, submitted) {
        if (submitted && state is MateriViewModel.UploadState.Success) {
            android.widget.Toast.makeText(context, "Materi berhasil diunggah.", android.widget.Toast.LENGTH_SHORT).show()
            viewModel.reset()
            onUploaded()
        }
    }

    BackScaffold(title = "Unggah Materi", onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = judul,
                onValueChange = { judul = it; viewModel.resetError() },
                label = { Text("Judul materi") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = keterangan,
                onValueChange = { keterangan = it },
                label = { Text("Keterangan (opsional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedButton(
                onClick = { pickPdf.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    if (pickedUri != null) Icons.Default.Check else Icons.Default.UploadFile,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(pickedName ?: "Pilih berkas PDF")
            }

            if (state is MateriViewModel.UploadState.Error) {
                Text(state.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val uri = pickedUri
                    when {
                        judul.isBlank() -> android.widget.Toast.makeText(context, "Judul wajib diisi.", android.widget.Toast.LENGTH_SHORT).show()
                        uri == null -> android.widget.Toast.makeText(context, "Pilih berkas PDF dulu.", android.widget.Toast.LENGTH_SHORT).show()
                        else -> {
                            val bytes = uriBytes(context, uri)
                            if (bytes == null) {
                                android.widget.Toast.makeText(context, "Gagal membaca berkas.", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                submitted = true
                                viewModel.upload(
                                    kelasId = kelasId,
                                    judul = judul.trim(),
                                    keterangan = keterangan,
                                    fileBytes = bytes,
                                    fileName = pickedName ?: "materi.pdf",
                                    mimeType = uriMime(context, uri)
                                )
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SimuaNavy)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Unggah", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

