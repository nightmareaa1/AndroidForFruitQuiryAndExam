package com.example.userauth.viewmodel

import org.junit.Test
import org.junit.Assert.*
import com.example.userauth.data.model.ScoreParameter
import com.example.userauth.data.model.SubmissionScore
import java.util.UUID

class ScoreViewModelTest {
    @Test
    fun initialSubmissionHasScores() {
        val vm = ScoreViewModel()
        val sub = vm.submissions.value.firstOrNull()
        assertNotNull(sub)
        assertEquals(3, sub!!.scores.size)
    }

    @Test
    fun updateScoreChangesValue() {
        val vm = ScoreViewModel()
        val sub = vm.submissions.value.first()
        val paramName = sub.scores[0].name
        vm.updateScore(sub.id, paramName, 9)
        val updated = vm.submissions.value.first()
        val p = updated.scores.find { it.name == paramName }
        assertNotNull(p)
        assertEquals(9, p!!.score)
    }
}
