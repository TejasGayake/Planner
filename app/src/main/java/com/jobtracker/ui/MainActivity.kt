package com.jobtracker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JobTrackerApp(
                parsedJobJson = intent?.getStringExtra("parsed_job_json"),
                imageUri = intent?.getStringExtra("image_uri")
            )
        }
    }
}
