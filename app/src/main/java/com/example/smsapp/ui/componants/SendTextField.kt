package com.example.smsapp.ui.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendTextField(text: MutableState<String>, height: Dp = 50.dp, maxLines: Int, paddingValues: PaddingValues, onClick: () -> Unit) {
    val textMeasurer = rememberTextMeasurer()
    val rowPadding = Padding(start = 5.dp, top = 7.dp, bottom = 7.dp)
    val innerTextPadding = Padding(vertical = 10.dp, horizontal = 15.dp)
    var textFieldHeight = height + rowPadding.totalVertical
    var rowHeight = remember { mutableStateOf(textFieldHeight) }
    var roundFactor = rememberSaveable { mutableIntStateOf(50) }
    val _paddingValues = Padding(paddingValues)
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val width = displayMetrics.widthPixels
    val density = LocalDensity.current
    val textStyle = LocalTextStyle.current.copy(
        color = MaterialTheme.colorScheme.onSecondary,
    )
    val iconSize = 40.dp
    // 48.dp is IconButton width
    val innerTextMaxWidth = (width - with(density) { (_paddingValues.totalHorizontal + rowPadding.totalHorizontal + innerTextPadding.totalHorizontal + 48.dp).toPx() }).toInt()
    val lineCount = remember { mutableIntStateOf(1)}
    val iconColor = if(text.value.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Gray
    val focusState = remember { mutableStateOf(false) }
    val showPlaceholder = text.value.isEmpty() && !focusState.value

    // On text.value change
    LaunchedEffect(text.value) {
        val mesure = textMeasurer.measure(
            text = text.value,
            style = textStyle,
            maxLines = maxLines,
            constraints = Constraints.fixedWidth(innerTextMaxWidth)
        )
        lineCount.intValue = (mesure.lineCount - 1).coerceAtLeast(0)
        rowHeight.value = textFieldHeight + (textStyle.lineHeight.value.dp * lineCount.intValue)
        roundFactor.intValue = if(lineCount.intValue > 0) 20 else 50
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .height(rowHeight.value)
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        BasicTextField(
            value = text.value,
            maxLines = maxLines,
            textStyle = textStyle,
            onValueChange = { text.value = it },
            cursorBrush =  SolidColor(MaterialTheme.colorScheme.onSecondary),
            modifier = Modifier
                .weight(1f)
                .padding(rowPadding.toPaddingValues())
                .height(rowHeight.value)
                .onFocusChanged { focusState.value = it.isFocused },
            decorationBox = { innerTextField ->
                Row(
                    Modifier
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            RoundedCornerShape(roundFactor.intValue)
                        )
                        .padding(innerTextPadding.toPaddingValues())
                ) {
                    if(showPlaceholder) { Text("Message") }
                    else { innerTextField() }

                }
            }
        )
        Box(
            modifier = Modifier.align(Alignment.Bottom)
        ) {
            IconButton(
                modifier = Modifier.padding(bottom = 10.dp),
                onClick = onClick,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(iconSize),
                    tint = iconColor
                )
            }
        }
    }
}