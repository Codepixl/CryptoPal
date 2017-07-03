package zyzxdev.cryptopal.people

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.activity.AddPersonActivity
import zyzxdev.cryptopal.activity.PersonDetailsActivity

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
			val v: View
			if(view == null)
				v = inflater.inflate(R.layout.list_item_person, viewGroup, false)
			else
				v = view
			(v.findViewById<TextView>(R.id.personName) as TextView).text = PeopleManager.people[i].name
			(v.findViewById<TextView>(R.id.personAddress) as TextView).text = PeopleManager.people[i].address
			(v.findViewById<LinearLayout>(R.id.mainLinearLayout)).setOnClickListener {
				val intent = Intent(ctx, PersonDetailsActivity::class.java)
				intent.putExtra("person", i)
				ctx.startActivity(intent)
			}
			return v
		}
	}
}