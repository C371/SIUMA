package com.example.siuma.ui.screens

import androidx.lifecycle.ViewModel

data class KRSItem(val code: String, val name: String, val sks: Int, val dosen: String, var isSelected: Boolean = false)

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

data class PresensiItem(val subject: String, val date: String, val time: String, val status: String)

class PresensiViewModel : ViewModel() {
    val historyPresensi = listOf(
        PresensiItem("Pengembangan Aplikasi Bergerak", "23 Okt 2023", "08:00", "Hadir"),
        PresensiItem("Kecerdasan Buatan", "24 Okt 2023", "13:00", "Hadir"),
        PresensiItem("Basis Data", "25 Okt 2023", "10:00", "Izin"),
        PresensiItem("Sistem Operasi", "26 Okt 2023", "07:30", "Hadir"),
        PresensiItem("Teori Bahasa & Automata", "27 Okt 2023", "13:00", "Hadir")
    )
}
