package zyzxdev.cryptopal.activity

import android.Manifest
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.util.TaskCompletedCallback
import zyzxdev.cryptopal.util.Util
import zyzxdev.cryptopal.wallet.WalletManager

class AddWalletActivity : android.support.v7.app.AppCompatActivity() {

	private val SCAN_ADDR = 0
	private val SCAN_PK = 1
	private var currentRequestCode = 0

	override fun onCreate(savedInstanceState: android.os.Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_add_wallet)
		actionBar?.title = getString(R.string.add_wallet)
		supportActionBar?.title = getString(R.string.add_wallet)

		(findViewById(R.id.doneButton) as android.widget.Button).setOnClickListener {
			(findViewById(R.id.doneButton) as android.widget.Button).isEnabled = false
			(findViewById(R.id.loadingSpinner) as android.widget.ProgressBar).visibility = android.view.View.VISIBLE
			val name = (findViewById(R.id.walletName) as android.widget.EditText).text.toString()
			val address = (findViewById(R.id.walletAddress) as android.widget.EditText).text.toString()
			val pk = (findViewById(R.id.walletPK) as android.widget.EditText).text.toString()
			val wallet = zyzxdev.cryptopal.wallet.Wallet(name, address, pk)
			WalletManager.wallets.add(wallet)
			WalletManager.save()
			wallet.refreshTransactions(this, object: TaskCompletedCallback {
				override fun taskCompleted(data: Object) {
					onBackPressed()
				}
			})
		}

		(findViewById(R.id.scanAddr) as android.widget.Button).setOnClickListener {
			scanQR(SCAN_ADDR)
		}

		(findViewById(R.id.scanPK) as android.widget.Button).setOnClickListener {
			scanQR(SCAN_PK)
		}
	}

	private fun scanQR(requestCode: Int){
		Util.requestPermission(Manifest.permission.CAMERA, this, 0)
		currentRequestCode = requestCode
		com.google.zxing.integration.android.IntentIntegrator(this)
				.setDesiredBarcodeFormats(com.google.zxing.integration.android.IntentIntegrator.QR_CODE_TYPES)
				.setOrientationLocked(false)
				.initiateScan()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if(requestCode == com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE) {
			val result: com.google.zxing.integration.android.IntentResult? = com.google.zxing.integration.android.IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
			when (currentRequestCode) {
				SCAN_ADDR -> (findViewById(R.id.walletAddress) as android.widget.EditText).setText(result?.contents)
				SCAN_PK -> (findViewById(R.id.walletPK) as android.widget.EditText).setText(result?.contents)
			}
		}
	}

}
