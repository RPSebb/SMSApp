package com.example.smsapp

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.Telephony
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.twotone.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smsapp.ui.theme.SMSAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.Long
import kotlin.random.Random

val requiredPermissions : List<String> = listOf(
    android.Manifest.permission.READ_SMS,
    android.Manifest.permission.READ_CONTACTS
)

val mapSaver = Saver<SnapshotStateMap<Long, ThreadData>, List<ThreadEntry>>(
    save = { stateMap -> stateMap.map{ ThreadEntry(it.key, it.value) }},
    restore = { list ->
        mutableStateMapOf<Long, ThreadData>().apply {
            putAll(list.map{ it.id to it.threadData })
        }
    }
)

val colorSaver: Saver<MutableState<Color>, Int> = Saver(
    save    = { it.value.toArgb()         },
    restore = { mutableStateOf(Color(it)) }
)

@Parcelize
data class ThreadEntry(val id: Long, val threadData: ThreadData) : Parcelable

@Parcelize
data class ThreadData(var id: Long, var threadId: Long, var address: String, var body: String, var date: Long) : Parcelable

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

fun Cursor.getValue(columnName: String): Any? {
    val columnIndex = getColumnIndex(columnName)
    val type = getType(columnIndex)

    return when (type) {
        Cursor.FIELD_TYPE_STRING -> getString(columnIndex)
//        Cursor.FIELD_TYPE_INTEGER -> getInt(columnIndex)
        Cursor.FIELD_TYPE_INTEGER -> getLong(columnIndex)
        Cursor.FIELD_TYPE_FLOAT -> getFloat(columnIndex)
//        Cursor.FIELD_TYPE_FLOAT -> getDouble(columnIndex)
        else -> null
    }
}

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SMSAppTheme {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    ConversationList()
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ConversationList() {
    val permissions = rememberMultiplePermissionsState(requiredPermissions)
    if(!permissions.allPermissionsGranted) { PermissionUI(permissions) }
    else {
        val contentResolver = LocalContext.current.contentResolver
        val datas           = rememberSaveable(saver = mapSaver) { mutableStateMapOf() }
        val keysOrder       = rememberSaveable<MutableList<Long>> { mutableListOf() }
        val initialLaunch   = rememberSaveable { mutableStateOf(true) }
        val scrollState     = rememberLazyListState()

        if(initialLaunch.value) {
            LaunchedEffect("fetch_threads") {
                launch {
                    fetchThreads(contentResolver, datas, keysOrder)
                    initialLaunch.value = false
                }
            }
        }

        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxWidth().background(White)
        ) {
//            items(keysOrder) { key -> ThreadItem(datas.getValue(key)) }
            items(keysOrder) { key -> ThreadItem(datas.getValue(key)) }
        }
    }
}

suspend fun fetchThreads(contentResolver: ContentResolver, threadsData: SnapshotStateMap<Long, ThreadData>, keysOrder: MutableList<Long>) = withContext(Dispatchers.IO) {
    val uri = Telephony.Threads.CONTENT_URI
    val projection = arrayOf("_id", "thread_id", "address", "body", "date")
    val selection = "address NOT NULL AND body NOT NULL AND date NOT NULL"
    val order = "date DESC"
    val cursor = contentResolver.query(uri, projection, selection, null, order)
    val tampon = mutableListOf<ThreadData>()
    val tamponSize = 30
    var count = 0

    cursor?.use {
        while (it.moveToNext()) {
            val threadData = ThreadData(it.getLong(0), it.getLong(1), it.getString(2), it.getString(3), it.getLong(4))
//            threadsData[it.getLong(0)] = threadData
            if(tampon.count() < tamponSize) { tampon.add(threadData) }
            else { tampon[count] = threadData }

            count += 1

            if(it.isLast || count == tamponSize) {
                withContext(Dispatchers.Default) {
                        threadsData.putAll(tampon.take(count).associateBy { it.threadId })
                        keysOrder.clear()
                        keysOrder.addAll(
                        threadsData.keys.toList().sortedByDescending { threadsData[it]?.date })
                }

                count = 0
            }
        }
    }
}

