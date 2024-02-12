package com.example.examplegemini

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.client.generativeai.GenerativeModel
import com.example.examplegemini.ui.theme.ExampleGeminiTheme
import dev.jeziellago.compose.markdowntext.MarkdownText

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExampleGeminiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                ) {
                    val generativeModel = GenerativeModel(
                            modelName = "gemini-pro",
                            apiKey = BuildConfig.apiKey
                    )
                    val viewModel = SummarizeViewModel(generativeModel)
                    SummarizeRoute(viewModel)
                }
            }
        }
    }
}

@Composable
internal fun SummarizeRoute(
        summarizeViewModel: SummarizeViewModel = viewModel()
) {
    val summarizeUiState by summarizeViewModel.uiState.collectAsState()

    SummarizeScreen(summarizeUiState, onSummarizeClicked = { inputText ->
        summarizeViewModel.summarize(inputText)
    })
}

@Composable
fun SummarizeScreen(
        uiState: SummarizeUiState = SummarizeUiState.Initial,
        onSummarizeClicked: (String) -> Unit = {}
) {


    var prompt by remember { mutableStateOf("") }

        val scrollState = rememberScrollState()
        when (uiState) {
            SummarizeUiState.Initial -> {
                // Nothing is shown
            }
            SummarizeUiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }

            is SummarizeUiState.Success -> {
                val linear = Brush.linearGradient(listOf(Color.Red, Color.Blue))
                Box (
                    Modifier
                        .fillMaxSize(1F)
                        .padding(bottom = 85.dp, end = 10.dp, start = 10.dp, top=10.dp)
                        .clip(RoundedCornerShape(20.dp))
                        ) {

                    Surface (Modifier.fillMaxSize()){
                        Box(modifier = Modifier.fillMaxSize().background(linear)){

                            Row(
                                modifier = Modifier
                                    .padding(all = 8.dp)
                                    .verticalScroll(scrollState)
                            ) {
                                Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = "Person Icon",
                                )

                                MarkdownText(
                                    markdown = uiState.outputText,
                                    style = TextStyle(
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        lineHeight = 10.sp,
                                        textAlign = TextAlign.Justify,
                                    )
                                )

                            }

                        }

                    }
                }

            }

            is SummarizeUiState.Error -> MarkdownText(markdown = uiState.errorMessage)

        }

        Box (Modifier.fillMaxSize(1F), Alignment.BottomEnd){
        Row (){
            TextField(
                value = prompt,
                label = { Text(stringResource(R.string.summarize_label)) },
                placeholder = { Text(stringResource(R.string.summarize_hint)) },
                onValueChange = { prompt = it },
                modifier = Modifier
                    .weight(8f)
            )
            TextButton(
                onClick = {
                    if (prompt.isNotBlank()) {
                        onSummarizeClicked(prompt)
                    }
                },

                modifier = Modifier
                    .weight(2f)
                    .padding(all = 4.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(stringResource(R.string.action_go))
            }
        }
        }

}



@Composable
@Preview(showSystemUi = true)
fun SummarizeScreenPreview() {
    SummarizeScreen()
}