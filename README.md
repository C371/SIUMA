# SIMUA — Aplikasi E-Learning (Android)

Aplikasi Android untuk sistem e-learning **SIMUA**. Mahasiswa dapat mengikuti kelas,
mengakses materi, mengumpulkan tugas, mengerjakan kuis, melakukan presensi, dan melihat
data akademik (KRS/KHS). Dosen dapat mengelola kelas, menandai presensi, dan memeriksa
pengumpulan tugas.

Aplikasi ini adalah **klien** yang terhubung ke REST API. Backend (Laravel) berada di
repositori terpisah — lihat [Backend / Server](#backend--server).

> **Aplikasi butuh server berjalan untuk bisa login.** Base URL default menunjuk ke
> server lokal melalui emulator Android (`http://10.0.2.2:8000/api/`). Baca bagian
> [Menjalankan Aplikasi](#menjalankan-aplikasi) agar bisa login & menguji.

---

## Daftar Isi
- [Fitur](#fitur)
- [Teknologi](#teknologi)
- [Backend / Server](#backend--server)
- [Akun Tes](#akun-tes)
- [Menjalankan Aplikasi](#menjalankan-aplikasi)
- [Build APK dari CLI](#build-apk-dari-cli)
- [Struktur Proyek](#struktur-proyek)
- [Catatan Pengumpulan](#catatan-pengumpulan)

---

## Fitur

**Mahasiswa**
- Login (token) & logout
- Beranda ringkasan (tugas & materi terbaru)
- Daftar & detail kelas
- Akses materi pembelajaran
- Pengumpulan tugas
- Mengerjakan kuis (skor otomatis)
- Presensi
- Akademik: KRS & KHS, serta rekap presensi
- Profil

**Dosen**
- Login & logout
- Daftar & detail kelas + daftar mahasiswa
- Unggah materi
- Menandai presensi mahasiswa
- Melihat pengumpulan tugas

---

## Teknologi
- **Bahasa:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Arsitektur:** MVVM (ViewModel + Repository)
- **Dependency Injection:** Hilt
- **Jaringan:** Retrofit + OkHttp (Gson)
- **Penyimpanan token:** DataStore Preferences
- **Gambar:** Coil
- **minSdk** 24 · **targetSdk** 36 · `applicationId` `com.example.siuma`

---

## Backend / Server

Aplikasi ini **tidak berdiri sendiri** — login dan seluruh data berasal dari REST API
backend Laravel:

- **Repo backend:** https://github.com/Watarufa/Simua-web

Backend harus berjalan (lokal) sebelum aplikasi dapat login. Panduan menjalankan backend
ada di README repo tersebut; ringkasannya juga ada di bawah.

---

## Akun Tes

Tersedia data contoh dari seeder backend. Password semua akun: `password`.

| Peran      | Email                       |
|------------|-----------------------------|
| Mahasiswa  | `andi@student.uns.ac.id`    |
| Mahasiswa  | `siti@student.uns.ac.id`    |
| Dosen      | `dosen@uns.ac.id`           |

Akun `andi@student.uns.ac.id` mengambil beberapa kelas dan memiliki tugas/kuis/presensi
sehingga paling lengkap untuk demo.

---

## Menjalankan Aplikasi

Karena aplikasi memerlukan server, jalankan **backend dulu**, baru aplikasi di **emulator**.

### 1. Jalankan backend (repo `Simua-web`)
1. Nyalakan database MySQL/MariaDB (mis. XAMPP).
2. Di folder backend, jalankan migrasi + seeder (sekali): `php artisan migrate:fresh --seed`
3. Jalankan server di port 8000: `php artisan serve --host=127.0.0.1 --port=8000`

> Detail lengkap (env, dependensi) ada di README repo backend.

### 2. Jalankan aplikasi di emulator
1. Buka proyek ini di **Android Studio**.
2. Pastikan `local.properties` berisi lokasi Android SDK, contoh:
   `sdk.dir=C:\\Users\\<user>\\AppData\\Local\\Android\\Sdk`
3. Jalankan pada **emulator Android** (AVD). Base URL default `http://10.0.2.2:8000/api/`
   sudah menunjuk ke `localhost` mesin Anda melalui emulator — **tidak perlu diubah**
   selama backend berjalan di port 8000.
4. Login memakai salah satu [akun tes](#akun-tes).

### Menjalankan di HP fisik (opsional)
`10.0.2.2` hanya berlaku di emulator. Untuk HP fisik:
1. Ubah `BASE_URL` di [`app/src/main/java/com/example/siuma/data/remote/ApiConfig.kt`](app/src/main/java/com/example/siuma/data/remote/ApiConfig.kt)
   menjadi IP LAN mesin server, mis. `http://192.168.x.x:8000/api/`.
2. Set `APP_URL` di `.env` backend ke IP yang sama agar tautan unduhan materi/tugas benar.
3. Pastikan HP dan mesin server berada di jaringan yang sama.

---

## Build APK dari CLI

APK debug:

```bash
./gradlew :app:assembleDebug
```

Hasil: `app/build/outputs/apk/debug/app-debug.apk`

> **Catatan lingkungan (Windows):** jika build CLI gagal pada `JdkImageTransform`,
> arahkan Gradle ke JDK lengkap (JBR Android Studio):
> ```bash
> ./gradlew :app:assembleDebug "-Dorg.gradle.java.home=C:\Program Files\Android\Android Studio\jbr"
> ```
> Build melalui Android Studio tidak terdampak masalah ini.

---

## Struktur Proyek

```
app/src/main/java/com/example/siuma/
├── data/
│   ├── local/        # DataStore (token), penyimpanan lokal
│   ├── remote/       # ApiConfig, Retrofit service, DTO
│   └── repository/   # Repository (jembatan data ↔ UI)
├── di/               # Modul Hilt (dependency injection)
└── ui/
    ├── navigation/   # Navigasi Compose
    ├── screens/      # Layar + ViewModel (Login, Beranda, Kelas, Tugas,
    │                 #   Kuis, Presensi, Akademik, Profil, dll.)
    └── theme/        # Tema Material 3
```

---

## Catatan Pengumpulan

- **Repo aplikasi (ini):** https://github.com/C371/SIUMA
- **Repo backend:** https://github.com/Watarufa/Simua-web
- Login **wajib backend berjalan**. APK memakai base URL emulator (`10.0.2.2`), sehingga
  untuk mencoba login diperlukan backend lokal aktif + emulator (lihat
  [Menjalankan Aplikasi](#menjalankan-aplikasi)). Tanpa backend, layar login tidak dapat
  memuat data.
