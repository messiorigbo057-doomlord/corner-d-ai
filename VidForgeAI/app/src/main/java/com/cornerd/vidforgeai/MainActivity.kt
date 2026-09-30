package com.cornerd.vidforgeai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { VidForgeTheme { VidForgeApp() } }
    }
}

@Composable
fun VidForgeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF08080C),
            surface = Color(0xFF12121A),
            primary = Color(0xFF9B7BFF),
            secondary = Color(0xFF38D9C5),
            onBackground = Color.White,
            onSurface = Color.White
        ),
        content = content
    )
}

@Composable
fun VidForgeApp(vm: VideoViewModel = viewModel()) {
    var tab by remember { mutableIntStateOf(0) }
    Scaffold(
        containerColor = Color(0xFF08080C),
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF0D0D13)) {
                NavigationBarItem(selected = tab == 0, onClick = { tab = 0 },
                    icon = { Icon(Icons.Default.AutoAwesome, null) }, label = { Text("Create") })
                NavigationBarItem(selected = tab == 1, onClick = { tab = 1 },
                    icon = { Icon(Icons.Default.VideoLibrary, null) }, label = { Text("Projects") })
                NavigationBarItem(selected = tab == 2, onClick = { tab = 2 },
                    icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") })
            }
        }
    ) { pad ->
        when (tab) {
            0 -> CreateScreen(Modifier.padding(pad), vm)
            1 -> ProjectsScreen(Modifier.padding(pad), vm)
            else -> SettingsScreen(Modifier.padding(pad))
        }
    }
}

@Composable
fun CreateScreen(modifier: Modifier, vm: VideoViewModel) {
    var prompt by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("5s") }
    var ratio by remember { mutableStateOf("16:9") }

    LazyColumn(
        modifier.fillMaxSize().padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 28.dp, bottom = 30.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF180D29)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("D", color = Color(0xFFB86CFF), fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleLarge)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("CORNER-D", style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold)
                    Text("AI VIDEO STUDIO", color = Color(0xFFB86CFF),
                        style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(26.dp))
        }
        item {
            Box(
                Modifier.fillMaxWidth().height(150.dp)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF24194A), Color(0xFF102A35))),
                        RoundedCornerShape(24.dp)
                    ).padding(20.dp)
            ) {
                Column(Modifier.align(Alignment.CenterStart)) {
                    Text("Create beyond the ordinary", style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold)
                    Text("Your vision. Our AI. Your story.",
                        color = Color.LightGray)
                }
            }
            Spacer(Modifier.height(22.dp))
        }
        item {
            Text("Prompt", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = prompt, onValueChange = { prompt = it },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                placeholder = { Text("A futuristic city at night, cinematic camera movement...") },
                shape = RoundedCornerShape(18.dp)
            )
            Spacer(Modifier.height(16.dp))
        }
        item {
            Text("Duration", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(vertical = 10.dp)) {
                listOf("5s", "10s").forEach {
                    FilterChip(selected = duration == it, onClick = { duration = it }, label = { Text(it) })
                }
            }
        }
        item {
            Text("Aspect ratio", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(vertical = 10.dp)) {
                listOf("16:9", "9:16", "1:1").forEach {
                    FilterChip(selected = ratio == it, onClick = { ratio = it }, label = { Text(it) })
                }
            }
        }
        item {
            Button(
                onClick = { vm.generate(prompt, duration, ratio) },
                enabled = prompt.isNotBlank() && !vm.loading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                if (vm.loading) CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                else {
                    Icon(Icons.Default.AutoAwesome, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Generate video")
                }
            }
            vm.error?.let {
                Spacer(Modifier.height(10.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            vm.lastUrl?.let {
                Spacer(Modifier.height(14.dp))
                Text("Generation submitted successfully.", color = Color(0xFF72E6D5))
                Text(it, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ProjectsScreen(modifier: Modifier, vm: VideoViewModel) {
    Column(modifier.fillMaxSize().padding(20.dp)) {
        Text("CORNER-D Projects", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))
        if (vm.history.isEmpty()) {
            Text("Your CORNER-D videos will appear here.", color = Color.Gray)
        } else {
            vm.history.forEach { item ->
                Card(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(item.prompt, fontWeight = FontWeight.SemiBold)
                        Text("${item.duration} • ${item.ratio}", color = Color.Gray)
                        item.url?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(modifier: Modifier) {
    Column(modifier.fillMaxSize().padding(20.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))
        Text("Backend URL", fontWeight = FontWeight.SemiBold)
        Text("Set API_BASE_URL in VideoApi.kt before building.", color = Color.Gray)
        Spacer(Modifier.height(18.dp))
        Text("Security", fontWeight = FontWeight.SemiBold)
        Text("Keep your AI-provider token on the server. Never ship it inside the APK.",
            color = Color.Gray)
    }
}
