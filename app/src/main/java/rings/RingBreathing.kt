package rings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class RingItem(
    val ringKey: String,
    val percent: Int,
    val sizeDp: Dp,
    val ringColor: Color,
    val percentColor: Color,
    val spec: RingBreathSpec = RingBreathSpec()
)

@Composable
fun TraceRingGroup(
    rings: List<RingItem>,
    modifier: Modifier = Modifier,
    spacing: Dp = 12.dp,
    showInnerPercent: Boolean = true
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        rings.forEachIndexed { index, item ->
            TraceRing(
                ringKey = item.ringKey,
                percentText = "${item.percent}%",
                sizeDp = item.sizeDp,
                ringColor = item.ringColor,
                percentColor = item.percentColor,
                spec = item.spec,
                showInnerPercent = showInnerPercent
            )

            if (index < rings.lastIndex) {
                Spacer(Modifier.width(spacing))
            }
        }
    }
}