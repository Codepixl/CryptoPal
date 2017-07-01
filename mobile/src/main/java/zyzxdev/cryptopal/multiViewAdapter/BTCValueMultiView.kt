package zyzxdev.cryptopal.multiViewAdapter

import android.content.Context
import android.view.View
import android.widget.TextView
import zyzxdev.cryptopal.R
import java.text.NumberFormat

/**
 * Created by aaron on 7/1/2017.
 */
class BTCValueMultiView: MultiViewAdapter.MultiViewItem{
	override fun onCreate(ctx: Context, view: View) {
		val btcValue = ctx.getSharedPreferences("data", Context.MODE_PRIVATE).getFloat("btcValue", -1f).toDouble()
		if(btcValue != -1.0){
			val formatter = NumberFormat.getCurrencyInstance()
			(view.findViewById(R.id.btcVal) as TextView).text = ctx.getString(R.string.BTC_value, formatter.format(btcValue))
		}else
			(view.findViewById(R.id.btcVal) as TextView).text = ctx.getString(R.string.BTC_value_placeholder)
	}

	override fun getLayout(): Int {
		return R.layout.list_item_btc_value
	}

}