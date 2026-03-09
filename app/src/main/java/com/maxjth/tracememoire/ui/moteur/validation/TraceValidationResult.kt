package com.maxjth.tracememoire.ui.moteur.validation

data class TraceValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)