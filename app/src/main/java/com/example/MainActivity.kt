package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.TicketRepository
import com.example.ui.PizzaEventScreen
import com.example.ui.TicketViewModel
import com.example.ui.TicketViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = AppDatabase.getDatabase(applicationContext)
    val repository = TicketRepository(database.ticketDao())
    val factory = TicketViewModelFactory(repository)

    setContent {
      MyApplicationTheme {
        val viewModel: TicketViewModel = viewModel(factory = factory)
        PizzaEventScreen(viewModel = viewModel)
      }
    }
  }
}

