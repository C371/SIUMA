package com.example.siuma.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File

// --- Data Models ---
data class MateriItem(val name: String, val date: String, val size: String, val localPath: String? = null)
data class Course(val id: String, val title: String, val subtitle: String, val room: String)

// --- ViewModel for Persistence ---
class DosenViewModel : ViewModel() {
    // Map course ID to its list of materials
    var courseMaterials = mutableStateMapOf<String, List<MateriItem>>()

    init {
        // Initial data
        val initialMateri = listOf(
            MateriItem("Modul 1 - Pengenalan Android.pdf", "12 Okt 2023", "1.2 MB"),
            MateriItem("Modul 2 - Compose UI.pdf", "19 Okt 2023", "2.5 MB"),
            MateriItem("Tugas 1 - Layouting.pdf", "20 Okt 2023", "800 KB")
        )
        // Populate for some courses
        courseMaterials["1"] = initialMateri
        courseMaterials["2"] = listOf(MateriItem("Silabus AI.pdf", "01 Sep 2023", "500 KB"))
    }

    fun addMateri(courseId: String, materi: MateriItem) {
        val currentList = courseMaterials[courseId] ?: emptyList()
        courseMaterials[courseId] = currentList + materi
    }
}

@Composable
fun KelasScreen(onBack: () -> Unit) {
    val viewModel: DosenViewModel = viewModel()
    var selectedCourse by remember { mutableStateOf<Course?>(null) }

    val courses = listOf(
        Course("1", "Pengembangan Aplikasi Bergerak", "Informatika - Semester 5", "B4.11"),
        Course("2", "Kecerdasan Buatan", "Informatika - Semester 5", "B4.04"),
        Course("3", "Basis Data", "Informatika - Semester 3", "Lab Komputer 1"),
        Course("4", "Sistem Operasi", "Informatika - Semester 3", "B4.05"),
        Course("5", "Teori Bahasa & Automata", "Informatika - Semester 5", "B4.11")
    )

    if (selectedCourse == null) {
        // List of Courses View
        BaseDosenScreen(title = "Daftar Kelas", onBack = onBack) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Text(
                        "Pilih mata kuliah untuk mengelola materi.",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(courses) { course ->
                    CourseItemCard(course) {
                        selectedCourse = course
                    }
                }
            }
        }
    } else {
        // Course Detail & Material View
        MateriDetailScreen(
            course = selectedCourse!!,
            materials = viewModel.courseMaterials[selectedCourse!!.id] ?: emptyList(),
            onBack = { selectedCourse = null },
            onUpload = { newMateri ->
                viewModel.addMateri(selectedCourse!!.id, newMateri)
            }
        )
    }
}

@Composable
fun CourseItemCard(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(course.title, fontWeight = FontWeight.Bold, color = Color(0xFF0B194C), fontSize = 16.sp)
            Text(course.subtitle, fontSize = 12.sp, color = Color.Gray)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(course.room, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun MateriDetailScreen(
    course: Course,
    materials: List<MateriItem>,
    onBack: () -> Unit,
    onUpload: (MateriItem) -> Unit
) {
    val context = LocalContext.current
    val pickPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val path = saveFileLocally(context, it)
            if (path != null) {
                val newMateri = MateriItem(
                    name = "Materi_${System.currentTimeMillis() % 10000}.pdf",
                    date = "Hari ini",
                    size = "Baru",
                    localPath = path
                )
                onUpload(newMateri)
            }
        }
    }

    BaseDosenScreen(title = course.title, onBack = onBack) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                course.subtitle,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = { pickPdfLauncher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B194C))
            ) {
                Icon(Icons.Default.UploadFile, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Unggah Materi Baru (PDF)")
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Materi Terunggah",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0B194C),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (materials.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada materi.", color = Color.LightGray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(materials) { materi ->
                        MateriCard(materi) {
                            openPdf(context, materi)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MateriCard(materi: MateriItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = materi.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0B194C)
                )
                Row {
                    Text(text = materi.date, fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "•", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = materi.size, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun MahasiswaScreen(onBack: () -> Unit) {
    BaseDosenScreen(title = "Daftar Mahasiswa", onBack = onBack) {
        Text("Daftar mahasiswa bimbingan atau mahasiswa di kelas.")
    }
}

@Composable
fun PenelitianScreen(onBack: () -> Unit) {
    BaseDosenScreen(title = "Penelitian", onBack = onBack) {
        Text("Halaman Penelitian dan Pengabdian.")
    }
}

@Composable
fun BaseDosenScreen(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B194C)
        )
        Spacer(modifier = Modifier.height(24.dp))
        content()
    }
}

private fun saveFileLocally(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "materi_${System.currentTimeMillis()}.pdf"
        val file = File(context.filesDir, fileName)
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}

private fun openPdf(context: Context, materi: MateriItem) {
    if (materi.localPath == null) {
        // For dummy data, we just show a toast or do nothing since the file doesn't exist on disk
        android.widget.Toast.makeText(context, "Simulasi: Membuka ${materi.name}", android.widget.Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val file = File(materi.localPath)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Buka dengan"))
    } catch (e: Exception) {
        android.widget.Toast.makeText(context, "Tidak ada aplikasi untuk membuka PDF", android.widget.Toast.LENGTH_SHORT).show()
    }
}
