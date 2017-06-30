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
import java.text.NumberFormat


/**
 * Created by aaron on 6/27/2017.
 */

class Wallet(var name: String, var address: String, var privateKey: String){
	var balance: Double = 0.0
	var lastUpdated: Long = -1
	var transactionsJSON: JSONArray? = null
	val transactions = ArrayList<Transaction>()

	companion object{
		val SATOSHI = 0.00000001
		val SATOSHIMULTIPLIER = 100000000.0
	}

	constructor(json: JSONObject): this("","",""){
		address = json.getString("address")
		privateKey = json.getString("privateKey")
		name = json.getString("name")
		balance = json.getDouble("balance")
		lastUpdated = json.getLong("lastUpdated")
		if(json.has("txs")) {
			transactionsJSON = json.getJSONArray("txs")
			parseTransactions()
		}
	}

	fun toJSON(): JSONObject {
		val obj = JSONObject()
		obj.put("address", address)
		obj.put("name", name)
		obj.put("balance", balance)
		obj.put("privateKey", privateKey)
		obj.put("lastUpdated", lastUpdated)
		if(transactionsJSON != null)
			obj.put("txs", transactionsJSON)
		return obj
	}

	fun parseTransactions(){
		transactions.clear()
		(0..transactionsJSON!!.length()-1).mapTo(transactions) { Transaction(transactionsJSON!!.getJSONObject(it), this) }
		for(trans in transactions) Log.v("CryptoPal", trans.toString())
	}

	fun getRoundedBalance(): Double{
		return Math.ceil(balance*100)/100.0
	}

	fun refreshBalance(ctx: Context, callback: TaskCompletedCallback? = null){
		DownloadTask(ctx).setCallback(object: TaskCompletedCallback {
			override fun taskCompleted(data: Object) {
				try {
					balance = (data as String).toLong()* SATOSHI
				}catch(e: NumberFormatException){
					balance = 0.0
				}
				callback?.taskCompleted(data)
			}
		}).execute("https://blockchain.info/q/addressbalance/$address")
	}

	//Also refreshes balance, because that data is included, so why not?
	fun refreshTransactions(ctx: Context, callback: TaskCompletedCallback? = null){
		Log.v("CryptoPal","Downloading Transactions...")
		DownloadTask(ctx).setCallback(object: TaskCompletedCallback {
			override fun taskCompleted(data: Object) {
				try {
					val jsonobj = JSONObject(data as String)
					balance = jsonobj.getLong("final_balance")* SATOSHI
					transactionsJSON = jsonobj.getJSONArray("txs")
					parseTransactions()
				}catch(e: JSONException){
					e.printStackTrace()
					Log.v("CryptoPal","Error parsing JSON.")
				}
				lastUpdated = System.currentTimeMillis()
				WalletHandler.save()
				callback?.taskCompleted(data)
			}
		}).execute("https://blockchain.info/rawaddr/$address")
	}

	class WalletAdapter(private val ctx: Context) : BaseAdapter() {
		private val inflater: LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

		override fun getCount(): Int {
			return WalletHandler.wallets.size + 1
		}

		override fun getItem(i: Int): Any {
			if(i >= 1)
				return WalletHandler.wallets[i-1]
			else
				return WalletHandler.wallets[0]
		}

		override fun getItemId(i: Int): Long {
			return i.toLong()
		}

		fun click(id: Int, view: View){
			val intent = Intent(ctx, WalletDetailsActivity::class.java)
			intent.putExtra("wallet", id)
			val card = view.findViewById(R.id.mainCardView)
			val options = ActivityOptionsCompat.makeScaleUpAnimation(card, 0, 0, card.width, card.height)
			ctx.startActivity(intent, options.toBundle())
		}

		override fun getView(ia: Int, view: View?, viewGroup: ViewGroup?): View {
			if(ia == 0){
				val v = inflater.inflate(R.layout.list_item_btc_value, viewGroup, false)
				val btcValue = ctx.getSharedPreferences("data", Context.MODE_PRIVATE).getFloat("btcValue", -1f).toDouble()
				if(btcValue != -1.0){
					val formatter = NumberFormat.getCurrencyInstance()
					(v.findViewById(R.id.btcVal) as TextView).text = ctx.getString(R.string.BTC_value, formatter.format(btcValue))
				}else
					(v.findViewById(R.id.btcVal) as TextView).text = ctx.getString(R.string.BTC_value_placeholder)
				return v
			}else{
				val i = ia - 1
				val v = inflater.inflate(R.layout.list_item_wallet, viewGroup, false)
				(v.findViewById(R.id.walletName) as TextView).text = WalletHandler.wallets[i].name
				(v.findViewById(R.id.walletAddress) as TextView).text = WalletHandler.wallets[i].address
				(v.findViewById(R.id.walletBalance) as TextView).text = ctx.getString(R.string.BTC_balance, (Math.round(WalletHandler.wallets[i].balance*100)/100.0).toString())
				(v.findViewById(R.id.mainCardView) as CardView).setOnClickListener {
					click(i, v)
				}
				return v
			}
		}
	}

}
