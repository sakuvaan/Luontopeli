package com.example.luontopeli.viewmodel

import android.content.Context
import android.net.Uri
import android.app.Application
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.luontopeli.data.local.AppDatabase
import com.example.luontopeli.data.local.entity.NatureSpot
import com.example.luontopeli.data.remote.firebase.AuthManager
import com.example.luontopeli.data.remote.firebase.FirestoreManager
import com.example.luontopeli.data.remote.firebase.StorageManager
import com.example.luontopeli.data.repository.NatureSpotRepository
import com.example.luontopeli.ml.PlantClassifier
import com.example.luontopeli.ml.ClassificationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel kameranäkymälle (CameraScreen).
 * Hallinnoi kuvien ottamista (CameraX), kasvin tunnistamista (ML Kit)
 * ja luontolöytöjen tallentamista Room-tietokantaan.
 */
class CameraViewModel(application: Application) : AndroidViewModel(application) {

    /** Room-tietokantainstanssi */
    private val db = AppDatabase.getDatabase(application)

    /** NatureSpotRepository hallinnoi luontolöytöjen tallennusta */
    private val repository = NatureSpotRepository(
        dao = db.natureSpotDao(),
        firestoreManager = FirestoreManager(),
        storageManager = StorageManager(),
        authManager = AuthManager()
    )

    /** ML Kit -pohjainen kasvin tunnistaja (viikon 5 lisäys) */
    private val classifier = PlantClassifier()

    /** Otetun kuvan paikallinen tiedostopolku */
    private val _capturedImagePath = MutableStateFlow<String?>(null)
    val capturedImagePath: StateFlow<String?> = _capturedImagePath.asStateFlow()

    /** Latausilmaisin kuvan oton tai tunnistuksen aikana */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** ML Kit -tunnistuksen tulos (viikon 5 lisäys) */
    private val _classificationResult = MutableStateFlow<ClassificationResult?>(null)
    val classificationResult: StateFlow<ClassificationResult?> = _classificationResult.asStateFlow()

    /** Nykyinen GPS-sijainti löydön tallennusta varten */
    var currentLatitude: Double = 0.0
    var currentLongitude: Double = 0.0

    /**
     * Ottaa kuvan ja suorittaa ML Kit -tunnistuksen.
     */
    fun takePhoto(context: Context, imageCapture: ImageCapture) {
        _isLoading.value = true

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val outputDir = File(context.filesDir, "nature_photos").also { it.mkdirs() }
        val outputFile = File(outputDir, "IMG_${timestamp}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    _capturedImagePath.value = outputFile.absolutePath

                    // Käynnistetään ML Kit -tunnistus (viikon 5 päivitys)
                    viewModelScope.launch {
                        try {
                            val uri = Uri.fromFile(outputFile)
                            val result = classifier.classify(uri, context)
                            _classificationResult.value = result
                        } catch (e: Exception) {
                            _classificationResult.value =
                                ClassificationResult.Error(e.message ?: "Tuntematon virhe")
                        }
                        _isLoading.value = false
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    _isLoading.value = false
                }
            }
        )
    }

    /** Tyhjentää tilan uutta kuvaa varten */
    fun clearCapturedImage() {
        _capturedImagePath.value = null
        _classificationResult.value = null
    }

    /**
     * Tallentaa löydön tietokantaan hyödyntäen ML Kit -tulosta (viikon 5 päivitys).
     */
    fun saveCurrentSpot() {
        val imagePath = _capturedImagePath.value ?: return
        viewModelScope.launch {
            val result = _classificationResult.value

            // Luodaan NatureSpot tunnistustuloksen perusteella
            val spot = NatureSpot(
                name = when (result) {
                    is ClassificationResult.Success -> result.label
                    else -> "Luontolöytö"
                },
                latitude = currentLatitude,
                longitude = currentLongitude,
                imageLocalPath = imagePath,
                plantLabel = (result as? ClassificationResult.Success)?.label,
                confidence = (result as? ClassificationResult.Success)?.confidence
            )
            repository.insertSpot(spot)
            clearCapturedImage()
        }
    }

    /** Vapauttaa resurssit (viikon 5 lisäys) */
    override fun onCleared() {
        super.onCleared()
        classifier.close()
    }
}