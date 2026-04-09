package com.example.siuma

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.siuma.databinding.ActivitySetelahditekanBinding

class setelahditekan : AppCompatActivity() {

    private lateinit var binding: ActivitySetelahditekanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetelahditekanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMasuk.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnFakultas.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://uns.ac.id/id/informasi-akademik/fakultas"))
            startActivity(intent)
        }

        binding.btnProdi.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://uns.ac.id/id/informasi-akademik/daftar-program-studi"))
            startActivity(intent)
        }

        binding.btnPendaftaran.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://spmb.uns.ac.id"))
            startActivity(intent)
        }
    }
}