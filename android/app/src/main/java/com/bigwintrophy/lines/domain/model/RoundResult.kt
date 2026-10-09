package com.bigwintrophy.lines.domain.model

data class RoundResult(
    val levelId: Int,
    val levelTitle: String,
    val trophyName: String,
    val outcome: RoundOutcome,
    val connectedPairs: Int,
    val totalPairs: Int,
    val moves: Int,
    val points: Int,
    val perfectFill: Boolean
) {

    val isVictory: Boolean get() = outcome == RoundOutcome.VICTORY
}
