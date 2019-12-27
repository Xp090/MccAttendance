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
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso


import com.xp090.azemaattendance.R
import com.xp090.azemaattendance.data.model.Attendance
import com.xp090.azemaattendance.data.model.CheckActionData
import com.xp090.azemaattendance.data.type.CheckType
import com.xp090.azemaattendance.util.ext.argument
import com.xp090.azemaattendance.util.ext.disableFab
import com.xp090.azemaattendance.util.ext.enableFab
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

const val ARG_CHECK_TYPE = "arg_check_type"
const val REQUEST_IMAGE_CAPTURE = 1

class CheckInOutFragment : Fragment() {
    companion object {
        enum class UIState {
            Disabled,
            NotChecked,
            Checking,
            Checked
        }
    }

    private val checkType: String by argument(ARG_CHECK_TYPE)
    private val checkInOutViewModel: CheckInOutViewModel by viewModel()
    private lateinit var checkTypeKeyword: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val checkIcon: Int by lazy {
        if (checkType == CheckType.CheckIn) R.drawable.ic_checkin else R.drawable.ic_checkout
    }

    private val checkDisabledIcon: Int by lazy {
        if (checkType == CheckType.CheckIn) R.drawable.ic_checkin_disabled else R.drawable.ic_checkout_disabled
    }

    private lateinit var uiState: UIState

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
//        if (checkType == CheckType.CheckIn) {
//            checkTypeKeyword = getString(R.string.in_keyword)
//            checkIcon = R.drawable.ic_checkin
//            checkDisabledIcon = R.drawable.ic_checkin_disabled
//        } else {
//            checkTypeKeyword = getString(R.string.out_keyword)
//            checkIcon = R.drawable.ic_checkout
//            checkDisabledIcon = R.drawable.ic_checkout_disabled
//        }

        checkInOutViewModel.checkEvent.observe(this, androidx.lifecycle.Observer { checkEvent ->
            if (checkEvent != null) {
                if (checkEvent.isLoading) {
                    displayProgress()
                } else {
                    displayNoProgress()
                    if (checkEvent.success != null) {
                        onCheckedState()
                    } else if (checkEvent.error != null) {
                        onCheckingState()
                        displayError(checkEvent.error)
                    }
                }
            }
        })
        initState()
    }

    private fun displayNoProgress() {
        progressBar.visibility = View.GONE
    }

    private fun displayProgress() {
        txtStatus.text = getString(R.string.checking, checkTypeKeyword)
        progressBar.visibility = View.VISIBLE
        fabAction.hide()
        btnCancel.visibility = View.INVISIBLE
    }

    private fun initState() {
        checkInOutViewModel.attendanceData.observe(this, androidx.lifecycle.Observer { attendance:Attendance? ->
            if (checkType == CheckType.CheckIn) {
                when {
                    attendance?.checkIn != null && attendance.checkIn.size > attendance.checkOut?.size ?: 0 -> onCheckedState()
                    checkInOutViewModel.imageFile != null -> onCheckingState()
                    else -> onNotCheckedState()
                }
            } else {
                when {
                    attendance?.checkOut != null  && attendance.checkIn != null &&
                            attendance.checkIn.size == attendance.checkOut.size -> onCheckedState()
                    checkInOutViewModel.imageFile != null -> onCheckingState()
                    attendance?.checkIn != null -> onNotCheckedState()
                    else -> onDisabledState()
                }
            }
        })

    }

    private fun onDisabledState() {
        uiState = Companion.UIState.Disabled
        txtStatus.text = getString(R.string.you_must_check_in_before_checkout)
        fabAction.disableFab()
        imgCheck.setImageResource(checkDisabledIcon)
        fabAction.setImageResource(R.drawable.ic_selfie)
    }

    private fun onNotCheckedState() {
        uiState = Companion.UIState.NotChecked
        txtStatus.text = getString(R.string.you_haven_t_not_checked_in_yet, checkTypeKeyword)
        btnCancel.visibility = View.INVISIBLE
        imgCheck.setImageResource(checkDisabledIcon)
        fabAction.enableFab()
        fabAction.show()
        fabAction.setImageResource(R.drawable.ic_selfie)
        fabAction.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun onCheckingState() {
        uiState = Companion.UIState.Checking
        txtStatus.text = getString(R.string.confirm_chekcing_in_with_image, checkTypeKeyword)
        btnCancel.visibility = View.VISIBLE
//        Picasso.get().load(checkInOutViewModel.imageFile!!).into(imgCheck)
        val photoURI: Uri = FileProvider.getUriForFile(
            this@CheckInOutFragment.activity!!,
            "com.xp090.azemaattendance.fileprovider",
            checkInOutViewModel.imageFile!!
        )
        imgCheck.setImageURI(photoURI)
        fabAction.setImageResource(R.drawable.ic_user)
        fabAction.show()
        fabAction.setOnClickListener {
            launchAsync {
                displayProgress()
                val checkActionData = asyncAwait { obtainLocation() }
                if (checkActionData != null) {
                    checkInOutViewModel.submitCheck(checkActionData)
                } else {
                    displayError(getString(R.string.unable_to_get_location))
                    displayNoProgress()
                }
            }
        }
        btnCancel.setOnClickListener {
            checkInOutViewModel.deleteCachedImage()
            onNotCheckedState()
        }
    }

    private fun onCheckedState() {
        uiState = Companion.UIState.Checked
        txtStatus.text = getString(R.string.you_checked_successfully, checkTypeKeyword)
        txtStatus.setTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        btnCancel.visibility = View.INVISIBLE
        imgCheck.setImageResource(checkIcon)
        fabAction.hide()
    }

    private fun displayError(message: String? = null) {
        val snackbarMessage = message ?: getString(R.string.an_error_occured)
        Snackbar.make(container,snackbarMessage,Snackbar.LENGTH_LONG).show()
    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                checkInOutViewModel.imageFile = try {
                    createImageFile()
                } catch (ex: IOException) {
                    displayError(getString(R.string.unable_to_save_image))
                    null
                }
                // Continue only if the File was successfully created
                checkInOutViewModel.imageFile?.also {
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && checkInOutViewModel.imageFile != null) {
            onCheckingState()
        } else {
           // displayError()
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
    private suspend fun obtainLocation(): CheckActionData? {
        return suspendCancellableCoroutine { cont ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        cont.resume(
                            CheckActionData(
                                checkType,
                                location.latitude,
                                location.longitude
                            )
                        )
                    } else {
                        cont.resume(null)
                    }

                }
                .addOnFailureListener { cont.resume(null) }
        }

    }

}
