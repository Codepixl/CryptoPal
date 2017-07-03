package zyzxdev.cryptopal.fragment.dashboard.card

import org.json.JSONObject
import zyzxdev.cryptopal.util.MultiViewAdapter

/**
 * Created by aaron on 7/2/2017.
 */
interface DashboardCard : MultiViewAdapter.MultiViewItem{
	fun toJSON(): JSONObject?
	fun getTypeName(): String
}