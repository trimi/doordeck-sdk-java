package com.doordeck.sdk.ui.qrcode


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.doordeck.sdk.databinding.ActivityQrScanBinding
import com.doordeck.sdk.ui.BaseActivity


/**
 * Launch the activity to scan with the QR Code
 */
internal class QRcodeActivity : BaseActivity() {

    private lateinit var binding: ActivityQrScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.tvDismiss.setOnClickListener { finish() }
    }

    // check if the user has grandted the camera permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == CAMERA)
                binding.qr.start()
        }
    }

    public override fun onStart() {
        super.onStart()
        binding.qr.start()
    }


    public override fun onResume() {
        super.onResume()
        binding.qr.start()
    }

    override fun onPause() {
        super.onPause()
        binding.qr.pause()
    }

    companion object {

        const val CAMERA = 98

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, QRcodeActivity::class.java)
            starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(starter)
        }
    }

}
