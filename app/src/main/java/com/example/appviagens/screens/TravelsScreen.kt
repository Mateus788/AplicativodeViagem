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
import com.example.appviagens.utils.isEndDateAfterStartDate
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

    var destinationError by remember { mutableStateOf("") }
    var budgetError by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf("") }

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
            onValueChange = {
                destination = it
                if (it.isNotBlank()) destinationError = ""
            },
            label = {
                Text(
                    destinationError.ifEmpty { "Destino" },
                    color = if (destinationError.isNotEmpty()) Color.Red else Color.Unspecified
                )
            },
            isError = destinationError.isNotEmpty(),
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
            label = {
                Text(
                    dateError.ifEmpty { "Data Final" },
                    color = if (dateError.isNotEmpty()) Color.Red else Color.Unspecified
                )
            },
            isError = dateError.isNotEmpty(),
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
            onValueChange = {
                budget = it
                if (it.isNotBlank()) budgetError = ""
            },
            label = {
                Text(
                    budgetError.ifEmpty { "Orçamento (R$)" },
                    color = if (budgetError.isNotEmpty()) Color.Red else Color.Unspecified
                )
            },
            isError = budgetError.isNotEmpty(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                destinationError = if (destination.isBlank()) "Campo obrigatório" else ""
                budgetError = if (budget.isBlank()) "Campo obrigatório" else ""
                dateError = when {
                    startDate.isBlank() || endDate.isBlank() -> "Datas obrigatórias"
                    !isEndDateAfterStartDate(startDate, endDate) -> "Data final deve ser após a inicial"
                    else -> ""
                }

                if (destinationError.isEmpty() && budgetError.isEmpty() && dateError.isEmpty()) {
                    val budgetDouble = budget.replace(",", ".").toDoubleOrNull()
                    if (budgetDouble == null) {
                        budgetError = "Valor inválido"
                        return@Button
                    }

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
                    Toast.makeText(context, "Viagem cadastrada com sucesso!", Toast.LENGTH_SHORT).show()

                    // Limpar campos
                    destination = ""
                    travelType = "Lazer"
                    startDate = ""
                    endDate = ""
                    budget = ""

                    // Retornar ou navegar
                    navController.popBackStack()
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
