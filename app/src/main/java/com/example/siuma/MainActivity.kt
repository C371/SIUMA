package com.example.siuma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.siuma.ui.theme.SIUMATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SIUMATheme {
                var showPlaceholder by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (showPlaceholder) {
                        PlaceholderScreen(
                            onBack = { showPlaceholder = false },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        LoginScreen(
                            onLoginClick = { showPlaceholder = true },
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
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_uns_biru),
            contentDescription = "Logo UNS",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // App Name
        Text(
            text = "SIMUA",
            fontSize = 58.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // Subtitle
        Text(
            text = "SISTEM INFORMASI MAHASISWA URUSAN AKADEMIS\nUNIVERSITAS SEBELAS MARET",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(64.dp))

        // Sign in method text
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

        // SSO UNS Button
        LoginButton(
            text = "SSO UNS",
            iconRes = R.drawable.logo_uns_only_black_sso,
            onClick = onLoginClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Google Button
        LoginButton(
            text = "Google",
            iconRes = R.drawable.google_logo,
            onClick = onLoginClick
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Footer
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
fun PlaceholderScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Halaman Kedua (Placeholder)", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("Kembali")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    SIUMATheme {
        LoginScreen(onLoginClick = {})
    }
}
