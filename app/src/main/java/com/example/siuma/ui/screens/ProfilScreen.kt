package com.example.siuma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.siuma.data.local.UserSession
import com.example.siuma.ui.ActionState

@Composable
fun ProfilScreen(
    session: UserSession?,
    onLogout: () -> Unit,
    viewModel: ProfilViewModel = hiltViewModel()
) {
    val isDosen = session?.isDosen ?: false
    val nama = session?.name ?: "Pengguna"
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SimuaBg)
            .verticalScroll(rememberScrollState())
    ) {
        // Header: avatar inisial + nama + chip peran (F-PROF-01)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SimuaNavy)
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(SimuaGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    nama.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = SimuaNavy
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(nama, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(8.dp))
            Surface(color = SimuaGold, shape = RoundedCornerShape(20.dp)) {
                Text(
                    if (isDosen) "Dosen" else "Mahasiswa",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    color = SimuaNavy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Info sesuai peran (F-PROF-02)
        Column(modifier = Modifier.padding(16.dp)) {
            val items = buildList {
                session?.email?.takeIf { it.isNotBlank() }?.let { add("Email" to it) }
                if (isDosen) {
                    session?.nidn?.let { add("NIDN" to it) }
                    session?.keahlian?.let { add("Keahlian" to it) }
                } else {
                    session?.nim?.let { add("NIM" to it) }
                    session?.prodi?.let { add("Program Studi" to it) }
                }
            }
            ProfileInfoCard(title = "Informasi", items = items)

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = { showPasswordDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SimuaNavy)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Ubah Kata Sandi")
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Keluar")
            }
        }
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(viewModel = viewModel, onDismiss = { showPasswordDialog = false })
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Keluar") },
            text = { Text("Apakah Anda yakin ingin keluar dari akun?") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) { Text("Ya, Keluar") }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Batal") } }
        )
    }
}

@Composable
private fun ChangePasswordDialog(viewModel: ProfilViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var current by remember { mutableStateOf("") }
    var baru by remember { mutableStateOf("") }
    var konfirmasi by remember { mutableStateOf("") }
    val state = viewModel.passwordState
    val isLoading = state is ActionState.Loading

    LaunchedEffect(state) {
        if (state is ActionState.Success) {
            android.widget.Toast.makeText(context, "Kata sandi diperbarui.", android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetPasswordState()
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Ubah Kata Sandi") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = current, onValueChange = { current = it },
                    label = { Text("Kata sandi saat ini") }, singleLine = true,
                    visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = baru, onValueChange = { baru = it },
                    label = { Text("Kata sandi baru") }, singleLine = true,
                    visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = konfirmasi, onValueChange = { konfirmasi = it },
                    label = { Text("Konfirmasi kata sandi") }, singleLine = true,
                    visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth()
                )
                if (state is ActionState.Error) {
                    Text(state.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isLoading,
                onClick = {
                    when {
                        current.isBlank() || baru.isBlank() -> android.widget.Toast.makeText(context, "Lengkapi semua kolom.", android.widget.Toast.LENGTH_SHORT).show()
                        baru != konfirmasi -> android.widget.Toast.makeText(context, "Konfirmasi tidak cocok.", android.widget.Toast.LENGTH_SHORT).show()
                        else -> viewModel.changePassword(current, baru, konfirmasi)
                    }
                }
            ) { Text(if (isLoading) "Menyimpan..." else "Simpan") }
        },
        dismissButton = { TextButton(enabled = !isLoading, onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun ProfileInfoCard(title: String, items: List<Pair<String, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SimuaNavy)
            Spacer(Modifier.height(12.dp))
            if (items.isEmpty()) {
                Text("-", color = Color.Gray, fontSize = 14.sp)
            }
            items.forEachIndexed { index, (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, color = Color.Gray, fontSize = 14.sp)
                    Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.Black)
                }
                if (index < items.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFF5F5F5))
                }
            }
        }
    }
}
