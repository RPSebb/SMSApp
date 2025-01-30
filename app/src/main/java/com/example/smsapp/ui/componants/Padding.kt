package com.example.smsapp.ui.componants

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

data class Padding(val start: Dp = 0.dp, val top: Dp = 0.dp, val end: Dp = 0.dp, val bottom: Dp = 0.dp) {

    var totalVertical: Dp = 0.dp
    var totalHorizontal: Dp = 0.dp

    init {
        calculateTotalPadding()
    }

    constructor(horizontal: Dp = 0.dp, vertical: Dp = 0.dp) : this(
        start = horizontal,
        top = vertical,
        end = horizontal,
        bottom = vertical
    ) {
        calculateTotalPadding()
    }

    constructor(all: Dp = 0.dp) : this(all, all, all, all) {
        calculateTotalPadding()
    }

    constructor(paddingValues: PaddingValues, layoutDirection: LayoutDirection = LayoutDirection.Ltr) : this(
        start = paddingValues.calculateStartPadding(layoutDirection),
        top = paddingValues.calculateTopPadding(),
        end = paddingValues.calculateEndPadding(layoutDirection),
        bottom = paddingValues.calculateBottomPadding()
    ) {
        calculateTotalPadding()
    }

    private fun calculateTotalPadding() {
        this.totalHorizontal = this.start + this.end
        this.totalVertical = this.top + this.bottom
    }

    fun toPaddingValues(): PaddingValues {
        return PaddingValues(start, top, end, bottom)
    }
}