package zyzxdev.cryptopal.wallet

import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.ViewGroup
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.activity.WalletDetailsActivity
import zyzxdev.cryptopal.fragment.dashboard.card.TransactionCard
import zyzxdev.cryptopal.people.PeopleManager
import zyzxdev.cryptopal.util.Util
import zyzxdev.cryptopal.view.ExpandableCardView

class Transaction private constructor(){
	var sent: Boolean = false
	var amount: Double = 0.0
	val otherInputs = ArrayList<String>()
	val otherOutputs = ArrayList<String>()
	var time: Long = 0
	var newBTC = false
	var address:String = ""

	companion object{
		fun fromSaved(json: JSONObject): Transaction{
			val ret = Transaction()

			ret.sent = json.getBoolean("sent")
			ret.amount = json.getDouble("amount")
			ret.time = json.getLong("time")
			ret.newBTC = json.getBoolean("newBTC")
			ret.address = json.getString("address")

			val inputs = json.getJSONArray("otherInputs")
			for(i in 0 until inputs.length())
				ret.otherInputs.add(inputs.getString(i))

			val outputs = json.getJSONArray("otherOutputs")
			for(i in 0 until outputs.length())
				ret.otherOutputs.add(outputs.getString(i))

			return ret
		}
	}

	constructor(json:JSONObject, owner: Wallet): this(){

		//Go over all of the inputs
		val inputs = json.getJSONArray("inputs")
		for(i in 0..inputs.length()-1){
			try {
				val prevOut = inputs
						.getJSONObject(i)
						.getJSONObject("prev_out")

				//If one of the inputs is our address, subtract the amount from the total.
				val inputAddr = prevOut.getString("addr")
				if(inputAddr == owner.address)
					amount -= prevOut.getInt("value")
				else if(!otherInputs.contains(inputAddr)) //Otherwise, add the address to otherInputs.
					otherInputs.add(inputAddr)
			}catch(e: org.json.JSONException){}
		}

		//Go over all of the outputs
		val outputs = json.getJSONArray("out")
		for(i in 0..outputs.length()-1){
			try {
				val out = outputs.getJSONObject(i)
				val outputAddr = out.getString("addr")

				//If one of the outputs is our address, add the amount to the total.
				if (outputAddr == owner.address)
					amount += out.getInt("value")
				else if(!otherOutputs.contains(outputAddr)) //Otherwise, add the address to otherOutputs.
					otherOutputs.add(outputAddr)
			}catch(e: org.json.JSONException){}
		}

		//Get the time of the transaction
		time = json.getLong("time")

		//The amount is in satoshis, so multiply it to get BTCs
		amount *= Wallet.SATOSHI

		//If the inputs involving us are more than the outputs, (aka the amount is < 0),
		//then that means we sent money.
		if(amount < 0)
			sent = true

		//If there are no inputs, then that means new BTC were generated.
		if(!sent && otherInputs.size == 0)
			newBTC = true

		this.address = owner.address
	}

	fun toJSON(): JSONObject{
		val json = JSONObject()

		json.put("sent", sent)
		json.put("amount", amount)
		json.put("time", time)
		json.put("newBTC", newBTC)
		json.put("address", address)

		val inputs = JSONArray()
		for(input in otherInputs)
			inputs.put(input)
		json.put("otherInputs", inputs)

		val outputs = JSONArray()
		for(output in otherOutputs)
			outputs.put(output)
		json.put("otherOutputs", outputs)

		return json
	}

	override fun toString(): String{
		return "${if(sent) "sent" else "received"} $amount BTC"
	}

	/**
	 * A BaseAdapter for transactions to display them.
	 */
	class TransactionAdapter(private val ctx: android.content.Context, val wallet: Wallet) : android.widget.BaseAdapter() {
		private val inflater: android.view.LayoutInflater = ctx.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE) as android.view.LayoutInflater

		override fun getCount(): Int {
			return wallet.transactions.size
		}

		override fun getItem(i: Int): Any {
			return wallet.transactions[i]
		}

		override fun getItemId(i: Int): Long {
			return i.toLong()
		}

		//Inflate the view and set values and stuff
		@SuppressLint("ViewHolder")
		override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
			val v = inflater.inflate(R.layout.card_transaction, viewGroup, false)

			val transaction = wallet.transactions[i]

			TransactionCard.populate(transaction, v, ctx)

			return v
		}
	}
}