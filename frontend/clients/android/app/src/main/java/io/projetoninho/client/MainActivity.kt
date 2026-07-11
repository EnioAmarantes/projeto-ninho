package io.projetoninho.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.projetoninho.client.ui.theme.App
import io.projetoninho.client.ui.theme.ProjetoNinhoClientTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ProjetoNinhoClientTheme {
                App()
            }
        }
    }
}