package com.example.smsapp.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smsapp.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(permissions: MultiplePermissionsState) {
    LaunchedEffect(Unit) { permissions.launchMultiplePermissionRequest() }

    val activity = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.permission_screen_title)) }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.permission_screen_description))
            Column(modifier = Modifier.fillMaxSize().weight(1f)) {
                PermissionItem(R.drawable.sms, stringResource(R.string.permission_sms))
                PermissionItem(R.drawable.contacts, stringResource(R.string.permission_contacts))
            }

            Button(
                modifier = Modifier.height(130.dp).width(270.dp).padding(bottom = 50.dp),
                shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp),
                onClick = {
                    if(!permissions.shouldShowRationale) {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", activity.packageName, null)
                        )
                        activity.startActivity(intent)
                    } else { permissions.launchMultiplePermissionRequest() }
                }
            ) {
                Text(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W500,
                    text = when(permissions.shouldShowRationale) {
                        false -> stringResource(R.string.permission_screen_button_settings)
                        true  -> stringResource(R.string.permission_screen_button_request)
                    }
                )
            }
        }
    }
}

@Composable
private fun PermissionItem(resId: Int, text: String) {
    ListItem(
        modifier = Modifier.fillMaxWidth().padding(0.dp).height(50.dp).background(Color.Red),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        leadingContent = {
            Icon(
                painter = painterResource(resId),
                contentDescription = text,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )
        },
        headlineContent = { Text(text) }
    )
}