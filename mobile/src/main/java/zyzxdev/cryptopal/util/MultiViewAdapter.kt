package zyzxdev.cryptopal.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

/**
 * Created by aaron on 7/1/2017.
 */
class MultiViewAdapter(val ctx: Context, val items: List<MultiViewItem>): BaseAdapter(){
	private val inflater: LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

	override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
		val v = inflater.inflate(items[i].getLayout(), viewGroup, false)
		items[i].onCreate(ctx, v)
		return v
	}

	override fun getItem(i: Int): Any {
		return items[i]
	}

	override fun getItemId(i: Int): Long {
		return i.toLong()
	}

	override fun getCount(): Int {
		return items.size
	}

	interface MultiViewItem{
		fun onCreate(ctx: Context, view: View)
		fun getLayout(): Int
	}
}