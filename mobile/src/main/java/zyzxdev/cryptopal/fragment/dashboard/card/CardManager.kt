package zyzxdev.cryptopal.fragment.dashboard.card

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.support.v7.widget.CardView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ListView
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.json.JSONArray
import org.json.JSONObject
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.util.MultiViewAdapter
import zyzxdev.cryptopal.util.SwipeDismissListViewTouchListener
import zyzxdev.cryptopal.util.SwipeDismissTouchListener
import zyzxdev.cryptopal.view.ExpandableCardView
import zyzxdev.cryptopal.wallet.WalletManager
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset

/**
 * Created by aaron on 7/2/2017.
 */
class CardManager{
	companion object{
		private var ctx: Context? = null
		val cards = ArrayList<DashboardCard>()
		var inited = false

		fun init(ctx: Context): Boolean{
			if(inited) return false
			inited = true

			Companion.ctx = ctx

			cards.clear()
			cards.add(BTCValueCard()) //Always have BTCValueCard at the top

			val cardsFile = File(ctx.filesDir, "cards.json")
			var inp: FileInputStream? = null
			if(cardsFile.exists()){
				try {
					inp = FileInputStream(cardsFile)
					val buf = ByteArray(inp.available())
					inp.read(buf)
					inp.close()
					val json = JSONObject(String(buf, Charset.forName("UTF-8")))
					if (json.has("cards")) {
						val arr = json.getJSONArray("cards")
						(0 until arr.length())
								.map { arr.getJSONObject(it) }
								.forEach {
									val data = it.getJSONObject("data")
									cards.add(when(it.getString("type")){
										BTCValueCard.typeName -> BTCValueCard()
										TransactionCard.typeName -> TransactionCard(data)
										else -> BlankCard()
									})
								}
						return true
					}
					return false
				}catch(e: Exception){
					e.printStackTrace()
					try{
						inp?.close()
					}catch(e: IOException){}
					return false
				}
			}
			return true
		}

		//Use this instead of cards.add, because this adds to the beginning like it should
		fun addCard(card: DashboardCard){
			cards.add(1, card)
			save()
		}

		fun save(): Boolean {
			if (ctx == null) return false
			var out: FileOutputStream? = null
			try {
				val cardsFile = File(ctx!!.filesDir, "cards.json")
				out = FileOutputStream(cardsFile)
				val arr: JSONArray = JSONArray()
				for (card in cards) {
					val cardData = card.toJSON()
					if (cardData != null) {
						val cardJSON = JSONObject()
						cardJSON.put("type", card.getTypeName())
						cardJSON.put("data", cardData)
						arr.put(cardJSON)
					} //Otherwise, it was a BlankCard for some reason.
				}
				val j = JSONObject()
				j.put("cards", arr)
				out.write(j.toString(4).toByteArray(Charset.forName("UTF-8")))
				out.close()
			} catch(e: Exception) {
				e.printStackTrace()
				try {
					out?.close()
				} catch(e: IOException) {
					e.printStackTrace()
				}
				return false
			}
			return true
		}
	}

	class ListViewSwipeDismissHandler: SwipeDismissListViewTouchListener.DismissCallbacks{
		override fun canDismiss(position: Int): Boolean {
			return position != 0
		}

		override fun onDismiss(listView: ListView?, reverseSortedPositions: IntArray?) {
			for(i in reverseSortedPositions!!)
				cards.removeAt(i)
			save()
			(listView?.adapter as MultiViewAdapter).notifyDataSetChanged()
		}
	}


	class CardViewAdapter(ctx: Context, items: List<MultiViewAdapter.MultiViewItem>, val listView: ListView): MultiViewAdapter(ctx, items){
		val touchListener: SwipeDismissListViewTouchListener = SwipeDismissListViewTouchListener(listView, CardManager.ListViewSwipeDismissHandler())
		init{
			listView.setOnTouchListener(touchListener)
			listView.setOnScrollListener(touchListener.makeScrollListener())
		}

		fun dismissAll(){
			listView.smoothScrollToPosition(0)
			Handler().postDelayed({
				(0 until listView.childCount)
						.filter { touchListener.mCallbacks.canDismiss(it) }
						.forEach {
							val view = listView.getChildAt(it)
							view.animate()
									.translationX(view.width*0.75f)
									.alpha(0f)
									.setDuration(250)
									.setStartDelay(it*50L)
									.start()
						}

				Handler().postDelayed({
					cards.clear()
					cards.add(BTCValueCard())
					save()
					(listView.adapter as MultiViewAdapter).notifyDataSetChanged()
				}, 50*listView.childCount+100L)
			}, 250)
		}

		override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
			val v = super.getView(i, view, viewGroup)

			val touchListener = SwipeDismissTouchListener(v, null, object: SwipeDismissTouchListener.DismissCallbacks{
				override fun canDismiss(token: Any?): Boolean {
					return i != 0
				}

				override fun onDismiss(dismissView: View?, token: Any?) {
					cards.removeAt(i)
					save()
					((viewGroup as ListView).adapter as MultiViewAdapter).notifyDataSetChanged()
				}
			})

			(v.findViewById<CardView>(R.id.mainCardView)).setOnTouchListener(touchListener)

			return v
		}
	}
}