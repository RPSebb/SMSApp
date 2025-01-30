package com.example.smsapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smsapp.models.Conversation
import com.example.smsapp.screens.ConversationScreen
import com.example.smsapp.screens.HomeScreen
import com.example.smsapp.screens.PermissionsScreen
import com.example.smsapp.ui.theme.SmsAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.InternalSerializationApi

val permissionsList : Map<String, List<String>> = mapOf(
    "required" to listOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.RECEIVE_MMS,
        Manifest.permission.READ_CONTACTS
    ),
    "optional" to if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(Manifest.permission.POST_NOTIFICATIONS) else emptyList()
)

@OptIn(ExperimentalPermissionsApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { SmsAppTheme { Router(intent) } }
    }

}

@SuppressLint("RestrictedApi", "PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class, InternalSerializationApi::class)
@Composable
fun Router(intent: Intent) {
    val navController = rememberNavController()
    val requiredPermissions = rememberMultiplePermissionsState(permissionsList.getValue("required"))
    val optionalPermissions = rememberMultiplePermissionsState(permissionsList.getValue("optional"))

    if(!requiredPermissions.allPermissionsGranted) {
        return PermissionsScreen(requiredPermissions)
    }

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (!optionalPermissions.allPermissionsGranted) {
            LaunchedEffect(Unit) { optionalPermissions.launchMultiplePermissionRequest() }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home")       { HomeScreen(onNavigateToMessages = { conversation -> navController.navigate(conversation) }) }
        composable<Conversation> { ConversationScreen(onNavigateUp = { navController.navigateUp() }) }
    }

    LaunchedEffect(Unit) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val conversation : Conversation = intent.getParcelableExtra<Conversation>("destination", Conversation::class.java) ?: return@LaunchedEffect
            navController.navigate(conversation)
        }
    }
}