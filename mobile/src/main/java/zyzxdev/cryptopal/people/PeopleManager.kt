package zyzxdev.cryptopal.people

import android.content.Context

/**
 * Created by aaron on 6/29/2017.
 */
class PeopleManager{
	companion object{
		val people = ArrayList<Person>()

		fun init(ctx: Context){
			val prefs = ctx.getSharedPreferences("people", Context.MODE_PRIVATE)
			people.clear()
			for((address, name) in prefs.all)
				people.add(Person(name as String, address))
		}

		fun save(ctx: Context){
			val prefs = ctx.getSharedPreferences("people", Context.MODE_PRIVATE)
			val edit = prefs.edit()
			for(person in people)
				edit.putString(person.address, person.name)
			edit.apply()
		}

		fun getNameForAddress(address: String): String{
			people
					.filter { it.address == address }
					.forEach { return it.name }
			return address
		}
	}
}