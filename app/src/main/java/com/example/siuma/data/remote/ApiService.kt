package com.example.siuma.data.remote

import com.example.siuma.data.remote.dto.AkademikDto
import com.example.siuma.data.remote.dto.DataResponse
import com.example.siuma.data.remote.dto.KelasDetailDto
import com.example.siuma.data.remote.dto.KelasDto
import com.example.siuma.data.remote.dto.KerjakanRequest
import com.example.siuma.data.remote.dto.KerjakanResponse
import com.example.siuma.data.remote.dto.KuisCreateRequest
import com.example.siuma.data.remote.dto.KuisDetailDto
import com.example.siuma.data.remote.dto.KuisDto
import com.example.siuma.data.remote.dto.SoalCreateRequest
import com.example.siuma.data.remote.dto.LoginRequest
import com.example.siuma.data.remote.dto.LoginResponse
import com.example.siuma.data.remote.dto.MahasiswaDto
import com.example.siuma.data.remote.dto.MateriUploadResponse
import com.example.siuma.data.remote.dto.MeResponse
import com.example.siuma.data.remote.dto.MessageResponse
import com.example.siuma.data.remote.dto.NilaiRequest
import com.example.siuma.data.remote.dto.PasswordChangeRequest
import com.example.siuma.data.remote.dto.PresensiDosenDto
import com.example.siuma.data.remote.dto.PresensiMahasiswaDto
import com.example.siuma.data.remote.dto.PresensiSaveRequest
import com.example.siuma.data.remote.dto.RekapResponseDto
import com.example.siuma.data.remote.dto.ProfileUpdateRequest
import com.example.siuma.data.remote.dto.ProfileUpdateResponse
import com.example.siuma.data.remote.dto.TugasDetailDto
import com.example.siuma.data.remote.dto.TugasDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Kontrak REST API SIMUA (lihat srs.md "Lampiran A").
 * Semua endpoint—kecuali login—membutuhkan header Bearer yang disisipkan
 * otomatis oleh [AuthInterceptor].
 */
interface ApiService {

    // --- Autentikasi & profil ---
    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @GET("me")
    suspend fun me(): Response<MeResponse>

    @POST("logout")
    suspend fun logout(): Response<MessageResponse>

    @PATCH("profile")
    suspend fun updateProfile(@Body body: ProfileUpdateRequest): Response<ProfileUpdateResponse>

    @PUT("profile/password")
    suspend fun changePassword(@Body body: PasswordChangeRequest): Response<MessageResponse>

    // --- Kelas ---
    @GET("kelas")
    suspend fun getKelas(): Response<DataResponse<List<KelasDto>>>

    @GET("kelas/{id}")
    suspend fun getKelasDetail(@Path("id") id: Int): Response<DataResponse<KelasDetailDto>>

    @GET("kelas/{id}/mahasiswa")
    suspend fun getKelasMahasiswa(@Path("id") id: Int): Response<DataResponse<List<MahasiswaDto>>>

    @Multipart
    @POST("kelas/{id}/materi")
    suspend fun uploadMateri(
        @Path("id") id: Int,
        @Part("judul") judul: RequestBody,
        @Part("keterangan") keterangan: RequestBody?,
        @Part file: MultipartBody.Part
    ): Response<MateriUploadResponse>

    // --- Tugas ---
    @GET("kelas/{id}/tugas")
    suspend fun getTugas(@Path("id") id: Int): Response<DataResponse<List<TugasDto>>>

    @GET("tugas/{id}")
    suspend fun getTugasDetail(@Path("id") id: Int): Response<DataResponse<TugasDetailDto>>

    @Multipart
    @POST("kelas/{id}/tugas")
    suspend fun createTugas(
        @Path("id") id: Int,
        @Part("judul") judul: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody?,
        @Part("deadline") deadline: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Response<MessageResponse>

    @Multipart
    @POST("tugas/{id}/pengumpulan")
    suspend fun submitPengumpulan(
        @Path("id") id: Int,
        @Part("jawaban_teks") jawabanTeks: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Response<MessageResponse>

    @PATCH("pengumpulan/{id}/nilai")
    suspend fun gradePengumpulan(
        @Path("id") id: Int,
        @Body body: NilaiRequest
    ): Response<MessageResponse>

    // --- Kuis ---
    @GET("kelas/{id}/kuis")
    suspend fun getKuis(@Path("id") id: Int): Response<DataResponse<List<KuisDto>>>

    @GET("kuis/{id}")
    suspend fun getKuisDetail(@Path("id") id: Int): Response<DataResponse<KuisDetailDto>>

    @POST("kelas/{id}/kuis")
    suspend fun createKuis(@Path("id") id: Int, @Body body: KuisCreateRequest): Response<MessageResponse>

    @POST("kuis/{id}/soal")
    suspend fun addSoal(@Path("id") id: Int, @Body body: SoalCreateRequest): Response<MessageResponse>

    @POST("kuis/{id}/kerjakan")
    suspend fun kerjakanKuis(@Path("id") id: Int, @Body body: KerjakanRequest): Response<KerjakanResponse>

    // --- Presensi (di dalam kelas) ---
    @GET("kelas/{id}/presensi")
    suspend fun getPresensiDosen(
        @Path("id") id: Int,
        @Query("pertemuan") pertemuan: Int?
    ): Response<DataResponse<PresensiDosenDto>>

    @GET("kelas/{id}/presensi")
    suspend fun getPresensiMahasiswa(@Path("id") id: Int): Response<DataResponse<PresensiMahasiswaDto>>

    @POST("kelas/{id}/presensi")
    suspend fun savePresensi(
        @Path("id") id: Int,
        @Body body: PresensiSaveRequest
    ): Response<MessageResponse>

    // --- Akademik & Rekap ---
    @GET("akademik")
    suspend fun getAkademik(): Response<DataResponse<AkademikDto>>

    @GET("rekap-presensi")
    suspend fun getRekapPresensi(): Response<DataResponse<RekapResponseDto>>
}
