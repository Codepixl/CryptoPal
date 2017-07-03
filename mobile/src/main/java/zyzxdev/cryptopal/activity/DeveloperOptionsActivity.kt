package zyzxdev.cryptopal.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_developer_options.*
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.fragment.dashboard.card.CardManager
import zyzxdev.cryptopal.fragment.dashboard.card.TransactionCard
import zyzxdev.cryptopal.wallet.WalletManager

class DeveloperOptionsActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_developer_options)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		addTransactionCard.setOnClickListener {
			if(WalletManager.wallets.size > 0 && WalletManager.wallets[0].transactions.size > 0) {
				CardManager.addCard(TransactionCard(WalletManager.wallets[0].transactions[0]))
				Toast.makeText(this, "Added Card.", Toast.LENGTH_SHORT).show()
			}else
				Toast.makeText(this, "Couldn't add card.", Toast.LENGTH_SHORT).show()
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
