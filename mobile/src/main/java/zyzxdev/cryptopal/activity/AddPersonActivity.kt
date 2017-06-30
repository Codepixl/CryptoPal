package zyzxdev.cryptopal.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_person.*
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.people.PeopleManager
import zyzxdev.cryptopal.people.Person

class AddPersonActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_add_person)
		supportActionBar?.title = getString(R.string.add_person)

		doneButton.setOnClickListener {
			PeopleManager.people.add(Person(personName.text.toString(), personAddress.text.toString()))
			PeopleManager.save(this)
			onBackPressed()
		}

		scanAddr.setOnClickListener {
			scanQR()
		}
	}

	private fun scanQR(){
		com.google.zxing.integration.android.IntentIntegrator(this)
				.setDesiredBarcodeFormats(com.google.zxing.integration.android.IntentIntegrator.QR_CODE_TYPES)
				.setOrientationLocked(false)
				.initiateScan()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if(requestCode == com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE) {
			val result: com.google.zxing.integration.android.IntentResult? = com.google.zxing.integration.android.IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
			personAddress.setText(result?.contents)
		}
	}
}
