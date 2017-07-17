package zyzxdev.cryptopal.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_developer_options.*
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.alarm.CryptoAlarmReceiver
import zyzxdev.cryptopal.fragment.dashboard.card.CardManager
import zyzxdev.cryptopal.fragment.dashboard.card.TransactionCard
import zyzxdev.cryptopal.util.CryptoNotificationManager
import zyzxdev.cryptopal.wallet.Transaction
import zyzxdev.cryptopal.wallet.WalletManager

class DeveloperOptionsActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_developer_options)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		addTransactionCard.setOnClickListener {
			CardManager.addCard(TransactionCard(Transaction.makeExample(randomize = true)))
		}

		updateNow.setOnClickListener {
			val intent = Intent(this, CryptoAlarmReceiver::class.java)
			sendBroadcast(intent)
		}

		sendTestNotification.setOnClickListener {
			val notification = CryptoNotificationManager.buildNotification(this, CryptoNotificationManager.Channel.TRANSACTIONS)
					.setContentTitle("Test")
					.setContentText("This is a test")
					.build()
			CryptoNotificationManager.sendNotification(this, notification)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home -> {
				onBackPressed()
				return true
			}
		}

		return super.onOptionsItemSelected(item)
	}
}
