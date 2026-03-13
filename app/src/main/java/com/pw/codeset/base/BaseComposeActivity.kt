package com.pw.codeset.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class BaseComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
        }
    }
}
