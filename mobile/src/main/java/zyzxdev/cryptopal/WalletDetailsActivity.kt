package zyzxdev.cryptopal

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_wallet_details.*
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat

class WalletDetailsActivity : AppCompatActivity() {

	var wallet: Wallet? = null
	var spinner: ProgressBar? = null
	var listView: ListView? = null
	var loading = false

	companion object{
		val balanceFormat = DecimalFormat("#");
		init{
			balanceFormat.maximumFractionDigits = 8
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_wallet_details)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		wallet = WalletHandler.wallets[intent.getIntExtra("wallet", 0)]
		supportActionBar?.title = wallet?.name

		(findViewById(R.id.walletBalance) as TextView).text = "${balanceFormat.format(wallet!!.balance)} BTC"

		val btcValue = getSharedPreferences("data", Context.MODE_PRIVATE).getFloat("btcValue", -1f).toDouble()
		val formatter = NumberFormat.getCurrencyInstance()
		if(btcValue != -1.0)
			(findViewById(R.id.walletBalanceUSD) as TextView).text = "${formatter.format(wallet!!.balance*btcValue)}"
		else
			(findViewById(R.id.walletBalanceUSD) as TextView).text = "$--.--"

		(findViewById(R.id.fab) as FloatingActionButton).setOnClickListener {
			QRDialog(this, wallet!!.address).show()
		}

		(findViewById(R.id.lastUpdated) as TextView).text = "Transactions last updated: ${Util.getTimeAgo(wallet!!.lastUpdated)}"

		spinner = findViewById(R.id.loadingTransactionsSpinner) as ProgressBar

		listView = findViewById(R.id.mainListView) as ListView
		listView?.adapter = Wallet.Transaction.TransactionAdapter(this, wallet!!)
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_wallet_details, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		when(item?.itemId){
			android.R.id.home -> {
				onBackPressed()
				return true
			}
			R.id.menu_item_delete -> {
				WalletHandler.wallets.remove(wallet)
				WalletHandler.save()
				finish()
				return true
			}
			R.id.menu_item_refresh -> {
				updateTransactions()
				return true
			}
			else -> {
				return super.onOptionsItemSelected(item)
			}
		}
	}

	fun updateTransactions(){
		if(loading) return
		loading = true
		spinner?.visibility = View.VISIBLE
		wallet?.refreshTransactions(this, object: TaskCompletedCallback{
			override fun taskCompleted(data: Object) {
				loading = false
				spinner?.visibility = View.GONE
				(listView?.adapter as Wallet.Transaction.TransactionAdapter).notifyDataSetChanged()
				(findViewById(R.id.lastUpdated) as TextView).text = "Transactions last updated: ${Util.getTimeAgo(wallet!!.lastUpdated)}"
				(findViewById(R.id.walletBalance) as TextView).text = "${balanceFormat.format(wallet!!.balance)} BTC"
			}
		})
	}
}
