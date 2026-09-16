package com.sonier.ai

import android.content.Intent
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

    private var resultText by mutableStateOf("Namaste! Main Sonier hoon.")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                SonierScreen(
                    text = resultText,
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
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )
            putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Sonier ko command dein..."
            )
        }

        try {
            startActivityForResult(intent, 100)
        } catch (e: Exception) {
            resultText = "Voice recognition available nahi hai."
        }
    }

    @Deprecated("Use Activity Result API in production")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val results =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

            val command = results?.firstOrNull() ?: return
            resultText = "Aapne kaha: $command"

            processCommand(command.lowercase(Locale.getDefault()))
        }
    }

    private fun processCommand(command: String) {

        when {
            command.contains("youtube") -> {
                openApp("com.google.android.youtube")
            }

            command.contains("chrome") -> {
                openApp("com.android.chrome")
            }

            command.contains("google") -> {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    android.net.Uri.parse("https://www.google.com")
                )
                startActivity(intent)
            }

            command.contains("phone") ||
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

                val intent = Intent(
                    Intent.ACTION_WEB_SEARCH
                ).apply {
                    putExtra("query", query)
                }

                startActivity(intent)
            }

            else -> {
                resultText =
                    "Command samajh gaya: $command"
            }
        }
    }

    private fun openApp(packageName: String) {
        val launchIntent =
            packageManager.getLaunchIntentForPackage(packageName)

        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            resultText = "Ye app phone mein nahi mila."
        }
    }
}

@Composable
fun SonierScreen(
    text: String,
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
                text = "SONIER AI",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onSpeak,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("🎤  Sonier se Baat Karein")
            }
        }
    }
}
