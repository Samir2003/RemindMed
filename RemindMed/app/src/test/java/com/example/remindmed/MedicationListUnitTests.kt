package com.example.remindmed

import com.gradle.controller.MedicationListController
import com.gradle.models.Medication
import com.gradle.models.MedicationList
import com.gradle.models.Patient
import com.gradle.ui.views.shared.MedicationListViewEvent
import com.gradle.ui.views.shared.MedicationListViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

internal class MedicationListUnitTests {
    private val model : MedicationList = MedicationList(emptyList<Medication>(), Patient())

    @Test
    fun viewModelTests() {
        val viewModel = MedicationListViewModel(model)

        val testP = Patient("1", "Gen", "g@gmail.com")
        model.patient = testP
        assertEquals(testP, viewModel.patient.value)
    }

    @Test
    fun controllerTests() {
        val controller = MedicationListController(model)

        val currMeds = model.medicationList
        controller.invoke(MedicationListViewEvent.MedicationRemove, "DNE")
        assertEquals(currMeds, model.medicationList)
    }
}