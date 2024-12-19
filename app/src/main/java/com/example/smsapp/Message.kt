package com.example.smsapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class Message(private var text: String = "") {

    private var padding: PaddingValues = PaddingValues(0.dp)
    private var margin: PaddingValues = PaddingValues(0.dp)

    @Composable
    fun Render() {
        Box(
            modifier = Modifier
                .padding(padding)
                .background(Color(0, 0, 255, 255))
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(margin)
            )
        }
    }

    fun text(value: String): Message {
        text = value
        return this
    }

    fun padding(value: PaddingValues): Message {
        padding = value
        return this
    }

    fun margin(value: PaddingValues): Message {
        margin = value
        return this
    }
}