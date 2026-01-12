package com.example.mukeatlist.ui.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyListState
import kotlin.math.abs

fun Modifier.scrollbar(
    state: LazyListState,
    color: Color = Color.Gray,
    alpha: Float = 0.5f,
    width: Dp = 4.dp,
    padding: Dp = 4.dp,
    // [변경 1] 터치 영역을 20dp -> 48dp로 대폭 확대 (안드로이드 권장 터치 크기)
    touchAreaWidth: Dp = 48.dp
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    var isSelected by remember { mutableStateOf(false) }

    val animatedWidth by animateDpAsState(
        targetValue = if (isSelected) 12.dp else width,
        animationSpec = tween(durationMillis = 200),
        label = "ScrollbarWidth"
    )

    val targetAlpha = if (isSelected) 0.8f else alpha

    this
        .drawWithContent {
            drawContent()

            val firstVisibleElementIndex = state.firstVisibleItemIndex
            val needDrawScrollbar = state.layoutInfo.totalItemsCount > state.layoutInfo.visibleItemsInfo.size

            if (needDrawScrollbar) {
                val elementHeight = size.height / state.layoutInfo.totalItemsCount
                val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
                val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight

                drawRoundRect(
                    color = color.copy(alpha = targetAlpha),
                    topLeft = Offset(size.width - animatedWidth.toPx() - padding.toPx(), scrollbarOffsetY),
                    size = Size(animatedWidth.toPx(), scrollbarHeight),
                    cornerRadius = CornerRadius(x = 4.dp.toPx(), y = 4.dp.toPx())
                )
            }
        }
        .pointerInput(Unit) {
            detectVerticalDragGestures(
                onDragStart = { offset ->
                    // [변경 2] 터치 X 좌표가 오른쪽 끝 영역(48dp) 안에 들어왔는지 확인
                    if (offset.x > size.width - touchAreaWidth.toPx()) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        isSelected = true

                        val totalItems = state.layoutInfo.totalItemsCount
                        if (totalItems == 0) return@detectVerticalDragGestures
                        val scrollRatio = offset.y / size.height
                        val targetIndex = (totalItems * scrollRatio).toInt().coerceIn(0, totalItems - 1)

                        coroutineScope.launch { state.scrollToItem(targetIndex) }
                    }
                },
                onVerticalDrag = { change, _ ->
                    // [변경 3] 일단 잡았으면(isSelected), 손가락이 왼쪽으로 많이 벗어나도 계속 스크롤 유지
                    // (터치 영역 밖으로 나가도 드래그가 끊기지 않게 함)
                    if (isSelected) {
                        val totalItems = state.layoutInfo.totalItemsCount
                        if (totalItems == 0) return@detectVerticalDragGestures

                        val scrollRatio = change.position.y / size.height
                        val targetIndex = (totalItems * scrollRatio).toInt().coerceIn(0, totalItems - 1)

                        coroutineScope.launch { state.scrollToItem(targetIndex) }
                    }
                },
                onDragEnd = { isSelected = false },
                onDragCancel = { isSelected = false }
            )
        }
}