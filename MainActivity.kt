package com.nilkanth.ai

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

class MainActivity : ComponentActivity() {

    private var message by mutableStateOf("Namaste! Main Nilkanth AI hoon.")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                NilkanthScreen(
                    message = message,
                    onSpeak = { startVoiceRecognition() }
                )
            }
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            // Hindi + English voice recognition
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                "hi-IN"
            )

            putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Nilkanth AI ko command dein..."
            )
        }

        try {
            startActivityForResult(intent, 100)
        } catch (e: Exception) {
            message = "Voice recognition available nahi hai."
        }
    }

    @Deprecated("Use Activity Result API in future versions")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {

            val results =
                data?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )

            val command = results?.firstOrNull() ?: return

            message = "Aapne kaha: $command"

            processCommand(
                command.lowercase(Locale.getDefault())
            )
        }
    }

    private fun processCommand(command: String) {

        when {

            command.contains("youtube") ||
            command.contains("यूट्यूब") -> {

                openApp("com.google.android.youtube")
            }

            command.contains("chrome") ||
            command.contains("क्रोम") -> {

                openApp("com.android.chrome")
            }

            command.contains("google") ||
            command.contains("गूगल") -> {

                openWebsite("https://www.google.com")
            }

            command.contains("phone") ||
            command.contains("फोन") ||
            command.contains("dialer") -> {

                val intent = Intent(Intent.ACTION_DIAL)
                startActivity(intent)
            }

            command.contains("search") ||
            command.contains("सर्च") -> {

                val query = command
                    .replace("search", "")
                    .replace("सर्च", "")
                    .trim()

                if (query.isNotEmpty()) {

                    val intent = Intent(
                        Intent.ACTION_WEB_SEARCH
                    ).apply {
                        putExtra("query", query)
                    }

                    startActivity(intent)

                } else {

                    message = "Kya search karna hai?"
                }
            }

            else -> {

                message = "Command mili: $command"
            }
        }
    }

    private fun openApp(packageName: String) {

        val launchIntent =
            packageManager.getLaunchIntentForPackage(packageName)

        if (launchIntent != null) {

            startActivity(launchIntent)

        } else {

            message = "Ye app phone mein installed nahi hai."
        }
    }

    private fun openWebsite(url: String) {

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )

        startActivity(intent)
    }
}

@Composable
fun NilkanthScreen(
    message: String,
    onSpeak: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "NILKANTH AI",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Your AI Voice Assistant"
            )

            Spacer(
                modifier = Modifier.height(40.dp)
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(40.dp)
            )

            Button(
                onClick = onSpeak,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {

                Text(
                    text = "🎤  Nilkanth se Baat Karein"
                )
            }
        }
    }
}
