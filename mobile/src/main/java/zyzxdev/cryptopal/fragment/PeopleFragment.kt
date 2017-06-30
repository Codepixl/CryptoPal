package zyzxdev.cryptopal.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_people.*
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.activity.AddPersonActivity
import zyzxdev.cryptopal.people.Person

class PeopleFragment : Fragment() {

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		fab.setOnClickListener {
			startActivity(android.content.Intent(context, AddPersonActivity::class.java))
		}

		mainListView.adapter = Person.PersonAdapter(context)
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater!!.inflate(R.layout.fragment_people, container, false)
	}

	override fun onResume() {
		super.onResume()
		(mainListView.adapter as Person.PersonAdapter).notifyDataSetChanged()
	}

}
