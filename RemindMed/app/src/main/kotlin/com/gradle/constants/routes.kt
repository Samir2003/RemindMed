package com.gradle.constants

object Routes {
    val HOME = "home"
    val MEDICATION_LIST = "medication-list"
    val PROFILE = "profile"
    val MEDICATION_ENTRY = "medication-entry"
    val PEOPLE_LIST = "people-list"
    val MEDICATION_EDIT = "medication-edit"
    val MEDICATION_INFO = "medication-info"
    val ADD_PATIENT = "add-patient"

    val MEDICATION_INFO_WITH_ARGS = MEDICATION_INFO + "?" +
            "${NavArguments.MEDICATION_INFO.MEDICATION_NAME}={${NavArguments.MEDICATION_INFO.MEDICATION_NAME}}&" +
            "${NavArguments.MEDICATION_INFO.START_DATE}={${NavArguments.MEDICATION_INFO.START_DATE}}&" +
            "${NavArguments.MEDICATION_INFO.END_DATE}={${NavArguments.MEDICATION_INFO.END_DATE}}&" +
            "${NavArguments.MEDICATION_INFO.DOSAGE}={${NavArguments.MEDICATION_INFO.DOSAGE}}" +
            "${NavArguments.MEDICATION_INFO.MID}={${NavArguments.MEDICATION_INFO.MID}}" +
            "${NavArguments.MEDICATION_INFO.NOTES}={${NavArguments.MEDICATION_INFO.NOTES}}"

    val MEDICATION_LIST_WITH_ARGS = MEDICATION_LIST + "?" +
            "${NavArguments.MEDICATION_LIST.PID}={${NavArguments.MEDICATION_LIST.PID}}"

    val MEDICATION_EDIT_WITH_ARGS = MEDICATION_EDIT + "?" +
            "${NavArguments.MEDICATION_EDIT.MEDICATION_ID}={${NavArguments.MEDICATION_EDIT.MEDICATION_ID}}&" +
            "${NavArguments.MEDICATION_EDIT.PID}={${NavArguments.MEDICATION_EDIT.PID}}"

    val MEDICATION_ENTRY_WITH_ARGS = MEDICATION_ENTRY + "?" +
            "${NavArguments.MEDICATION_ENTRY.PID}={${NavArguments.MEDICATION_ENTRY.PID}}"
}

object NavArguments {
    object MEDICATION_INFO {
        val MEDICATION_NAME = "medicationName"
        val START_DATE = "startDate"
        val END_DATE = "endDate"
        val DOSAGE = "dosage"
        val MID = "mid"
        val NOTES = "notes"
    }

    object MEDICATION_EDIT {
        val MEDICATION_ID = "medicationId"
        val PID = "pid"
    }

    object MEDICATION_LIST {
        val PID = "pid"
    }

    object MEDICATION_ENTRY {
        val PID = "pid"
    }
}


