package com.cornerd.vidforgeai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
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

        setContent {
            VidForgeTheme {
                VidForgeApp()
            }
        }
    }
}

@Composable
fun VidForgeTheme(
    content: @Composable () -> Unit
) {
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
fun VidForgeApp(
    vm: VideoViewModel = viewModel()
) {
    var tab by remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        containerColor = Color(0xFF08080C),

        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0D0D13)
            ) {

                NavigationBarItem(
                    selected = tab == 0,
                    onClick = {
                        tab = 0
                    },
                    icon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Create"
                        )
                    },
                    label = {
                        Text("Create")
                    }
                )

                NavigationBarItem(
                    selected = tab == 1,
                    onClick = {
                        tab = 1
                    },
                    icon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Projects"
                        )
                    },
                    label = {
                        Text("Projects")
                    }
                )

                NavigationBarItem(
                    selected = tab == 2,
                    onClick = {
                        tab = 2
                    },
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    label = {
                        Text("Settings")
                    }
                )
            }
        }
    ) { paddingValues ->

        when (tab) {

            0 -> {
                CreateScreen(
                    modifier = Modifier.padding(paddingValues),
                    vm = vm
                )
            }

            1 -> {
                ProjectsScreen(
                    modifier = Modifier.padding(paddingValues),
                    vm = vm
                )
            }

            else -> {
                SettingsScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun CreateScreen(
    modifier: Modifier,
    vm: VideoViewModel
) {

    var prompt by remember {
        mutableStateOf("")
    }

    var duration by remember {
        mutableStateOf("5s")
    }

    var ratio by remember {
        mutableStateOf("16:9")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),

        contentPadding = PaddingValues(
            top = 28.dp,
            bottom = 30.dp
        )
    ) {

        item {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF180D29)
                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "D",
                            color = Color(0xFFB86CFF),
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column {

                    Text(
                        text = "CORNER-D",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "AI VIDEO STUDIO",
                        color = Color(0xFFB86CFF),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(26.dp)
            )
        }

        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF24194A),
                                Color(0xFF102A35)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {

                Column(
                    modifier = Modifier.align(
                        Alignment.CenterStart
                    )
                ) {

                    Text(
                        text = "Create beyond the ordinary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Your vision. Our AI. Your story.",
                        color = Color.LightGray
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(22.dp)
            )
        }

        item {

            Text(
                text = "Prompt",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = prompt,

                onValueChange = {
                    prompt = it
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),

                placeholder = {
                    Text(
                        "A futuristic city at night, cinematic camera movement..."
                    )
                },

                shape = RoundedCornerShape(18.dp)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }

        item {

            Text(
                text = "Duration",
                fontWeight = FontWeight.SemiBold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(
                    vertical = 10.dp
                )
            ) {

                listOf(
                    "5s",
                    "10s"
                ).forEach { option ->

                    FilterChip(
                        selected = duration == option,

                        onClick = {
                            duration = option
                        },

                        label = {
                            Text(option)
                        }
                    )
                }
            }
        }

        item {

            Text(
                text = "Aspect ratio",
                fontWeight = FontWeight.SemiBold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(
                    vertical = 10.dp
                )
            ) {

                listOf(
                    "16:9",
                    "9:16",
                    "1:1"
                ).forEach { option ->

                    FilterChip(
                        selected = ratio == option,

                        onClick = {
                            ratio = option
                        },

                        label = {
                            Text(option)
                        }
                    )
                }
            }
        }

        item {

            Button(
                onClick = {
                    vm.generate(
                        prompt = prompt,
                        duration = duration,
                        ratio = ratio
                    )
                },

                enabled =
                    prompt.isNotBlank() &&
                    !vm.loading,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

                shape = RoundedCornerShape(18.dp)
            ) {

                if (vm.loading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )

                } else {

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "Generate video"
                    )
                }
            }

            vm.error?.let { message ->

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            vm.lastUrl?.let { url ->

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = "Generation completed successfully.",
                    color = Color(0xFF72E6D5)
                )

                Text(
                    text = url,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ProjectsScreen(
    modifier: Modifier,
    vm: VideoViewModel
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "CORNER-D Projects",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        if (vm.history.isEmpty()) {

            Text(
                text = "Your CORNER-D videos will appear here.",
                color = Color.Gray
            )

        } else {

            vm.history.forEach { job ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = job.prompt,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "${job.duration} • ${job.ratio}",
                            color = Color.Gray
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "Status: ${job.status}",
                            color = Color.Gray
                        )

                        job.url?.let { url ->

                            Spacer(
                                modifier = Modifier.height(6.dp)
                            )

                            Text(
                                text = url,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    modifier: Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "Backend URL",
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "CORNER-D AI server is connected through VideoApi.kt.",
            color = Color.Gray
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "Security",
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Keep your AI-provider token on the server. Never ship it inside the APK.",
            color = Color.Gray
        )
    }
}
