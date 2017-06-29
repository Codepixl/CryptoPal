package zyzxdev.cryptopal

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import java.io.File

class AddWalletActivity : AppCompatActivity() {

	private val SCAN_ADDR = 0
	private val SCAN_PK = 1
	private var currentRequestCode = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_add_wallet)
		actionBar?.title = getString(R.string.add_wallet)
		supportActionBar?.title = getString(R.string.add_wallet)

		(findViewById(R.id.doneButton) as Button).setOnClickListener {
			(findViewById(R.id.doneButton) as Button).isEnabled = false
			(findViewById(R.id.loadingSpinner) as ProgressBar).visibility = View.VISIBLE
			val name = (findViewById(R.id.walletName) as EditText).text.toString()
			val address = (findViewById(R.id.walletAddress) as EditText).text.toString()
			val pk = (findViewById(R.id.walletPK) as EditText).text.toString()
			val wallet = Wallet(name, address, pk)
			wallet.refreshTransactions(this, object: TaskCompletedCallback{
				override fun taskCompleted(data: Object) {
					onBackPressed()
				}
			})
			WalletHandler.wallets.add(wallet)
			WalletHandler.save()
		}

		(findViewById(R.id.scanAddr) as Button).setOnClickListener {
			scanQR(SCAN_ADDR)
		}

		(findViewById(R.id.scanPK) as Button).setOnClickListener {
			scanQR(SCAN_PK)
		}
	}

	private fun scanQR(requestCode: Int){
		currentRequestCode = requestCode
		IntentIntegrator(this)
				.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
				.setOrientationLocked(false)
				.initiateScan()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if(requestCode == IntentIntegrator.REQUEST_CODE) {
			val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
			when (currentRequestCode) {
				SCAN_ADDR -> (findViewById(R.id.walletAddress) as EditText).setText(result?.contents)
				SCAN_PK -> (findViewById(R.id.walletPK) as EditText).setText(result?.contents)
			}
		}
	}

}
