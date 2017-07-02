package zyzxdev.cryptopal.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import zyzxdev.cryptopal.R

class DeveloperOptionsActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_developer_options)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}
}
