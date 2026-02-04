package com.example.userauth.viewmodel

import org.junit.Test
import org.junit.Assert.*
import com.example.userauth.data.model.SubmissionScore
import com.example.userauth.data.model.ScoreParameter
import com.example.userauth.data.model.ScoreParameter as SP

class DataDisplayViewModelTest {
    @Test
    fun initialSubmissionsAverageAndParameterAverages() {
        val vm = DataDisplayViewModel(ScoreViewModel())
        val subs = vm.allSubmissionsAverages()
        assertEquals(1, subs.size)
        assertEquals("选手A", subs[0].contestant)
        assertEquals("作品A", subs[0].title)
        // Each score is 5, 3 parameters -> average = 5.0
        assertEquals(5.0f, subs[0].average, 0.001f)
        val params = vm.parameterAverages()
        assertEquals(3, params.size)
        assertEquals("创新性", params[0].name)
        assertEquals(5.0f, params[0].average, 0.001f)
    }
}
