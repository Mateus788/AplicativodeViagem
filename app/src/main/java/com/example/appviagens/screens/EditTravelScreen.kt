package com.example.appviagens.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Money
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appviagens.data.Travel
import com.example.appviagens.utils.isEndDateAfterStartDate
import com.example.appviagens.viewmodel.TravelViewModel
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun EditTravelScreen(
    navController: NavController,
    viewModel: TravelViewModel,
    travelId: Int,
    paddingValues: PaddingValues
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var travel by remember { mutableStateOf<Travel?>(null) }

    var destination by remember { mutableStateOf("") }
    var travelType by remember { mutableStateOf("Lazer") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }

    var destinationError by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf("") }
    var budgetError by remember { mutableStateOf("") }

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

    LaunchedEffect(travelId) {
        travel = viewModel.getTravelById(travelId)
        travel?.let {
            destination = it.destination
            travelType = it.travelType
            startDate = it.startDate
            endDate = it.endDate
            budget = it.budget.toString()
        }
    }

    if (travel != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Editar viagem",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 25.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = destination,
                onValueChange = { destination = it },
                label = {
                    Text(
                        destinationError.ifEmpty { "Destino" },
                        color = if (destinationError.isNotEmpty()) Color.Red else Color.Unspecified
                    )
                },
                leadingIcon = { Icon(Icons.Rounded.Flight, contentDescription = null) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Lazer", "Negócio").forEach { type ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        RadioButton(
                            selected = travelType == type,
                            onClick = { travelType = type }
                        )
                        Text(text = type, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = startDate,
                onValueChange = {},
                label = { Text("Data de Início") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { startDatePicker.show() }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    }
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = endDate,
                onValueChange = {},
                label = {
                    Text(
                        dateError.ifEmpty { "Data de Término" },
                        color = if (dateError.isNotEmpty()) Color.Red else Color.Unspecified
                    )
                },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { endDatePicker.show() }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    }
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = budget,
                onValueChange = { budget = it },
                label = {
                    Text(
                        budgetError.ifEmpty { "Orçamento (R$)" },
                        color = if (budgetError.isNotEmpty()) Color.Red else Color.Unspecified
                    )
                },
                leadingIcon = { Icon(Icons.Rounded.Money, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    destinationError = if (destination.isBlank()) "Destino é obrigatório" else ""
                    budgetError = if (budget.isBlank()) "Orçamento é obrigatório" else ""
                    dateError = if (startDate.isBlank() || endDate.isBlank()) "Datas são obrigatórias"
                    else if (!isEndDateAfterStartDate(startDate, endDate)) "Data final deve ser depois da inicial"
                    else ""

                    if (destinationError.isEmpty() && budgetError.isEmpty() && dateError.isEmpty()) {
                        val budgetValue = budget.toDoubleOrNull()
                        if (budgetValue == null) {
                            budgetError = "Orçamento inválido"
                            return@Button
                        }

                        val updatedTravel = travel!!.copy(
                            destination = destination,
                            travelType = travelType,
                            startDate = startDate,
                            endDate = endDate,
                            budget = budgetValue
                        )

                        coroutineScope.launch {
                            viewModel.updateTravel(updatedTravel)
                            Toast.makeText(context, "Viagem atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2979FF),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            ) {
                Text("Salvar alterações")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
