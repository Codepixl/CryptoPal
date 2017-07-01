package zyzxdev.cryptopal.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_person_details.*
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.people.PeopleManager
import zyzxdev.cryptopal.people.Person
import zyzxdev.cryptopal.util.Util

class PersonDetailsActivity : AppCompatActivity() {

	var person: Person? = null
	var personID: Int = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_person_details)

		//Get personID from intent extra
		personID = intent.getIntExtra("person", 0)
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_person_details, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		when(item?.itemId){
			R.id.menu_item_delete_person -> {
				PeopleManager.people.removeAt(personID)
				PeopleManager.save(this)
				onBackPressed()
			}
			R.id.menu_item_edit_person -> {
				intent = Intent(this, AddPersonActivity::class.java)
				intent.putExtra("editing", personID)
				startActivity(intent)
			}
		}
		return false
	}

	override fun onResume() {
		super.onResume()

		//Get person from ID
		person = PeopleManager.people[personID]

		//Generate QR code bitmap
		qrCode.setImageBitmap(Util.generateQRCode(person!!.address))

		//Set person address text
		personAddress.text = person?.address

		//Set actionbar text
		supportActionBar?.title = person?.name
	}
}
