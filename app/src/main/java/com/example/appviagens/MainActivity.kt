package com.example.appviagens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.appviagens.data.UserDatabase
import com.example.appviagens.screens.AppNavigation
import com.example.appviagens.ui.theme.ProjetoFinalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = UserDatabase.getDatabase(applicationContext)

        enableEdgeToEdge()
        setContent {
            ProjetoFinalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(paddingValues = innerPadding, database = database)
                }
            }
        }
    }
}
