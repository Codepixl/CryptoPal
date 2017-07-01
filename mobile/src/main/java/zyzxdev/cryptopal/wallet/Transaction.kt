package zyzxdev.cryptopal.wallet

import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.ViewGroup
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.activity.WalletDetailsActivity
import zyzxdev.cryptopal.people.PeopleManager
import zyzxdev.cryptopal.util.Util
import zyzxdev.cryptopal.view.ExpandableCardView

class Transaction(json: org.json.JSONObject, owner: Wallet){
	var sent: Boolean = false
	var amount: Double = 0.0
	val otherInputs = ArrayList<String>()
	val otherOutputs = ArrayList<String>()
	var time: Long = 0
	var newBTC = false

	init{
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
				else //Otherwise, add the address to otherInputs.
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
				else //Otherwise, add the address to otherOutputs.
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

		fun click(i: Int, v: android.view.View){
			val card = v.findViewById(R.id.mainCardView) as ExpandableCardView
			val icon = v.findViewById(R.id.expandIcon) as android.widget.ImageView
			val r = v.resources

			//Handle card collapsing and expanding
			if(card.collapsed){
				val time = (v.findViewById(R.id.mainCardView) as ExpandableCardView).expand()
				icon.animate().rotation(180f).duration = time.toLong()
			}else{
				//That fancy math resolves out to 60dp
				val time = (v.findViewById(R.id.mainCardView) as ExpandableCardView).collapse((60 * android.content.res.Resources.getSystem().displayMetrics.density).toInt())
				icon.animate().rotation(0f).duration = time.toLong()
			}
		}

		//Inflate the view and set values and stuff
		@SuppressLint("ViewHolder")
		override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
			val v = inflater.inflate(R.layout.list_item_transaction, viewGroup, false)

			val transaction = wallet.transactions[i]

			(v.findViewById(R.id.sentReceived) as TextView).text = ctx.getString(
					if(transaction.sent)
						R.string.transaction_sent
					else
						R.string.transaction_received
			)
			(v.findViewById(R.id.amount) as TextView).text = ctx.getString(R.string.BTC_balance, WalletDetailsActivity.balanceFormat.format(transaction.amount))
			(v.findViewById(R.id.amount) as TextView).setTextColor(ContextCompat.getColor(ctx,
					if(transaction.sent)
						android.R.color.holo_red_dark
					else
						android.R.color.holo_green_dark
			))
			(v.findViewById(R.id.transactionIcon) as ImageView).setImageDrawable(ContextCompat.getDrawable(ctx,
					if(transaction.sent)
						R.drawable.ic_send_red_30dp
					else
						R.drawable.ic_receive_green_30dp
			))
			(v.findViewById(R.id.date) as TextView).text = Util.Companion.getTimeAgo(transaction.time, ctx)

			//Populate other addresses
			val stringBuilder = StringBuilder()
			for(address in if(transaction.sent)
				transaction.otherOutputs
			else
				transaction.otherInputs
			) {
				stringBuilder.append(PeopleManager.getNameForAddress(address))
				stringBuilder.append("\n")
			}

			//Remove last \n from address list
			stringBuilder.setLength(stringBuilder.length-1)

			//Set otherAddress TextView to StringBuilder contents
			(v.findViewById(R.id.otherAddress) as TextView).text = stringBuilder.toString()

			//Handle click
			(v.findViewById(R.id.mainCardView) as CardView).setOnClickListener {
				click(i, v)
			}

			return v
		}
	}
}