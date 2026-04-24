package com.example.siuma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.siuma.ui.navigation.LocalBackStack
import com.example.siuma.ui.navigation.NavDisplay
import com.example.siuma.ui.navigation.Route
import com.example.siuma.ui.screens.*
import com.example.siuma.ui.theme.SIUMATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val backStack = remember { mutableStateListOf<Route>(Route.Login) }
            
            CompositionLocalProvider(LocalBackStack provides backStack) {
                SIUMATheme {
                    NavDisplay(backStack = backStack) { route ->
                        when (route) {
                            is Route.Login -> LoginScreen()
                            is Route.SSOLogin -> SSOLoginScreen()
                            is Route.GoogleLogin -> GoogleLoginScreen()
                            is Route.Main -> MainScreen(isDosen = false)
                            is Route.MainDosen -> MainScreen(isDosen = true)
                            is Route.Jadwal -> JadwalScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.KRS -> KRSScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.KHS -> KHSScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.Kelas -> KelasScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.Mahasiswa -> MahasiswaScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.Penelitian -> PenelitianScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.Presensi -> PresensiScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.Pembayaran -> PembayaranScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.Detail -> DetailScreen(title = route.title, onBack = { backStack.removeLastOrNull() })
                            else -> DetailScreen(title = "Fitur", onBack = { backStack.removeLastOrNull() })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    val backStack = LocalBackStack.current
    
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
            onClick = { backStack.add(Route.SSOLogin) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginButton(
            text = "Google",
            iconRes = R.drawable.google_logo,
            onClick = { backStack.add(Route.GoogleLogin) }
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
fun SSOLoginScreen() {
    val backStack = LocalBackStack.current
    var nim by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_uns_only_black_sso),
            contentDescription = "SSO UNS",
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Login SSO UNS", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = nim,
            onValueChange = { nim = it },
            label = { Text("NIM / NIP / Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                if (nim.isNotBlank()) {
                    backStack.clear()
                    backStack.add(Route.Main)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Masuk Sebagai Mahasiswa")
        }
        //button untuk login sebagai dosen
        Button(
            onClick = {
                if (nim == "Dosen123") {
                    backStack.clear()
                    backStack.add(Route.MainDosen)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Masuk Sebagai Dosen")
        }
        TextButton(onClick = { backStack.removeLastOrNull() }) {
            Text("Batal")
        }
    }
}

@Composable
fun GoogleLoginScreen() {
    val backStack = LocalBackStack.current
    var email by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.google_logo),
            contentDescription = "Google",
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Login dengan Google", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                //mungkin tambah parameter untuk membedakan jenis email dari dosen/mahasiswa untuk melakukan login
                //cont: student.uns.ac.id untuk mahasiswa, staff.uns.ac.id untuk dosen
                if (email.isNotBlank()) {
                    backStack.clear()
                    backStack.add(Route.Main)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Lanjutkan")
        }
        TextButton(onClick = { backStack.removeLastOrNull() }) {
            Text("Batal")
        }
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
