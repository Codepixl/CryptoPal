package zyzxdev.cryptopal.activity

import kotlinx.android.synthetic.main.activity_main_tabbed.*
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.fragment.DashboardFragment
import zyzxdev.cryptopal.fragment.PeopleFragment
import zyzxdev.cryptopal.people.PeopleManager
import zyzxdev.cryptopal.util.Animations
import zyzxdev.cryptopal.wallet.WalletHandler

class MainTabbedActivity : android.support.v7.app.AppCompatActivity() {

	val dashboard = DashboardFragment()
	val people = PeopleFragment()
	private var refreshed = false

	private val mOnNavigationItemSelectedListener = android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener { item ->
		when (item.itemId) {
			R.id.navigation_dashboard -> {
				supportFragmentManager
						.beginTransaction()
						.disallowAddToBackStack()
						.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
						.replace(R.id.fragmentHolder, dashboard, "currentFragment")
						.commit()
				supportActionBar?.title = getString(R.string.bar_title_dashboard)
				return@OnNavigationItemSelectedListener true
			}
			R.id.navigation_people -> {
				supportFragmentManager
						.beginTransaction()
						.disallowAddToBackStack()
						.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
						.replace(R.id.fragmentHolder, people, "currentFragment")
						.commit()
				supportActionBar?.title = getString(R.string.bar_title_people)
				return@OnNavigationItemSelectedListener true
			}
		}
		false
	}

	override fun onCreate(savedInstanceState: android.os.Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main_tabbed)

		Animations.init(this)
		PeopleManager.init(this) //It is important that PeopleManager is initialized before WalletHandler so that transactions use people names
		WalletHandler.init(this)

		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
		supportFragmentManager.beginTransaction().disallowAddToBackStack().replace(R.id.fragmentHolder, dashboard, "currentFragment").commitNow()
		supportActionBar?.title = getString(R.string.bar_title_dashboard)
	}

	fun refreshIfNecessary(){
		if(!refreshed)
			(supportFragmentManager.findFragmentByTag("currentFragment") as DashboardFragment).refreshData()
		refreshed = true
	}
}
