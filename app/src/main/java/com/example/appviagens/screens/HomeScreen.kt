package com.example.appviagens.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appviagens.R
import com.example.appviagens.data.Travel
import com.example.appviagens.viewmodel.TravelViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: TravelViewModel, userId: Int) {
    var travels by remember { mutableStateOf<List<Travel>>(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId > 0) {
            travels = viewModel.getTravelsByUser(userId)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Minhas Viagens", style = MaterialTheme.typography.headlineMedium)

            if (travels.isEmpty()) {
                Text("Nenhuma viagem cadastrada.", modifier = Modifier.padding(top = 16.dp))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp, bottom = 80.dp)
                ) {
                    items(travels, key = { it.id }) { travel ->
                        val dismissState = rememberSwipeToDismissBoxState()

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = true,
                            enableDismissFromEndToStart = true,
                            backgroundContent = {
                                val icon = when (dismissState.targetValue) {
                                    SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Delete
                                    SwipeToDismissBoxValue.EndToStart -> Icons.Default.Description
                                    else -> null
                                }

                                icon?.let {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 8.dp),
                                        contentAlignment = if (icon == Icons.Default.Delete) Alignment.CenterStart else Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null
                                        )
                                    }
                                }
                            },
                            content = {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onLongPress = {
                                                    navController.navigate("EditarViagem/${travel.id}")
                                                }
                                            )
                                        },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val imageRes = if (travel.travelType == "Lazer") {
                                            R.drawable.leisure_image
                                        } else {
                                            R.drawable.business_image
                                        }

                                        Image(
                                            painter = painterResource(id = imageRes),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .padding(end = 8.dp)
                                        )

                                        Column {
                                            Text("Destino: ${travel.destination}", style = MaterialTheme.typography.bodyLarge)
                                            Text("Período: ${travel.startDate} - ${travel.endDate}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Orçamento: R$${travel.budget}", style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        )

                        // Ações de swipe
                        LaunchedEffect(dismissState.targetValue) {
                            when (dismissState.targetValue) {
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Excluir viagem?",
                                        actionLabel = "Sim",
                                        withDismissAction = true,
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.deleteTravel(travel.id)
                                        travels = travels.filter { it.id != travel.id }
                                    } else {
                                        dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                                    }
                                }

                                SwipeToDismissBoxValue.EndToStart -> {
                                    coroutineScope.launch {
                                        try {
                                            val roteiro = viewModel.gerarRoteiroGemini(travel)
                                            val encodedRoteiro = Uri.encode(roteiro)
                                            navController.navigate("roteiroIA/$encodedRoteiro")
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar("Erro: ${e.localizedMessage}")
                                        } finally {
                                            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                                        }
                                    }
                                }

                                else -> Unit
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}