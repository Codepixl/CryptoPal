package zyzxdev.cryptopal.wallet

import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.activity.WalletDetailsActivity
import zyzxdev.cryptopal.people.PeopleManager
import zyzxdev.cryptopal.view.ExpandableCardView

class Transaction(json: org.json.JSONObject, owner: Wallet){
	var sent: Boolean = false
	var amount: Double = 0.0
	var other = ""
	var time: Long = 0

	init{
		val inputs = json.getJSONArray("inputs")
		for(i in 0..inputs.length()-1){
			try {
				val prevOut = inputs
						.getJSONObject(i)
						.getJSONObject("prev_out")
				val inputAddr = prevOut.getString("addr")
				if(inputAddr == owner.address)
					amount -= prevOut.getInt("value")
			}catch(e: org.json.JSONException){}
		}

		val outputs = json.getJSONArray("out")
		for(i in 0..outputs.length()-1){
			try {
				val out = outputs.getJSONObject(i)
				val outputAddr = out.getString("addr")
				if (outputAddr == owner.address)
					amount += out.getInt("value")
			}catch(e: org.json.JSONException){}
		}

		time = json.getLong("time")

		amount *= Wallet.SATOSHI
		if(amount < 0)
			sent = true

		if(sent)
			for(i in 0..outputs.length()-1){
				try {
					val out = outputs.getJSONObject(i)
					val outputAddr = out.getString("addr")
					if (outputAddr != owner.address)
						if(!other.contains(outputAddr))
							other += "${PeopleManager.getNameForAddress(outputAddr)}\n"
				}catch(e: org.json.JSONException){}
			}
		else
			for(i in 0..inputs.length()-1){
				try{
					val prevOut = inputs
							.getJSONObject(i)
							.getJSONObject("prev_out")
					val inputAddr = prevOut.getString("addr")
					if(inputAddr != owner.address)
						if(!other.contains(inputAddr))
							other += "${PeopleManager.getNameForAddress(inputAddr)}\n"
				}catch(e: org.json.JSONException){}
			}

		if(other.isBlank())
			other = "NEW_BTC"
		else
			other = other.substring(0, other.length-1)
	}

	override fun toString(): String{
		return "${if(sent) "sent" else "received"} $amount BTC ${if(sent) "to" else "from"} $other"
	}

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
			if(card.collapsed){
				val time = (v.findViewById(R.id.mainCardView) as ExpandableCardView).expand()
				icon.animate().rotation(180f).duration = time.toLong()
			}else{
				val time = (v.findViewById(R.id.mainCardView) as ExpandableCardView).collapse((60 * android.content.res.Resources.getSystem().displayMetrics.density).toInt())
				icon.animate().rotation(0f).duration = time.toLong()
			}
		}

		override fun getView(i: Int, view: android.view.View?, viewGroup: android.view.ViewGroup?): android.view.View {
			val v = inflater.inflate(R.layout.list_item_transaction, viewGroup, false)
			(v.findViewById(R.id.sentReceived) as android.widget.TextView).text = ctx.getString(
					if(wallet.transactions[i].sent)
						R.string.transaction_sent
					else
						R.string.transaction_received
			)
			(v.findViewById(R.id.amount) as android.widget.TextView).text = "${WalletDetailsActivity.Companion.balanceFormat.format(wallet.transactions[i].amount)} BTC"
			(v.findViewById(R.id.amount) as android.widget.TextView).setTextColor(android.support.v4.content.ContextCompat.getColor(ctx,
					if(wallet.transactions[i].sent)
						android.R.color.holo_red_dark
					else
						android.R.color.holo_green_dark
			))
			(v.findViewById(R.id.transactionIcon) as android.widget.ImageView).setImageDrawable(android.support.v4.content.ContextCompat.getDrawable(ctx,
					if(wallet.transactions[i].sent)
						R.drawable.ic_send_red_30dp
					else
						R.drawable.ic_receive_green_30dp
			))
			(v.findViewById(R.id.date) as android.widget.TextView).text = zyzxdev.cryptopal.util.Util.Companion.getTimeAgo(wallet.transactions[i].time, ctx)
			(v.findViewById(R.id.otherAddress) as android.widget.TextView).text =
				if(wallet.transactions[i].other == "NEW_BTC")
					ctx.getString(R.string.new_btc)
				else
					wallet.transactions[i].other
			(v.findViewById(R.id.mainCardView) as android.support.v7.widget.CardView).setOnClickListener {
				click(i, v)
			}

			return v
		}
	}
}