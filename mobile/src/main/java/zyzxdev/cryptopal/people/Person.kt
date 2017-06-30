package zyzxdev.cryptopal.people

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import zyzxdev.cryptopal.R

/**
 * Created by aaron on 6/29/2017.
 */
class Person(var name: String, var address: String){
	class PersonAdapter(private val ctx: Context): BaseAdapter(){
		private val inflater: LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

		override fun getItem(i: Int): Any {
			return PeopleManager.people[i]
		}

		override fun getItemId(i: Int): Long {
			return i.toLong()
		}

		override fun getCount(): Int {
			return PeopleManager.people.size
		}

		override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
			val v = inflater.inflate(R.layout.list_item_person, viewGroup, false)
			(v.findViewById(R.id.personName) as TextView).text = PeopleManager.people[i].name
			(v.findViewById(R.id.personAddress) as TextView).text = PeopleManager.people[i].address
			return v
		}
	}
}