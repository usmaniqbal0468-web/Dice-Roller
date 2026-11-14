package com.example.diceroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlin.random.Random
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DiceApp()
            }
        }
    }
}
object Routes {
    const val MAIN = "main"
    const val RESULT = "result"
    const val ARG_VALUE = "value"

    const val RESULT_PATTERN = "$RESULT/{$ARG_VALUE}"

    fun result(value: Int) = "$RESULT/$value"
}

@Composable
fun DiceApp(navController: NavHostController = rememberNavController()) {
    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.MAIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.MAIN) {
                MainDiceScreen(
                    onGoToResult = { roll -> navController.navigate(Routes.result(roll)) }
                )
            }

            composable(
                route = Routes.RESULT_PATTERN,
                arguments = listOf(navArgument(Routes.ARG_VALUE) { type = NavType.IntType })
            ) { backStackEntry ->
                val value = (backStackEntry.arguments?.getInt(Routes.ARG_VALUE) ?: 1).coerceIn(1, 6)

                DieResultRouter(
                    value = value,
                    onBack = { navController.popBackStack() },
                    onNavigate = { target -> navController.navigate(Routes.result(target)) }
                )
            }
        }
    }
}


private fun diceDrawable(value: Int): Int = when (value) {
    1 -> R.drawable.dice_1
    2 -> R.drawable.dice_2
    3 -> R.drawable.dice_3
    4 -> R.drawable.dice_4
    5 -> R.drawable.dice_5
    else -> R.drawable.dice_6
}

@Composable
private fun DiceImage(value: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(diceDrawable(value)),
        contentDescription = "Die showing $value",
        modifier = modifier.size(120.dp)
    )
}

@Composable
private fun ScreenContainer(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        content = content
    )
}

@Composable
private fun ResultShell(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    ScreenContainer {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        content()
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Back")
        }
    }
}

@Composable
fun MainDiceScreen(onGoToResult: (Int) -> Unit) {
    // rememberSaveable keeps the roll after screen rotation
    var roll by rememberSaveable { mutableStateOf(1) }

    ScreenContainer {
        Text(
            text = "Main Dice Screen",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        DiceImage(roll)
        Text(text = "Current Roll: $roll", fontSize = 18.sp)

        Button(
            onClick = { roll = Random.nextInt(1, 7) }, // 1..6 (upper bound is exclusive)
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Roll Die")
        }
        Button(
            onClick = { onGoToResult(roll) },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Go to Result")
        }
    }
}
@Composable
fun DieResultRouter(
    value: Int,
    onBack: () -> Unit,
    onNavigate: (Int) -> Unit
) {
    when (value) {
        1 -> DieResult1(onBack)
        2 -> DieResult2(value, onBack)
        3 -> DieResult3(value, onBack)
        4 -> DieResult4(onBack, onNavigate)
        5 -> DieResult5(value, onBack)
        else -> DieResult6(value, onBack, onNavigate)
    }
}

@Composable
fun DieResult1(onBack: () -> Unit) {
    var temperature by rememberSaveable { mutableStateOf(Random.nextInt(5, 36)) }

    ResultShell(title = "Die Result: 1", onBack = onBack) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "\u2708 Favorite Travel Destination",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(text = "Santorini, Greece", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "Mock Temperature: $temperature\u00B0C")
                Button(onClick = { temperature = Random.nextInt(5, 36) }) {
                    Text("Refresh Temperature")
                }
            }
        }
    }
}
@Composable
fun DieResult2(value: Int, onBack: () -> Unit) {
    var current by rememberSaveable { mutableStateOf(value) }

    ResultShell(title = "Die Result: 2", onBack = onBack) {
        DiceImage(current)
        Text(text = "Current Value: $current", fontWeight = FontWeight.Bold)
        Button(
            onClick = { current = ((current - 1 + 2) % 6) + 1 },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Increment by 2")
        }
    }
}

@Composable
fun DieResult3(value: Int, onBack: () -> Unit) {
    var current by rememberSaveable { mutableStateOf(value) }
    var text by rememberSaveable { mutableStateOf("") }

    val parsed = text.toIntOrNull()
    val invalid = text.isNotEmpty() && (parsed == null || parsed !in 1..6)

    ResultShell(title = "Die Result: 3", onBack = onBack) {
        DiceImage(current)
        Text(text = "Current Value: $current", fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = text,
            onValueChange = { input ->
                // digits only, a single character (a die has one digit)
                val filtered = input.filter { it.isDigit() }.takeLast(1)
                text = filtered
                val n = filtered.toIntOrNull()
                if (n != null && n in 1..6) {
                    current = n
                }
            },
            label = { Text("Enter a value (1 - 6)") },
            singleLine = true,
            isError = invalid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(0.7f)
        )
        if (invalid) {
            Text(
                text = "Please enter a number between 1 and 6",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun DieResult4(onBack: () -> Unit, onNavigate: (Int) -> Unit) {
    ResultShell(title = "Die Result: 4", onBack = onBack) {
        Text(text = "Select a screen to navigate to:", fontSize = 14.sp)
        for (i in 1..6) {
            Button(
                onClick = { onNavigate(i) },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Go to Screen $i")
            }
        }
    }
}

@Composable
fun DieResult5(value: Int, onBack: () -> Unit) {
    ResultShell(title = "Die Result: 5", onBack = onBack) {
        DiceImage(value)
        Text(text = "Result: $value", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DieResult6(value: Int, onBack: () -> Unit, onNavigate: (Int) -> Unit) {
    // null = second die not rolled yet
    var secondRoll by rememberSaveable { mutableStateOf<Int?>(null) }
    val second = secondRoll

    ResultShell(title = "Die Result: 6 (Dice Game)", onBack = onBack) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Current Die", fontSize = 12.sp)
                DiceImage(value, Modifier.padding(top = 4.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Second Die", fontSize = 12.sp)
                if (second == null) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(120.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("?", fontSize = 40.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    DiceImage(second, Modifier.padding(top = 4.dp))
                }
            }
        }

        Button(
            onClick = { secondRoll = Random.nextInt(1, 7) },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Roll Second Die")
        }

        if (second != null) {
            val won = second >= value
            Text(
                text = if (won) "You Won! ($second \u2265 $value)" else "You Lost ($second < $value)",
                fontWeight = FontWeight.Bold,
                color = if (won) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            if (won) {
                Button(
                    onClick = { onNavigate(second) },
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("Go to Result")
                }
            }
        }
    }
}