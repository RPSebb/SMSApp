package com.example.smsapp

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.AccountCircle
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smsapp.ui.theme.SMSAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

val requiredPermissions : List<String> = listOf(
    Manifest.permission.READ_SMS,
    Manifest.permission.READ_CONTACTS
)

val colorSaver: Saver<MutableState<Color>, Int> = Saver(
    save    = { it.value.toArgb()         },
    restore = { mutableStateOf(Color(it)) }
)

fun dateToString(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
    return format.format(date)
}

fun randomInt(min: Int = 0, max: Int = 1) : Int {
    return Random.nextInt(2) * (max - min) + min
}

fun randomColor(min: Int = 0, max: Int = 255) : Color {
    return Color(randomInt(min, max), randomInt(min, max), randomInt(min, max), 255)
}

const val CONVERSATION_LIST_SCREEN = "conversation"
const val PERMISSION_SCREEN    = "permission"

@OptIn(ExperimentalPermissionsApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SMSAppTheme { ScreenController() } }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScreenController() {
    val navController = rememberNavController() // Contrôleur de navigation
    val permissions = rememberMultiplePermissionsState(requiredPermissions)
    var startDestination = if(permissions.allPermissionsGranted) CONVERSATION_LIST_SCREEN else PERMISSION_SCREEN

    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination

    LaunchedEffect(permissions.allPermissionsGranted) {
        if(permissions.allPermissionsGranted) {
            navController.navigate(CONVERSATION_LIST_SCREEN) {
                popUpTo(PERMISSION_SCREEN) { inclusive = true } // Supprime PermissionScreen du stack
            }
        } else {
            if(navController.currentBackStackEntry?.destination?.route != PERMISSION_SCREEN) {
                navController.navigate(PERMISSION_SCREEN) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true } // Réinitialise le stack
                }
            }
        }
    }

    Scaffold(
        topBar = {
            when (currentDestination?.route) {
                PERMISSION_SCREEN -> PermissionTopBar()
                CONVERSATION_LIST_SCREEN -> TopAppBar(title = { Text("Conversation") }, modifier = Modifier.shadow(8.dp))
                else ->  TopAppBar(title = { Text("Caca") }, modifier = Modifier.shadow(8.dp))
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(PERMISSION_SCREEN) { PermissionScreen(permissions) }

            composable(CONVERSATION_LIST_SCREEN) {
                ConversationListScreen(
                    onNavigateToConversation = { threadId -> navController.navigate("conversation/$threadId") }
                )
            }

            composable(
                route = "conversation/{threadId}",
                arguments = listOf(navArgument("threadId") { type = NavType.LongType })
            ) { backStackEntry ->
                val threadId = backStackEntry.arguments?.getLong("threadId") ?: 0L
                ConversationScreen(threadId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionTopBar()  {
    TopAppBar(
        title = { Text("Permissions") },
        modifier = Modifier.shadow(8.dp),
        navigationIcon = {}
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(permissions: MultiplePermissionsState) {
    // Uncomment for colorful border, useless by the way
//    var angle = remember { mutableFloatStateOf(0f) }
//    val deltaTime : Long = 1000 / 60
//    val speed = 0.02f
//    val colors =
//    listOf(
//        Color(0xFFE2CFFF),
//        Color(0xFFC2D3F1),
//        Color(0xFFE7C0AB),
//        Color(0xFFAFB2F1),
//        Color(0xFFC795EC),
//        Color(0xFFA0E0F8),
//        Color(0xFFE2CFFF)
//    )
//
//    val brush = Brush.sweepGradient(colors)
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            angle.value += speed * deltaTime
//            delay(deltaTime)
//        }
//    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
//        Surface(
//            modifier = Modifier.height(85.dp).width(275.dp),
//            shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp)) {
//            Canvas(modifier = Modifier) {
//                rotate(degrees = angle.value) {
//                    drawCircle(
//                        brush = brush,
//                        radius = size.width,
//                        blendMode = BlendMode.SrcIn
//                    )
//                }
//            }
//        }
        Button(
            modifier = Modifier.height(80.dp).width(270.dp),
            shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp),
            onClick = { permissions.launchMultiplePermissionRequest() }
        ) {
            Text(
                fontSize = 15.sp,
                fontWeight = FontWeight.W500,
                text = "REQUEST PERMISSIONS",
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(viewModel: SMSViewModel = hiltViewModel(), onNavigateToConversation: (Long) -> Unit) {

    val listState = rememberLazyListState()
    val threads = viewModel.threads.collectAsState().value
    val orders  = viewModel.threadsOrder.collectAsState().value

    if(threads.isEmpty() || orders.isEmpty()) {
        EmptyUI()
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = orders,
                key = { it }
            ) { key ->
                Box(modifier = Modifier.clickable { onNavigateToConversation(key) })
                { ThreadItem(threads[key]) }
            }
        }
    }
}

@Composable
fun ConversationScreen(threadId: Long, viewModel: SMSViewModel = hiltViewModel()) {
    val listState = rememberLazyListState()
    val messages = viewModel.messages.collectAsState().value
    LaunchedEffect(threadId) {
        viewModel.getMessages(threadId)
    }

    if(messages == null) {
        EmptyUI()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            reverseLayout = true,
            state = rememberLazyListState()
        ) {
            items(items = messages[threadId]?.toList() ?: emptyList()) { message ->
                MessageItem(message.second)
            }
        }
    }
}

@Composable
fun EmptyUI() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "No message available")
    }
}

@Composable
fun MessageItem(message: SMS?) {
    if(message == null) {
        return ListItem(
            leadingContent = {
                Icon(
                    imageVector = Icons.TwoTone.Warning,
                    contentDescription = "Account Icon",
                    tint = Color.Red,
                    modifier = Modifier.size(40.dp)
                )
            },
            headlineContent = { Text("You shall never see this !") }
        )
    }

    val date = dateToString(message.date)
    var _alignment = Alignment.Start
    var _backgroundColor = Color(0xFFB6C2FF)
    var _shape = RoundedCornerShape(0.dp, 5.dp, 5.dp, 5.dp)

    if(message.type == 2L) {
        _alignment = Alignment.End
        _backgroundColor = Color(0xFFA1F1CE)
        _shape = RoundedCornerShape(5.dp, 5.dp, 0.dp, 5.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 5.dp),
        horizontalAlignment = _alignment) {
        Box(
            modifier = Modifier
                .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.85f)
                .clip(_shape)
                .background(_backgroundColor)
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

@Composable
fun ThreadItem(thread: SMSThread?) {
    if(thread == null) {
        return ListItem(
            leadingContent = {
                Icon(
                    imageVector = Icons.TwoTone.Warning,
                    contentDescription = "Account Icon",
                    tint = Color.Red,
                    modifier = Modifier.size(40.dp)
                )
            },
            headlineContent = { Text("You shall never see this !") }
        )
    }

    val date = dateToString(thread.date)
    val iconColor = rememberSaveable(saver = colorSaver) { mutableStateOf(randomColor(175, 225)) }

    ListItem(
        modifier = Modifier.fillMaxWidth().padding(0.dp).height(80.dp),
        leadingContent = {
            Icon(
                imageVector = Icons.TwoTone.AccountCircle,
                contentDescription = "Account Icon",
                tint = iconColor.value,
                modifier = Modifier.size(40.dp)
            )
        },
        headlineContent = {
            Text(text = thread.address)
        },
        supportingContent = {
            Text(
                text = thread.body,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        },
        trailingContent = {
            Text(text = date.take(8) + "\n" + date.takeLast(5))
        }
    )
    HorizontalDivider()
}