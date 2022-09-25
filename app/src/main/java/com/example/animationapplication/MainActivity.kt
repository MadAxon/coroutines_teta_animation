package com.example.animationapplication

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.animationapplication.ui.theme.AnimationApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimationApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Animation()
                }
            }
        }
    }
}

@Composable
fun Animation() {
    val coroutineScope = rememberCoroutineScope()
    val x = remember { Animatable(0f) }
    val greenBoxEndEdge = remember { mutableStateOf(0f) }
    val currentTimeMillis = remember { mutableStateOf(0L) }
    val lastDelta = remember { mutableStateOf(0f) }
    val circleIcon = remember { mutableStateOf(R.drawable.ic_baseline_arrow_forward_24) }

    Box(Modifier.fillMaxSize()) {
        val greenBoxPaddingDp = 20.dp
        val greenBoxPaddingPx = with(LocalDensity.current) { greenBoxPaddingDp.toPx() }
        val whiteCircleSizeDp = 32.dp
        val whiteCircleSizePx = with(LocalDensity.current) { whiteCircleSizeDp.toPx() }
        val whiteCirclePaddingDp = 4.dp
        val whiteCirclePaddingPx = with(LocalDensity.current) { whiteCirclePaddingDp.toPx() }

        Box(
            Modifier
                .fillMaxWidth()
                .size(width = Dp.Unspecified, height = 96.dp)
                .padding(greenBoxPaddingDp)
                .background(
                    MaterialTheme.colors.secondary,
                    CircleShape
                )
                .align(Alignment.Center)
                .onGloballyPositioned {
                    greenBoxEndEdge.value =
                        it.size.width - greenBoxPaddingPx - whiteCircleSizePx - whiteCirclePaddingPx
                }
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp)
                    .align(Alignment.Center)
                    .graphicsLayer(alpha = 1f - x.value / greenBoxEndEdge.value)
            ) {
                Text(
                    text = "Order 1000 rub",
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.White,
                )
                Text(
                    text = "Swipe to confirm",
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.White
                )
            }

            Box(modifier = Modifier
                .offset { IntOffset(x.value.toInt(), 0) }
                .draggable(
                    state = rememberDraggableState { delta ->
                        lastDelta.value = delta
                        val xDelta = x.value + delta
                        if (xDelta < 0 || xDelta > greenBoxEndEdge.value) return@rememberDraggableState
                        coroutineScope.launch {
                            x.snapTo(xDelta)
                        }
                    },
                    orientation = Orientation.Horizontal,
                    onDragStarted = {
                        currentTimeMillis.value = System.currentTimeMillis()
                        circleIcon.value = R.drawable.ic_baseline_arrow_forward_24
                    },
                    onDragStopped = {
                        coroutineScope.launch {
                            val timeDelta = System.currentTimeMillis() - currentTimeMillis.value
                            val duration = minOf(maxOf(timeDelta * 2, 200).toFloat(), greenBoxEndEdge.value - x.value)
                            x.animateTo(
                                targetValue = if (lastDelta.value > 0) greenBoxEndEdge.value else 0f,
                                animationSpec = tween(
                                    durationMillis = duration.toInt(),
                                    delayMillis = 0,
                                    easing = LinearEasing
                                )
                            )
                            if (lastDelta.value > 0) circleIcon.value = R.drawable.ic_baseline_check_24
                        }
                    }
                )
            ) {
                Box(
                    modifier = Modifier
                        .shadow(8.dp, CircleShape)
                        .align(Alignment.CenterStart)
                        .padding(whiteCirclePaddingDp)
                        .background(Color.White, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = circleIcon.value),
                        modifier = Modifier
                            .padding(8.dp)
                            .size(whiteCircleSizeDp),
                        contentDescription = "",
                    )
                }
            }
        }
    }
}
