package com.gradle.apiCalls

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.gradle.models.Doctor
import com.gradle.models.Medication
import com.gradle.models.Patient
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.InternalAPI
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

@OptIn(DelicateCoroutinesApi::class)
class PatientApi {

    private val host: String = "https://remindmed-api-nsjyfltjaa-uk.a.run.app"
    private val nullPatient = Patient("-1", "", "")
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
            ContentType.Application.Json
        }
    }

    fun getPatientbyId(id: String): Patient {
        return try {
            var patient: Patient? = null
            runBlocking {
                launch {
                    patient = client.get("$host/patient?id=$id").body()
                }
            }
            if (patient != null) {
                patient as Patient
            } else {
                nullPatient
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun getPatientbyEmail(email: String): Patient {
        return try {
            var patient: Patient? = null
            runBlocking {
                launch {
                    patient = client.get("$host/patient/email?email=$email").body()
                }
            }
            if (patient != null) {
                patient as Patient
            } else {
                nullPatient
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun getDoctors(pid: String): MutableList<Doctor> {
        return try {
            var doctors: MutableList<Doctor>? = null
            runBlocking {
                launch {
                    doctors = client.get("$host/patient/doctors?pid=$pid").body()
                }
            }
            if (doctors?.isEmpty() == false) {
                doctors as MutableList<Doctor>
            } else {
                mutableListOf<Doctor>()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun getAllPatients(): MutableList<Patient> {
        return try {
            var patients: MutableList<Patient>? = null
            runBlocking {
                launch {
                    patients = client.get("$host/patient/all").body()
                }
            }
            if (patients?.isEmpty() == false) {
                patients as MutableList<Patient>
            } else {
                mutableListOf<Patient>()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    @OptIn(InternalAPI::class)
    fun addPatient(patient: Patient): Boolean {
        return try {
            var success = false
            runBlocking {
                launch {
                    success = client.post("$host/patient/add") {
                        contentType(ContentType.Application.Json)
                        setBody(patient)
                    }.status.isSuccess()
                }
            }
            success
        } catch (e: Exception) {
            throw e
        }
    }

    fun deletePatient(id: String): Boolean {
        return try {
            var success = false
            runBlocking {
                launch {
                    success = client.delete("$host/patient/delete?id=$id").status.isSuccess()
                }
            }
            success
        } catch (e: Exception) {
            throw e
        }
    }

    fun updatePatient(patient: Patient): Boolean {
        return try {
            var success = false
            runBlocking {
                launch {
                    success =
                        client.put("$host/patient/update?id=${patient.pid}&name=${patient.name}&email=${patient.email}").status.isSuccess()
                }
            }
            success
        } catch (e: Exception) {
            throw e
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun addMedication(medication: Medication): Boolean {
        return try {
            var success = false
            runBlocking {
                launch {
                    success = client.post("$host/patient/medicine") {
                        contentType(ContentType.Application.Json)
                        setBody(medication)
                    }.status.isSuccess()
                }
            }
            success
        } catch (e: Exception) {
            throw e
        }
    }

    fun removeMedication(pid: String, mid: String): Boolean {
        return try {
            var success = false
            runBlocking {
                launch {
                    success =
                        client.delete("$host/patient/medicine?pid=$pid&mid=$mid").status.isSuccess()
                }
            }
            success
        } catch (e: Exception) {
            throw e
        }
    }

    fun updateMedication(medication: Medication): Boolean {
        return try {
            var success = false
            runBlocking {
                launch {
                    success = client.put("$host/patient/medicine") {
                        contentType(ContentType.Application.Json)
                        setBody(medication)
                    }.status.isSuccess()
                }
            }
            success
        } catch (e: Exception) {
            throw e
        }
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getMedicines(pid: String): MutableList<Medication> {
        return try {
            var meds = mutableListOf<Medication>()
            runBlocking {
                launch {
                    meds = client.get("$host/patient/medicines?pid=$pid")
                        .body<MutableList<Medication>>()
                }
            }
            meds.ifEmpty {
                mutableListOf<Medication>()
            }
        } catch (e: Exception) {
            throw e
        }
    }
}
