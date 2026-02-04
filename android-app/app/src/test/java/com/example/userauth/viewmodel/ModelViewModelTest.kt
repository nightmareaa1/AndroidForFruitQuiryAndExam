package com.example.userauth.viewmodel

import org.junit.Test
import org.junit.Assert.*
import com.example.userauth.data.model.EvaluationModel

class ModelViewModelTest {
    @Test
    fun initialModelLoaded() {
        val vm = ModelViewModel()
        val first = vm.models.value.firstOrNull()
        assertNotNull(first)
        assertEquals(" Mango Model", first?.name)
    }

    @Test
    fun addModelAddsItem() {
        val vm = ModelViewModel()
        val before = vm.models.value.size
        vm.addModel("TestModel", 10)
        val after = vm.models.value.size
        assertEquals(before + 1, after)
        assertTrue(vm.models.value.any { it.name == "TestModel" && it.weight == 10 })
    }

    @Test
    fun deleteModelRemovesItem() {
        val vm = ModelViewModel()
        vm.addModel("Temp", 5)
        val id = vm.models.value.last().id
        val before = vm.models.value.size
        vm.deleteModel(id)
        val after = vm.models.value.size
        assertEquals(before - 1, after)
        assertFalse(vm.models.value.any { it.id == id })
    }
}
