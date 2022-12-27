package com.example.wirlesstask.ui.fragments.sendFile

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import com.example.wirlesstask.R
import com.example.wirlesstask.core.base.BaseFragment
import com.example.wirlesstask.core.extension.showSnackBar
import com.example.wirlesstask.core.extension.toLog
import com.example.wirlesstask.databinding.FragmentSendFileBinding
import com.example.wirlesstask.domain.client.BluetoothConnectionService
import com.example.wirlesstask.domain.helper.FilePickerHelper
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class SendFileDataFragment : BaseFragment<FragmentSendFileBinding>() {
    private var fileURI: String = ""
    private val args:SendFileDataFragmentArgs by navArgs()
    override fun onFragmentReady() {
        setToolBarConfigs(R.string.choose_a_file_to_send, true)
        binding.fileSelectButton.setOnClickListener { filePicker() }
        binding.fileSelectorSend.setOnClickListener { send() }
    }

    private fun filePicker() {
        val mimeTypes: Array<String> = arrayOf("image/*", "video/*", "application/pdf", "audio/*")

        val intent = Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(Intent.createChooser(intent, "Choose a file"), 111)
    }

    private fun send() {
        if (fileURI == "") {
            Toast.makeText(requireContext(), "Please choose a file first", Toast.LENGTH_SHORT)
                .show()
        } else {
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setTitle("Confirmation")
            alertDialogBuilder.setMessage("Are you sure want to send this file?")
            alertDialogBuilder.setPositiveButton("Send") { _, _ -> checkLessThan5MB(fileURI) }
            alertDialogBuilder.setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "Cancelled the file sending process",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            alertDialogBuilder.show()
        }

    }

    private fun checkLessThan5MB(fileURI: String) {
        val fiveMB = 1024 * 1024 * 5;
        val file = File(fileURI)

        if (file.readBytes().size > fiveMB) {
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setTitle("File too large")
            alertDialogBuilder.setMessage("This file is larger than the 5MB Limit")
            alertDialogBuilder.setPositiveButton("OK") { _, _ ->
                Toast.makeText(requireContext(), "File sending failed", Toast.LENGTH_SHORT).show()
            }
            alertDialogBuilder.show()
        } else {
            val sendingResult = BluetoothConnectionService().startClient(args.device, fileURI)
            if (sendingResult) {
                showSnackBar("Successfully sent the file!")
            } else {
                showSnackBar("Failed to send the file, please try again later.")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val selectedFile = data?.data
                val selectedFilePath = FilePickerHelper.getPath(requireContext(), selectedFile!!)
                selectedFilePath.toString().toLog("onActivityResult")
//                fileInfoNameValue.text = selectedFilePath
                fileURI = selectedFilePath?:""
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                Toast.makeText(requireContext(), "File choosing cancelled", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error while choosing this file",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun subscribeToObservables() {

    }

    override fun onRefreshView() {

    }
}