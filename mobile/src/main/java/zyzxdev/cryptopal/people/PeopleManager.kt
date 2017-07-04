package zyzxdev.cryptopal.people

import android.content.Context
import zyzxdev.cryptopal.wallet.WalletManager

/**
 * Created by aaron on 6/29/2017.
 */
class PeopleManager{
	companion object{
		val people = ArrayList<Person>()
		var inited = false

		fun init(ctx: Context){
			if(inited) return
			inited = true

			val prefs = ctx.getSharedPreferences("people", Context.MODE_PRIVATE)
			people.clear()
			for((address, name) in prefs.all)
				people.add(Person(name as String, address))
		}

		fun save(ctx: Context){
			val prefs = ctx.getSharedPreferences("people", Context.MODE_PRIVATE)
			val edit = prefs.edit()
			edit.clear()
			for(person in people)
				edit.putString(person.address, person.name)
			edit.apply()
		}

		/**
		 * @return The name of a person/wallet corresponding to an address. If there is no corresponding name, it returns the address.
		 */
		fun getNameForAddress(address: String): String{
			people
					.filter { it.address == address }
					.forEach { return it.name }
			WalletManager.wallets
					.filter { it.address == address }
					.forEach { return it.name }
			return address
		}
	}
}