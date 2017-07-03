package zyzxdev.cryptopal.fragment.dashboard.card

import android.content.Context
import android.view.View
import org.json.JSONObject
import zyzxdev.cryptopal.util.MultiViewAdapter

/**
 * Created by aaron on 7/2/2017.
 */
class BlankCard: DashboardCard{

	override fun onCreate(ctx: Context, view: View) {}

	override fun getLayout(): Int {
		return android.R.layout.activity_list_item
	}

	override fun getTypeName(): String {
		return "@NULLCARD@"
	}

	override fun toJSON(): JSONObject? {
		return null
	}
}