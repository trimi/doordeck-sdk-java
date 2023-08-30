package com.doordeck.sdk.ui.nfc


import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.os.Bundle
import android.widget.Toast
import com.doordeck.sdk.R
import com.doordeck.sdk.databinding.ActivityNfcBinding
import com.doordeck.sdk.ui.BaseActivity
import com.doordeck.sdk.ui.unlock.UnlockActivity
import com.doordeck.sdk.ui.unlock.UnlockActivity.Companion.COMING_FROM_NFC


/**
 * Launch the activity to scan the tile using NFC
 */
internal class NFCActivity : BaseActivity(), NFCView {

    private lateinit var nfcPresenter: NFCPresenter

    private lateinit var binding: ActivityNfcBinding

    private var flagWentToBackground = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkNfcEnabled()
        nfcPresenter = NFCPresenter()
        setupListeners()
    }

    private fun setupListeners() {
        binding.tvDismiss.setOnClickListener { finish() }
    }


    private fun checkNfcEnabled() {
        val manager = getSystemService(Context.NFC_SERVICE) as NfcManager
        val adapter = manager.defaultAdapter
        if (adapter != null && !adapter.isEnabled) {
            Toast.makeText(applicationContext, getString(R.string.nfc_not_enabled_message), Toast.LENGTH_LONG).show()
            startActivity(Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS))
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            // We scanned an NFC Tag.
            nfcPresenter.processNFCData(intent)
        }

    }

    override fun onResume() {
        super.onResume()

        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            // Yes, Activity start via Beam...  wonder if we should pass a flag indicating Beam?
            nfcPresenter.processNFCData(intent)
        } else if (flagWentToBackground) {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        nfcPresenter.onStart(this)
    }

    override fun onStop() {
        super.onStop()
        nfcPresenter.onStop()
        // This is also called when we move to another screen, but onResume won't be called as we're leaving this activity
        // Also this won't be triggered when we get asked for a permission
        flagWentToBackground = true
    }


    override fun unlockFromTileId(tileId: String) {
        intent.action = ""
        UnlockActivity.start(this, tileId, comingFrom = COMING_FROM_NFC)
        finish()
    }

    override fun onError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, NFCActivity::class.java)
            starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(starter)
        }
    }

}
