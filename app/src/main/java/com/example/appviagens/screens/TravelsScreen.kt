package com.example.appviagens.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appviagens.data.Travel
import com.example.appviagens.viewmodel.TravelViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelsScreen(navController: NavController, viewModel: TravelViewModel, userId: Int) {
    val context = LocalContext.current

    var destination by remember { mutableStateOf("") }
    var travelType by remember { mutableStateOf("Lazer") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()

    val startDatePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth -> startDate = "$dayOfMonth/${month + 1}/$year" },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val endDatePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth -> endDate = "$dayOfMonth/${month + 1}/$year" },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destino") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Tipo de Viagem:")
        Row {
            listOf("Lazer", "Negócio").forEach { type ->
                Row(
                    modifier = Modifier.padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = travelType == type,
                        onClick = { travelType = type }
                    )
                    Text(text = type, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }

        OutlinedTextField(
            value = startDate,
            onValueChange = {},
            label = { Text("Data de Início") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { startDatePicker.show() }) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Selecionar Data")
                }
            }
        )

        OutlinedTextField(
            value = endDate,
            onValueChange = {},
            label = { Text("Data Final") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { endDatePicker.show() }) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Selecionar Data")
                }
            }
        )

        OutlinedTextField(
            value = budget,
            onValueChange = { budget = it },
            label = { Text("Orçamento ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (destination.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && budget.isNotBlank()) {
                    try {
                        val budgetDouble = budget.toDouble()

                        val travel = Travel(
                            id = 0,
                            userId = userId,
                            destination = destination,
                            travelType = travelType,
                            startDate = startDate,
                            endDate = endDate,
                            budget = budgetDouble
                        )

                        viewModel.insertTravel(travel)
                        Toast.makeText(context, "Viagem cadastrada!", Toast.LENGTH_SHORT).show()

                        destination = ""
                        travelType = "Lazer"
                        startDate = ""
                        endDate = ""
                        budget = ""

                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "Digite um valor numérico válido para o orçamento.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2979FF),
                contentColor = Color.White
            )
        ) {
            Text("Cadastrar")
        }
    }
}
