package com.group.mobileparkingchain.features.chat.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group.mobileparkingchain.features.chat.data.model.ChatMessage
import com.group.mobileparkingchain.features.chat.presentation.viewmodel.ChatViewModel
import com.group.mobileparkingchain.ui.theme.sdp
import com.group.mobileparkingchain.ui.theme.ssp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F1216), Color(0xFF131B22), Color(0xFF0F1216))
    )
    val accent = Color(0xFF1AA6A6)
    val userBubble = Color(0xFF1B6E6A)
    val botBubble = Color(0xFF1E242C)
    val botBubbleBorder = Color(0xFF2A323D)
    val inputBg = Color(0xFF1A2028)

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    // Prevent initial autofocus on the input field when entering the screen
    LaunchedEffect(Unit) {
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("AI Assistant", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Ask about parking, payments, or your account",
                            color = Color(0xFF9AA5B1),
                            fontSize = ssp(12)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF131B22),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundGradient)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    })
                }
        ) {
            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = sdp(16))
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = sdp(12), bottom = sdp(8)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF121820)),
                        shape = RoundedCornerShape(sdp(16))
                    ) {
                        Column(modifier = Modifier.padding(sdp(12))) {
                            Text(
                                "Quick tips",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = ssp(14)
                            )
                            Text(
                                "Try: “How do I book a spot?” or “What payments are supported?”",
                                color = Color(0xFFB7C0CC),
                                fontSize = ssp(12),
                                lineHeight = ssp(18)
                            )
                        }
                    }
                }

                items(messages) { message ->
                    MessageBubble(message)
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = sdp(8)),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            // Helper box for alignment and shape of the bot's "thinking" state
                            Column(
                                modifier = Modifier
                                    .widthIn(max = sdp(280))
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = sdp(16),
                                            topEnd = sdp(16),
                                            bottomStart = sdp(0),
                                            bottomEnd = sdp(16)
                                        )
                                    )
                                    .background(botBubble)
                                    .border(1.dp, botBubbleBorder, RoundedCornerShape(sdp(16)))
                                    .padding(sdp(12))
                            ) {
                                TypingIndicator(
                                    dotSize = sdp(8),
                                    dotColor = Color.White,
                                    modifier = Modifier.padding(horizontal = sdp(4))
                                )
                            }
                        }
                    }
                }
            }

            // Input Area
            Surface(
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F141A))
                        .navigationBarsPadding()
                        .padding(horizontal = sdp(12), vertical = sdp(10)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Type your question...", color = Color(0xFF8B95A1)) },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(sdp(14)))
                            .background(inputBg),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = inputBg,
                            unfocusedContainerColor = inputBg,
                            disabledContainerColor = inputBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = accent
                        ),
                        maxLines = 3
                    )

                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendMessage(textInput)
                                textInput = ""
                            }
                        },
                        enabled = textInput.isNotBlank() && !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (textInput.isNotBlank()) accent else Color(0xFF5C6673)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val isUser = message.isUser
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = sdp(4)),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = sdp(280))
                .clip(
                    RoundedCornerShape(
                        topStart = sdp(16),
                        topEnd = sdp(16),
                        bottomStart = if (isUser) sdp(16) else sdp(0),
                        bottomEnd = if (isUser) sdp(0) else sdp(16)
                    )
                )
                .background(if (isUser) Color(0xFF1B6E6A) else Color(0xFF1E242C))
                .border(
                    width = 1.dp,
                    color = if (isUser) Color(0xFF1B6E6A) else Color(0xFF2A323D),
                    shape = RoundedCornerShape(
                        topStart = sdp(16),
                        topEnd = sdp(16),
                        bottomStart = if (isUser) sdp(16) else sdp(0),
                        bottomEnd = if (isUser) sdp(0) else sdp(16)
                    )
                )
                .padding(sdp(12))
        ) {
            Text(
                text = message.content,
                color = Color.White,
                fontSize = ssp(15),
                lineHeight = ssp(21)
            )
        }
    }
}
