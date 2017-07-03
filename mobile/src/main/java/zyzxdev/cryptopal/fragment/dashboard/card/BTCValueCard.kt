package zyzxdev.cryptopal.fragment.dashboard.card

import android.content.Context
import android.view.View
import android.widget.TextView
import org.json.JSONObject
import zyzxdev.cryptopal.R
import java.text.NumberFormat

/**
 * Created by aaron on 7/1/2017.
 */
class BTCValueCard : DashboardCard {
	val isTop = true

	override fun onCreate(ctx: Context, view: View) {
		val btcValue = ctx.getSharedPreferences("data", Context.MODE_PRIVATE).getFloat("btcValue", -1f).toDouble()
		if(btcValue != -1.0){
			val formatter = NumberFormat.getCurrencyInstance()
			(view.findViewById<TextView>(R.id.btcVal) as TextView).text = ctx.getString(R.string.BTC_value, formatter.format(btcValue))
		}else
			(view.findViewById<TextView>(R.id.btcVal) as TextView).text = ctx.getString(R.string.BTC_value_placeholder)
	}

	override fun getLayout(): Int {
		return R.layout.card_btc_value
	}

	override fun toJSON(): JSONObject? {
		return if(isTop) null else JSONObject()
	}

	override fun getTypeName(): String {
		return TransactionCard.typeName
	}

	companion object{
		val typeName = "BTCValueCard"
	}
}