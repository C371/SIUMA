package com.example.siuma.data.remote

/**
 * Konfigurasi jaringan terpusat.
 *
 * BASE_URL memakai host khusus emulator Android `10.0.2.2` yang menunjuk ke
 * `localhost` mesin dev. Samakan PORT dengan `php artisan serve` yang berjalan
 * (default 8000). Untuk perangkat fisik, ganti host ke IP LAN mesin dev dan
 * sesuaikan APP_URL di Simua-web/.env agar `file_url` (unduh materi/tugas) benar.
 */
object ApiConfig {
    const val BASE_URL = "http://10.0.2.2:8000/api/"
}
