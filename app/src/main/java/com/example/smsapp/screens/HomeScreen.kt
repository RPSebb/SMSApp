package com.example.smsapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smsapp.models.Conversation
import com.example.smsapp.viewmodels.HomeViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.min
import kotlin.random.Random

private val colorMapSaver: Saver<MutableMap<Long, Color>, List<Pair<Long, Int>>> = Saver(
    save = { map ->
        map.map { (key, color) -> key to color.toArgb() }
    },
    restore = { list ->
        list.toMap().mapValues { (_, argb) -> Color(argb) }.toMutableMap()
    }
)

private fun randomInt(min: Int = 0, max: Int = 1) : Int {
    return Random.nextInt(2) * (max - min) + min
}

private fun randomColor(min: Int = 0, max: Int = 255) : Color {
    return Color(randomInt(min, max), randomInt(min, max), randomInt(min, max), 255)
}

private fun getDatePattern(todayDate: ZonedDateTime, conversationDate: ZonedDateTime, weekFields: WeekFields) : String {
    val todayWeek = todayDate.get(weekFields.weekOfWeekBasedYear())
    val conversationWeek = conversationDate.get(weekFields.weekOfWeekBasedYear())
    if(todayDate.year == conversationDate.year) {
        if (todayWeek == conversationWeek) {
            if (todayDate.dayOfYear == conversationDate.dayOfYear) {
                return "HH:mm"
            }
            return "EEEE"
        }
        return "dd MMMM"
    }
    return "dd/MM/yy"
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), onNavigateToMessages: (Conversation) -> Unit) {

    val listState = rememberLazyListState()
    val conversations = viewModel.conversations.collectAsState().value
    var todayDate  = rememberSaveable { Instant.now().atZone(ZoneId.systemDefault()) }
    val weekFields = rememberSaveable { WeekFields.of(Locale.getDefault()) }
    val iconColors = rememberSaveable(saver = colorMapSaver) { mutableMapOf<Long, Color>() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Conversations") }) },
    ) { paddingValues ->
        if (conversations.isEmpty()) { EmptyThreads() }
        else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                items(
                    items = conversations
                ) { conversation ->
                    val conversationDate =
                        Instant.ofEpochMilli(conversation.date).atZone(ZoneId.systemDefault())
                    val formatedDate = conversationDate.format(
                        DateTimeFormatter.ofPattern(
                            getDatePattern(
                                todayDate,
                                conversationDate,
                                weekFields
                            )
                        )
                    )

                    if (iconColors[conversation.threadId] == null) {
                        iconColors[conversation.threadId] = randomColor(175, 225)
                    }

                    Box(modifier = Modifier.clickable { onNavigateToMessages(conversation) })
                    {
                        ConversationItem(
                            conversation,
                            iconColors[conversation.threadId] ?: Color.Red,
                            formatedDate
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyThreads() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) { Text(text = "No conversation available") }
}

@Composable
fun ConversationItem(conversation: Conversation, iconColor: Color, formatedDate: String) {
    val fontWeight = if(conversation.read == 0) FontWeight.W700 else FontWeight.W400
    val count = min(conversation.count, 99)
//    val count = 1
    val msgCountColor = MaterialTheme.colorScheme.surface
    val msgCountBackgroundColor = MaterialTheme.colorScheme.primary

    ListItem(
        modifier = Modifier.fillMaxWidth().padding(0.dp).height(75.dp),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        leadingContent = {
            Icon(
                imageVector = Icons.TwoTone.AccountCircle,
                contentDescription = "Account Icon",
                tint = iconColor,
                modifier = Modifier.size(50.dp)
            )
        },
        headlineContent = {
            Text(
                text = conversation.address,
                fontWeight = fontWeight
            )
        },
        supportingContent = {
            Text(
                text = conversation.body,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                fontWeight = fontWeight
            )
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text(text = formatedDate, fontWeight = fontWeight, fontSize = 12.sp)

                if(conversation.read == 0) {
                    Text(
                        text = "$count",
                        color = msgCountColor,
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        modifier = Modifier
                        .padding(top = 6.dp, end = 12.dp)
                        .drawBehind {
                            drawCircle(
                                color = msgCountBackgroundColor,
                                radius = 23f
                            )
                        }
                    )
                }
            }
        }
    )
}