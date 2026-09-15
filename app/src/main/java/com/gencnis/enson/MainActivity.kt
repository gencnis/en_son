package com.gencnis.enson

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.gencnis.enson.ui.home.HomeScreen
import com.gencnis.enson.ui.theme.EnSonTheme
import com.gencnis.enson.ui.track.NewTrackScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            EnSonTheme {

                var showNewTrackScreen by rememberSaveable {
                    mutableStateOf(false)
                }

                if (showNewTrackScreen) {
                    NewTrackScreen(
                        onBack = {
                            showNewTrackScreen = false
                        },
                        onCreate = {
                            showNewTrackScreen = false
                        }
                    )
                } else {
                    HomeScreen(
                        onCreateTrack = {
                            showNewTrackScreen = true
                        }
                    )
                }
            }
        }
    }
}