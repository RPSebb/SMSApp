package com.example.smsapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smsapp.models.Message
import com.example.smsapp.ui.componants.SendTextField
import com.example.smsapp.viewmodels.ConversationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    viewModel: ConversationViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val conversation = viewModel.conversation.collectAsState().value
    val listState = rememberLazyListState()
    val context = LocalContext.current

    var visibleItems = remember { mutableStateOf(emptyList<Int>()) }
    var text = rememberSaveable { mutableStateOf("")}
    val messages = viewModel.messages.collectAsState().value
    val processedMessages = remember { mutableStateOf<Set<Long>>(emptySet()) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
        .collect { visibleItemsInfo -> visibleItems.value = visibleItemsInfo.map { it.index } }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(conversation.address) },
                navigationIcon = {
                    IconButton(onClick = { onNavigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(WindowInsets.ime.asPaddingValues())) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                reverseLayout = true,
                state = listState
            ) {
                items(items = messages) { message ->
                    val isVisible = messages.indexOf(message) in visibleItems.value
                    if(message.read == 0 && isVisible && !processedMessages.value.contains(message.id) ) {
                        processedMessages.value = processedMessages.value + message.id
                        viewModel.setMessageRead(message.id)
                    }
                    MessageItem(message)
                }
            }
            SendTextField(
                text = text,
                maxLines = 5,
                paddingValues = paddingValues,
                onClick = {
                    viewModel.sendMessage(text = text.value)
                    .onSuccess {
                        text.value = ""
                        Toast.makeText(context, "Message sent", Toast.LENGTH_LONG).show()
                    }
                    .onFailure { error ->
                        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }
}

fun dateToString(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
    return format.format(date)
}

@Composable
fun MessageItem(message: Message) {
    val date = dateToString(message.date)
    var alignment = Alignment.Start
    var backgroundColor = Color(0xFFB6C2FF)
    var shape = RoundedCornerShape(0.dp, 5.dp, 5.dp, 5.dp)

    if(message.type == 2L) {
        alignment = Alignment.End
        backgroundColor = Color(0xFFA1F1CE)
        shape = RoundedCornerShape(5.dp, 5.dp, 0.dp, 5.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 5.dp),
        horizontalAlignment = alignment) {

//  For debug set read
//        Text(
//            text = "${message.id} - ${message.threadId}",
//            fontSize = 10.sp,
//            fontWeight = FontWeight.W500
//        )

        Box(
            modifier = Modifier
                .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.85f)
                .clip(shape)
                .background(backgroundColor)
                .padding(10.dp)
        ) {
            Text(
                text = message.body.trim(),
                fontWeight = FontWeight.W400,
                color = Color(0xFF101010)
            )
        }

        Text(
            text = date.take(8) + " " + date.takeLast(5),
            fontSize = 10.sp,
            fontWeight = FontWeight.W500
        )

    }
}