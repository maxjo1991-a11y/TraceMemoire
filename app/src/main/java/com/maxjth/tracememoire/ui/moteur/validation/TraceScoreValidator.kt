package com.maxjth.tracememoire.ui.moteur.validation

import android.util.Log
import kotlin.math.abs

object TraceScoreValidator {

    private const val TAG = "TraceScoreValidator"

    fun validatePercent(
        name: String,
        value: Int?,
        errors: MutableList<String>
    ) {
        if (value == null) return

        if (value !in 0..100) {
            errors.add("$name hors limite : $value")
        }
    }

    fun validateDelta(
        name: String,
        previous: Int?,
        current: Int?,
        shownDelta: Int?,
        errors: MutableList<String>
    ) {

        if (previous == null || current == null || shownDelta == null) return

        val expected = current - previous

        if (expected != shownDelta) {
            errors.add(
                "$name delta incohérent | attendu=$expected affiché=$shownDelta"
            )
        }
    }

    fun validateTraceCount(
        previous: Int?,
        current: Int?,
        errors: MutableList<String>
    ) {

        if (previous == null || current == null) return

        if (current < previous) {
            errors.add(
                "Nombre de traces impossible : $previous → $current"
            )
        }
    }

    fun validateJump(
        name: String,
        previous: Int?,
        current: Int?,
        errors: MutableList<String>
    ) {

        if (previous == null || current == null) return

        val diff = abs(current - previous)

        if (diff > 40) {
            errors.add(
                "$name variation inhabituelle : $previous → $current"
            )
        }
    }

    fun validateAll(
        previousJour: Int?,
        currentJour: Int?,
        shownJourDelta: Int?,
        previousTraces: Int?,
        currentTraces: Int?,
        humeur: Int?,
        energie: Int?,
        corps: Int?,
        presence: Int?
    ): TraceValidationResult {

        Log.d(TAG, "Validation exécutée")

        val errors = mutableListOf<String>()

        validatePercent("Humeur", humeur, errors)
        validatePercent("Énergie", energie, errors)
        validatePercent("Corps", corps, errors)
        validatePercent("Présence", presence, errors)

        validatePercent("Score Jour", currentJour, errors)

        validateDelta(
            "Score Jour",
            previousJour,
            currentJour,
            shownJourDelta,
            errors
        )

        validateTraceCount(
            previousTraces,
            currentTraces,
            errors
        )

        validateJump(
            "Score Jour",
            previousJour,
            currentJour,
            errors
        )

        if (errors.isNotEmpty()) {
            errors.forEach {
                Log.e(TAG, it)
            }
        } else {
            Log.d(TAG, "Validation OK")
        }

        return TraceValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
}