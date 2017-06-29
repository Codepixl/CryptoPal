package zyzxdev.cryptopal

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import android.R.attr.description
import android.widget.TextView
import android.R.attr.name
import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.widget.BaseAdapter
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ImageView
import org.json.JSONArray
import org.json.JSONException


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

	fun toJSON(): JSONObject{
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
		DownloadTask(ctx).setCallback(object: TaskCompletedCallback{
			override fun taskCompleted(data: Object) {
				try {
					balance = (data as String).toLong()*SATOSHI
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
		DownloadTask(ctx).setCallback(object: TaskCompletedCallback{
			override fun taskCompleted(data: Object) {
				try {
					val jsonobj = JSONObject(data as String)
					balance = jsonobj.getLong("final_balance")*SATOSHI
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
			return WalletHandler.wallets.size
		}

		override fun getItem(i: Int): Any {
			return WalletHandler.wallets[i]
		}

		override fun getItemId(i: Int): Long {
			return i.toLong()
		}

		fun click(id: Int, view: View){
			val intent = Intent(ctx, WalletDetailsActivity::class.java)
			intent.putExtra("wallet", id)
			val options = ActivityOptionsCompat.makeSceneTransitionAnimation(ctx as Activity, view.findViewById(R.id.walletBalance), ctx.getString(R.string.transition_balance))
			ctx.startActivity(intent, options.toBundle())
		}

		override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
			val v = inflater.inflate(R.layout.list_item_wallet, viewGroup, false)
			(v.findViewById(R.id.walletName) as TextView).text = WalletHandler.wallets[i].name
			(v.findViewById(R.id.walletAddress) as TextView).text = WalletHandler.wallets[i].address
			(v.findViewById(R.id.walletBalance) as TextView).text = "${(Math.round(WalletHandler.wallets[i].balance*100)/100.0)} BTC"
			(v.findViewById(R.id.mainCardView) as CardView).setOnClickListener {
				click(i, v)
			}
			return v
		}
	}

	class Transaction(json: JSONObject, owner: Wallet){
		var sent: Boolean = false
		var amount: Double = 0.0
		var other = ""
		var time: Long = 0

		init{
			val inputs = json.getJSONArray("inputs")
			for(i in 0..inputs.length()-1){
				val prevOut = inputs
						.getJSONObject(i)
						.getJSONObject("prev_out")
				val inputAddr = prevOut.getString("addr")
				if(inputAddr == owner.address){
					amount -= prevOut.getInt("value")
				}
			}

			val outputs = json.getJSONArray("out")
			for(i in 0..outputs.length()-1){
				try {
					val out = outputs.getJSONObject(i)
					val outputAddr = out.getString("addr")
					if (outputAddr == owner.address)
						amount += out.getInt("value")
					else
						other = outputAddr
				}catch(e: JSONException){}
			}

			time = json.getLong("time")

			amount *= SATOSHI
			if(amount < 0)
				sent = true
		}

		override fun toString(): String{
			return "${if(sent) "sent" else "received"} $amount BTC ${if(sent) "to" else "from"} $other"
		}

		class TransactionAdapter(private val ctx: Context, val wallet: Wallet) : BaseAdapter() {
			private val inflater: LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

			override fun getCount(): Int {
				return wallet.transactions.size
			}

			override fun getItem(i: Int): Any {
				return wallet.transactions[i]
			}

			override fun getItemId(i: Int): Long {
				return i.toLong()
			}

			override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
				val v = inflater.inflate(R.layout.list_item_transaction, viewGroup, false)
				(v.findViewById(R.id.sentReceived) as TextView).text = ctx.getString(
						if(wallet.transactions[i].sent)
							R.string.transaction_sent
						else
							R.string.transaction_received
				)
				(v.findViewById(R.id.amount) as TextView).text = "${WalletDetailsActivity.balanceFormat.format(wallet.transactions[i].amount)} BTC"
				(v.findViewById(R.id.amount) as TextView).setTextColor(ContextCompat.getColor(ctx,
						if(wallet.transactions[i].sent)
							android.R.color.holo_red_dark
						else
							android.R.color.holo_green_dark
				))
				(v.findViewById(R.id.transactionIcon) as ImageView).setImageDrawable(ContextCompat.getDrawable(ctx,
						if(wallet.transactions[i].sent)
							R.drawable.ic_send_red_30dp
						else
							R.drawable.ic_receive_green_30dp
				))
				(v.findViewById(R.id.date) as TextView).text = Util.getTimeAgo(wallet.transactions[i].time)
				return v
			}
		}
	}
}
