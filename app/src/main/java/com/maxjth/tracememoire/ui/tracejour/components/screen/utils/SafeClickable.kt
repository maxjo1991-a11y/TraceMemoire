package com.maxjth.tracememoire.ui.tracejour.components.screen.utils

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics

/**
 * SAFE CLICKABLE (anti-crash Indication / ripple)
 *
 * But: éviter clickable() qui peut crasher sur les versions récentes
 * quand une Indication "ancienne" (PlatformRipple) est injectée.
 *
 * -> On remplace par pointerInput + detectTapGestures.
 * -> Zéro ripple, zéro indication, zéro crash.
 */
fun Modifier.safeClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    if (!enabled) {
        // On garde juste la sémantique (optionnel) et on ne capte pas les taps.
        this.semantics { role = Role.Button }
    } else {
        this
            .semantics { role = Role.Button }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() }
                )
            }
    }
}