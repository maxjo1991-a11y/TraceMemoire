
package com.maxjth.tracememoire.ui.tracejour.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.time.LocalTime
import com.maxjth.tracememoire.ui.model.TraceEvent
import com.maxjth.tracememoire.ui.model.TraceEventType

@Composable
fun rememberNowHour(): () -> String {
    return remember {
        {
            val now = LocalTime.now()
            "%02d:%02d".format(now.hour, now.minute)
        }
    }
}

data class RebuiltTags(
    val tags: Set<String>,
    val times: Map<String, String>
)

fun rebuildSelectedTagsFromEvents(events: List<TraceEvent>): RebuiltTags {
    val tags = mutableSetOf<String>()
    val times = mutableMapOf<String, String>()

    events.forEach { e ->
        if (e.type == TraceEventType.TAG_UPDATE) {
            val parts = e.value.split("|")
            if (parts.size >= 2) {
                val action = parts[0]
                val tag = parts.last()
                val time = if (parts.size >= 3) parts[1] else ""

                if (action == "TAG_ON") {
                    tags += tag
                    if (time.isNotBlank()) times[tag] = time
                } else {
                    tags -= tag
                    times -= tag
                }
            }
        }
    }

    return RebuiltTags(tags, times)
}

