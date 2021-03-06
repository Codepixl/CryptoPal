package zyzxdev.cryptopal.wallet

import org.json.JSONObject
import android.widget.TextView
import android.content.Context
import android.view.ViewGroup
import android.content.Intent
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.widget.BaseAdapter
import android.support.v4.app.ActivityOptionsCompat
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import zyzxdev.cryptopal.util.DownloadTask
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.activity.WalletDetailsActivity
import zyzxdev.cryptopal.util.TaskCompletedCallback
import zyzxdev.cryptopal.view.ExpandableCardView


/**
 * Created by aaron on 6/27/2017.
 */

class Wallet(var name: String, var address: String){
	var balance: Double = 0.0
	var lastUpdated: Long = -1
	val transactions = ArrayList<Transaction>()

	companion object{
		val SATOSHI = 0.00000001
		val SATOSHIMULTIPLIER = 100000000.0
	}

	//Initialize wallet from saved JSON data
	constructor(json: JSONObject): this("",""){
		address = json.getString("address")
		name = json.getString("name")
		balance = json.getDouble("balance")
		lastUpdated = json.getLong("lastUpdated")

		//load transactions
		val transactionsJSON = json.getJSONArray("transactions")
		(0 until transactionsJSON.length()).mapTo(transactions) { Transaction.fromSaved(transactionsJSON.getJSONObject(it)) }
	}

	//Convert wallet to JSON data
	fun toJSON(): JSONObject {
		val obj = JSONObject()
		obj.put("address", address)
		obj.put("name", name)
		obj.put("balance", balance)
		obj.put("lastUpdated", lastUpdated)
		val transactionsJSON = JSONArray()
		for(transaction in transactions)
			transactionsJSON.put(transaction.toJSON())
		obj.put("transactions", transactionsJSON)
		return obj
	}

	//Parse the transactions for this wallet from JSON data
	fun parseTransactions(transactionsJSON: JSONArray){
		transactions.clear()
		(0..transactionsJSON.length()-1).mapTo(transactions) { Transaction(transactionsJSON.getJSONObject(it), this) }
	}

	fun getRoundedBalance(): Double{
		return Math.ceil(balance*100)/100.0
	}

	//Refresh the balance of this wallet
	fun refreshBalance(ctx: Context, callback: TaskCompletedCallback? = null){
		DownloadTask(ctx).setCallback(object: TaskCompletedCallback {
			override fun taskCompleted(data: Any?) {
				try {
					if(data != null)
						balance = (data as String).toLong()* SATOSHI
				}catch(e: NumberFormatException){
					balance = 0.0
				}
				callback?.taskCompleted(data)
			}
		}).execute("https://blockchain.info/q/addressbalance/$address")
	}

	//Also refreshes balance, because that data is included, so why not?
	//Refreshes the last 50 transactions for this wallet.
	//Transactions are parsed in the Transaction class.
	//Transactions are parsed inside of the async call to avoid UI hangups.
	//Callback data is an arrayList of new transactions.
	fun refreshTransactions(ctx: Context, callback: TaskCompletedCallback? = null, save: Boolean = true){
		Log.v("CryptoPal","Downloading Transactions...")

		DownloadTask(ctx).setCallback(object: TaskCompletedCallback {
			override fun taskCompleted(data: Any?) {
				val txHashes = ArrayList<String>()
				transactions.mapTo(txHashes){ it.hash }
				val ret = ArrayList<Transaction>()
				try {
					val jsonobj = JSONObject(data as String)
					balance = jsonobj.getLong("final_balance")* SATOSHI
					parseTransactions(jsonobj.getJSONArray("txs"))

					transactions.filterNotTo(ret) { txHashes.contains(it.hash) }
				}catch(e: JSONException){
					e.printStackTrace()
					Log.v("CryptoPal","Error parsing JSON.")
					callback?.taskCompleted(null)
				}
				lastUpdated = System.currentTimeMillis()
				if(save)
					WalletManager.save()
				callback?.taskCompleted(ret)
			}
		}).execute("https://blockchain.info/rawaddr/$address")
	}

	/**
	 * BaseAdapter for showing Wallets in a list.
	 */
	class WalletAdapter(private val ctx: Context) : BaseAdapter() {
		private val inflater: LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

		override fun getCount(): Int {
			return WalletManager.wallets.size
		}

		override fun getItem(i: Int): Any {
			return WalletManager.wallets[i]
		}

		override fun getItemId(i: Int): Long {
			return i.toLong()
		}

		//Open WalletDetailsActivity on wallet click
		fun click(id: Int, view: View){
			val intent = Intent(ctx, WalletDetailsActivity::class.java)
			intent.putExtra("wallet", id)
			val card = view.findViewById<CardView>(R.id.mainCardView)
			val options = ActivityOptionsCompat.makeScaleUpAnimation(card, 0, 0, card.width, card.height)
			ctx.startActivity(intent, options.toBundle())
		}

		//Inflate the view and stuff
		override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
			val v: View
			if(view == null)
				v = inflater.inflate(R.layout.list_item_wallet, viewGroup, false)
			else
				v = view
			(v.findViewById<TextView>(R.id.walletName) as TextView).text = WalletManager.wallets[i].name
			(v.findViewById<TextView>(R.id.walletAddress) as TextView).text = WalletManager.wallets[i].address
			(v.findViewById<TextView>(R.id.walletBalance) as TextView).text = ctx.getString(R.string.BTC_balance, (Math.round(WalletManager.wallets[i].balance*100)/100.0).toString())
			(v.findViewById<CardView>(R.id.mainCardView) as CardView).setOnClickListener {
				click(i, v)
			}
			return v
		}
	}

}
