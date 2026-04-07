package com.example.siuma

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.siuma.ui.theme.SIUMATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SIUMATheme {
                var showHomeScreen by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (showHomeScreen) {
                        HomeScreen(
                            onLogout = { showHomeScreen = false },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        LoginScreen(
                            onLoginClick = { showHomeScreen = true },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logosimuawelcome),
            contentDescription = "Logo UNS",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SIMUA",
            fontSize = 58.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "SISTEM INFORMASI MAHASISWA URUSAN AKADEMIS\nUNIVERSITAS SEBELAS MARET",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = "Choose your sign in method",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Text(
            text = "Select one of the buttons below to sign in",
            fontSize = 12.sp,
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        LoginButton(
            text = "SSO UNS",
            iconRes = R.drawable.logo_uns_only_black_sso,
            onClick = onLoginClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginButton(
            text = "Google",
            iconRes = R.drawable.google_logo,
            onClick = onLoginClick
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Dengan login dan menggunakan aplikasi, Anda menyetujui kebijakan privasi SIMUA",
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
    }
}

@Composable
fun LoginButton(text: String, iconRes: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun HomeScreen(onLogout: () -> Unit, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0B194C))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
                modifier = Modifier.size(50.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logosimua),
                    contentDescription = "Logo",
                    modifier = Modifier.padding(6.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = "SIMUA",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Universitas Sebelas Maret",
                    color = Color(0xFFFFC107),
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Keluar", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp)
        ) {
            Text(
                text = "Selamat Datang di SIMUA",
                color = Color(0xFF0B194C),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Gerbang Edukasi dan Teknologi Andalan Universitas Sebelas Maret",
                color = Color(0xFF757575),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        val context = LocalContext.current

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B194C)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 32.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Jelajahi",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Temukan program impian dan informasi pendaftaran UNS di sini.",
                    color = Color(0xFFCCCCCC),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                ExploreButton(
                    text = "Fakultas dari UNS",
                    iconRes = R.drawable.simuafak,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    val intent = Intent(Intent.ACTION_VIEW, "https://uns.ac.id/id/informasi-akademik/fakultas".toUri())
                    context.startActivity(intent)
                }

                ExploreButton(
                    text = "Program Studi",
                    iconRes = R.drawable.simuaprodi,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    val intent = Intent(Intent.ACTION_VIEW, "https://uns.ac.id/id/informasi-akademik/daftar-program-studi".toUri())
                    context.startActivity(intent)
                }

                ExploreButton(
                    text = "Pendaftaran",
                    iconRes = R.drawable.simuapend,
                    modifier = Modifier.padding(bottom = 0.dp)
                ) {
                    val intent = Intent(Intent.ACTION_VIEW, "https://spmb.uns.ac.id".toUri())
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun ExploreButton(
    text: String,
    iconRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF0B194C)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}