fun showColumns(contentResolver: ContentResolver, uri: Uri) {
    val cursor = contentResolver.query(uri, null, null, null, null)
    Log.d("RPSebb", uri.toString())
    cursor?.use {
        Log.d("RPSebb", it.columnNames.joinToString(", "))
    }
}

fun queryDb(contentResolver: ContentResolver, uri: Uri, projection: Array<String>? = null, selection: String? = null, selectionArgs: Array<String>? = null, order: String? = null) {

    val cursor = contentResolver.query(uri, projection, selection, selectionArgs, order)

    var i = 0
    cursor?.use {
        var line : String = ""
        for(column in it.columnNames) {
            line += column.take(20).padEnd(20, ' ') + " | "
        }
        Log.d("RPSebb", line)

        while(it.moveToNext()) {
//            line = ""
//            for(column in it.columnNames) {
//                val value: String = it.getValue(column).toString()
//                    .replace("\n", "")
//                    .replace("\r", "")
//                    .replace(Regex("[\\p{So}\\p{C}]+"), "\uFFFD")
//                    .take(20)
//                    .padEnd(20, ' ')
//
//                line += "$value | "
//            }
//            Log.d("RPSebb", line)
            if(!it.isLast) { continue }
            for(column in it.columnNames) {
                val value: String = it.getValue(column).toString()
                    .replace("\n", "")
                    .replace("\r", "")
                    .replace(Regex("[\\p{So}\\p{C}]+"), "\uFFFD")
                    .take(20)
                    .padEnd(20, ' ')
                Log.d("RPSebb", "$column : $value")
            }
//            if(++i > 0) { break }
        }
    }
}

fun getAddressWithThreadId(contentResolver: ContentResolver, threadId: Long) : String {

    val cursor = contentResolver.query(
        Telephony.Sms.CONTENT_URI,
        arrayOf(Telephony.Sms.ADDRESS),
        "${Telephony.Sms.THREAD_ID} = ?",
        arrayOf(threadId.toString()),
        Telephony.Sms.DATE + " DESC LIMIT 1"
    )

    return cursor?.use {
        it.moveToFirst()
        return it.getString(0)
    }.toString()
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionUI(permissions: MultiplePermissionsState) {
    Column(
        modifier = Modifier.fillMaxWidth())
    {
        Button(onClick = { permissions.launchMultiplePermissionRequest() })
        {
            Text("Request permission")
        }
    }
}

@Composable
fun ThreadItem(thread: ThreadData) {
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

@Composable
fun ThreadUI(data: ThreadData) {
    val height = 85.dp
    Row(
        modifier = Modifier.fillMaxWidth().height(height).background(White)
            .drawBehind {
                drawLine(
                    color = Color(210, 210, 210, 255),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }

    ) {
        Icon(
            imageVector =  Icons.Rounded.AccountCircle,
            contentDescription = "Account Circle Icon",
            tint = randomColor(0, 255),
            modifier = Modifier.width(60.dp).fillMaxHeight()
                .padding(start = 5.dp, end = 4.dp, top = 10.dp, bottom = 20.dp)
        )
        Column(modifier = Modifier.weight(1f).fillMaxHeight())
        {
            Text(
                text = data.address,
                style = TextStyle(
                    color = Color(50, 50, 70),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                modifier = Modifier.padding(top = 15.dp)
            )

            Text(
                text = data.body,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                style = TextStyle(
                    color = Color(150, 150, 150)
                ),
                modifier = Modifier.padding(top = 5.dp, end = 25.dp)
            )
        }

        Text(
            text = dateToString(data.date),
            style = TextStyle(
                color = Color(50, 50, 70),
                fontSize = 16.sp
            ),
            modifier = Modifier.width(60.dp).fillMaxHeight()
        )
    }
}