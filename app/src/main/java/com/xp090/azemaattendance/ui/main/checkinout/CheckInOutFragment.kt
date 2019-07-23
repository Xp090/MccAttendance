package com.xp090.azemaattendance.ui.main.checkinout

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


import com.xp090.azemaattendance.R
import com.xp090.azemaattendance.repository.data.CheckActionData
import com.xp090.azemaattendance.repository.data.CheckType
import com.xp090.azemaattendance.util.ext.argument
import com.xp090.azemaattendance.util.extra.asyncAwait
import com.xp090.azemaattendance.util.extra.launchAsync
import kotlinx.android.synthetic.main.fragment_check_in_out.*
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

const val ARG_CHECK_TYPE = "arg_check_type"
const val REQUEST_IMAGE_CAPTURE = 1

class CheckInOutFragment : Fragment() {

    private val checkType: String by argument(ARG_CHECK_TYPE)
    private val checkInOutViewModel: CheckInOutViewModel by viewModel()
    private lateinit var checkTypeKeyword: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var checkActionData : CheckActionData? = null
    private var imageFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_check_in_out, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        checkTypeKeyword =
            if (checkType == CheckType.CheckIn) getString(R.string.in_keyword) else getString(R.string.out_keyword)
        onInitState()
    }

    private fun onInitState() {
        onNotCheckedState()
    }

    private fun onNotCheckedState() {
        txtStatus.text = getString(R.string.you_haven_t_not_checked_in_yet, checkTypeKeyword)
        btnCheckInOut.text = getString(R.string.check_in_or_out, checkTypeKeyword)
        btnCheckInOut.setOnClickListener {
            launchAsync {
                checkActionData = asyncAwait {obtainLocation()}
                dispatchTakePictureIntent()
            }
        }
    }

    fun onErrorDuringChecking (){
        Toast.makeText(activity!!,"errr",Toast.LENGTH_LONG).show()
    }


//    private suspend fun  captureImage() : ImageProxy? {
//       return suspendCancellableCoroutine {con ->
//            val imageCaptureConfig = ImageCaptureConfig.Builder()
//                .setTargetRotation(Surface.ROTATION_0)
//                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
//                .build()
//            val imageCapture = ImageCapture(imageCaptureConfig)
//            CameraX.bindToLifecycle(
//                this as LifecycleOwner,
//                imageCapture
//            )
//
//            imageCapture.takePicture(object : ImageCapture.OnImageCapturedListener() {
//                override fun onCaptureSuccess(image: ImageProxy?, rotationDegrees: Int) {
//                    con.resume(image)
//                }
//
//                override fun onError(useCaseError: ImageCapture.UseCaseError?, message: String?, cause: Throwable?) {
//                    con.resumeWithException(cause!!)
//                }
//            })
//        }
//
//
//    }
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                imageFile = try {
                    createImageFile()
                } catch (ex: IOException) {
                    onErrorDuringChecking()
                    null
                }
                // Continue only if the File was successfully created
                 imageFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this@CheckInOutFragment.activity!!,
                        "com.xp090.azemaattendance.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && imageFile != null && checkActionData != null) {
            checkInOutViewModel.submitCheck(checkActionData!!,imageFile!!)
        }else{
            onErrorDuringChecking()
        }
    }



    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }


    @SuppressLint("MissingPermission")
    private suspend fun obtainLocation() :CheckActionData? {
        return suspendCancellableCoroutine { cont ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        cont.resume(CheckActionData(checkType,location.latitude,location.longitude))
                    }else{
                        cont.resume(null)
                    }

                }
                .addOnFailureListener { ex -> cont.resume(null)}
        }

    }

}
