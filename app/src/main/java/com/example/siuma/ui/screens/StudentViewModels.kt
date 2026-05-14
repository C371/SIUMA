package com.example.siuma.ui.screens

import androidx.lifecycle.ViewModel

data class KRSItem(val code: String, val name: String, val sks: Int, val dosen: String, var isSelected: Boolean = false)

// KHSItem removed to avoid conflict with KHSScreen.kt

class KRSViewModel : ViewModel() {
    val availableCourses = listOf(
        KRSItem("IF101", "Pemrograman Dasar", 3, "Dr. Eng. Irwan Prasetyo"),
        KRSItem("IF102", "Matematika Diskrit", 3, "Dra. Siti Aminah, M.Si"),
        KRSItem("IF201", "Struktur Data", 4, "Budi Raharjo, S.T., M.Kom"),
        KRSItem("IF202", "Organisasi Komputer", 3, "Agus Setiawan, M.T."),
        KRSItem("IF301", "Pemrograman Berorientasi Objek", 4, "Dr. Akhmad Fauzi"),
        KRSItem("IF302", "Basis Data", 4, "Herry Prasetyo, S.Kom, M.Sc.Eng."),
        KRSItem("IF401", "Rekayasa Perangkat Lunak", 3, "Indra Gunawan, S.T., M.Cs."),
        KRSItem("IF501", "Pengembangan Aplikasi Bergerak", 3, "Arif Rohmadi, S.Kom., M.Cs.")
    )
}

data class PresensiItem(
    val subject: String,
    val date: String,
    val time: String,
    val status: String, // Hadir, Izin, Alpha
    val room: String = "N/A",
    val materi: String = ""
)

class AcademicViewModel : ViewModel() {
    // ... (kode lainnya tetap sama)
    val daftarJadwalMahasiswa = androidx.compose.runtime.mutableStateListOf(
        JadwalItem(1, "Pengembangan Aplikasi Bergerak", "12013220427 - Arif Rohmadi S.Kom., M.Cs", "B4.11", "Senin", "08:00 - 10:30", true),
        JadwalItem(2, "Kecerdasan Buatan", "12013220428 - Akhmad Syaifuddin S.Si., M.Cs.", "B4.04", "Selasa", "13:00 - 15:30", false),
        JadwalItem(3, "Basis Data", "12013220429 - Bambang Widoyono S.T., M.T.I.", "Lab Komputer 1", "Rabu", "10:00 - 12:30", true),
        JadwalItem(4, "Sistem Operasi", "12013220430 - Herdito Ibnu Dewangkoro M.Kom.", "B4.05", "Kamis", "07:30 - 10:00", true),
        JadwalItem(5, "Teori Bahasa & Automata", "12013220431 - HERI PRASETYO S.Kom, M.Sc.Eng.", "B4.11", "Jumat", "13:00 - 14:40", true)
    )

    val historyPresensi = androidx.compose.runtime.mutableStateListOf(
        PresensiItem("Pengembangan Aplikasi Bergerak", "23 Okt 2023", "08:00", "Hadir", "B4.11"),
        PresensiItem("Kecerdasan Buatan", "24 Okt 2023", "13:00", "Hadir", "B4.04"),
        PresensiItem("Basis Data", "25 Okt 2023", "10:00", "Izin", "Lab Komputer 1"),
        PresensiItem("Sistem Operasi", "26 Okt 2023", "07:30", "Hadir", "B4.05"),
        PresensiItem("Teori Bahasa & Automata", "27 Okt 2023", "13:00", "Hadir", "B4.11")
    )

    val daftarJadwalDosen = androidx.compose.runtime.mutableStateListOf(
        JadwalItem(101, "Pengembangan Aplikasi Bergerak", "Informatika - Kelas A", "B4.11", "Senin", "08:00 - 10:30", true, true),
        JadwalItem(102, "Kecerdasan Buatan", "Informatika - Kelas B", "B4.04", "Selasa", "13:00 - 15:30", false, true),
        JadwalItem(103, "Pengembangan Aplikasi Bergerak", "Informatika - Kelas C", "B4.11", "Rabu", "13:00 - 15:30", true, true),
        JadwalItem(104, "Kecerdasan Buatan", "Informatika - Kelas A", "B4.05", "Kamis", "10:00 - 12:30", true, true)
    )

    val historyPresensiDosen = androidx.compose.runtime.mutableStateListOf(
        PresensiItem("Pengembangan Aplikasi Bergerak", "23 Okt 2023", "08:00", "Hadir", "B4.11", "Pengenalan Android Jetpack"),
        PresensiItem("Pengembangan Aplikasi Bergerak", "25 Okt 2023", "13:00", "Hadir", "B4.11", "MVVM Pattern & Flow"),
        PresensiItem("Kecerdasan Buatan", "26 Okt 2023", "10:00", "Hadir", "B4.05", "Fuzzy Logic Introduction")
    )

    fun markPresensiDone(id: Int, isDosen: Boolean = false, materi: String = "") {
        val list = if (isDosen) daftarJadwalDosen else daftarJadwalMahasiswa
        val history = if (isDosen) historyPresensiDosen else historyPresensi
        
        val index = list.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = list[index]
            list[index] = item.copy(isPresensiDone = true)
            
            // Add to history
            history.add(0, PresensiItem(
                subject = item.title,
                date = "Hari ini",
                time = item.time.substringBefore(" -"),
                status = "Hadir",
                room = item.room,
                materi = if (isDosen) materi else ""
            ))
        }
    }

    fun getPresensiStats(isDosen: Boolean): Map<String, Int> {
        val history = if (isDosen) historyPresensiDosen else historyPresensi
        return history.groupBy { it.status }.mapValues { it.value.size }
    }
}
