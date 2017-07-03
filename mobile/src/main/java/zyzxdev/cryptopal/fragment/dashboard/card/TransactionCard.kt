package zyzxdev.cryptopal.fragment.dashboard.card

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.json.JSONObject
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.activity.WalletDetailsActivity
import zyzxdev.cryptopal.people.PeopleManager
import zyzxdev.cryptopal.util.Util
import zyzxdev.cryptopal.view.ExpandableCardView
import zyzxdev.cryptopal.wallet.Transaction

/**
 * Created by aaron on 7/1/2017.
 */
class TransactionCard: DashboardCard {
	val transaction: Transaction

	constructor(transaction: Transaction){
		this.transaction = transaction
	}

	constructor(json: JSONObject){
		transaction = Transaction.fromSaved(json)
	}

	override fun onCreate(ctx: Context, view: View) {
		populate(transaction, view, ctx, true)
	}

	override fun getLayout(): Int {
		return R.layout.card_transaction
	}

	override fun toJSON(): JSONObject? {
		return transaction.toJSON()
	}

	override fun getTypeName(): String {
		return typeName
	}

	companion object{
		val typeName = "TransactionCard"

		private fun click(v: View){
			val card = v.findViewById<ExpandableCardView>(R.id.mainCardView)
			val icon = v.findViewById<ImageView>(R.id.expandIcon)

			//Handle card collapsing and expanding
			if(card.collapsed){
				val time = v.findViewById<ExpandableCardView>(R.id.mainCardView).expand()
				icon.animate().rotation(180f).duration = time.toLong()
			}else{
				//That fancy math resolves out to 60dp
				val time = v.findViewById<ExpandableCardView>(R.id.mainCardView).collapse((60 * android.content.res.Resources.getSystem().displayMetrics.density).toInt())
				icon.animate().rotation(0f).duration = time.toLong()
			}
		}

		fun populate(transaction: Transaction, view: View, ctx: Context, showWalletName: Boolean = false){
			(view.findViewById<TextView>(R.id.sentReceived) as TextView).text = ctx.getString(
					if(transaction.sent)
						R.string.transaction_sent
					else
						R.string.transaction_received
			)
			(view.findViewById<TextView>(R.id.amount) as TextView).text = ctx.getString(R.string.BTC_balance, WalletDetailsActivity.balanceFormat.format(transaction.amount))
			(view.findViewById<TextView>(R.id.amount) as TextView).setTextColor(ContextCompat.getColor(ctx,
					if(transaction.sent)
						android.R.color.holo_red_dark
					else
						android.R.color.holo_green_dark
			))
			view.findViewById<ImageView>(R.id.transactionIcon).setImageDrawable(ContextCompat.getDrawable(ctx,
					if(transaction.sent)
						R.drawable.ic_send_red_30dp
					else
						R.drawable.ic_receive_green_30dp
			))
			(view.findViewById<TextView>(R.id.date) as TextView).text = Util.Companion.getTimeAgo(transaction.time, ctx)

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

			if(transaction.newBTC)
				stringBuilder.append(ctx.getString(R.string.new_btc)+"\n")

			//Remove last \n from address list
			stringBuilder.setLength(stringBuilder.length-1)

			//Set otherAddress TextView to StringBuilder contents
			(view.findViewById<TextView>(R.id.otherAddress) as TextView).text = stringBuilder.toString()

			//Handle click
			view.findViewById<ExpandableCardView>(R.id.mainCardView).setOnClickListener {
				click(view)
			}

			//Hide wallet name if we should, or populate otherwise
			if(!showWalletName)
				view.findViewById<TextView>(R.id.walletName).visibility = View.GONE
			else
				(view.findViewById<TextView>(R.id.walletName) as TextView).text = PeopleManager.getNameForAddress(transaction.address)
		}
	}
}