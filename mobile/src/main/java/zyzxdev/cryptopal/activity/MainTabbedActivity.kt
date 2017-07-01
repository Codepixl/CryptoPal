package zyzxdev.cryptopal.activity

import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle

import zyzxdev.cryptopal.R
import android.support.v4.graphics.drawable.DrawableCompat
import android.os.Build
import android.content.res.ColorStateList
import kotlinx.android.synthetic.main.activity_main_tabbed.*
import zyzxdev.cryptopal.fragment.DashboardFragment
import zyzxdev.cryptopal.fragment.WalletsFragment
import zyzxdev.cryptopal.fragment.PeopleFragment
import zyzxdev.cryptopal.fragment.SettingsFragment
import zyzxdev.cryptopal.people.PeopleManager
import zyzxdev.cryptopal.util.Animations
import zyzxdev.cryptopal.wallet.WalletHandler


class MainTabbedActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener{
	private var pagerAdapter: SectionsPagerAdapter? = null
	private var viewPager: ViewPager? = null
	private val refreshed = HashMap<Int, Boolean>()
	private var currentTab = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main_tabbed)

		//Load data
		PeopleManager.init(this)
		WalletHandler.init(this)
		Animations.init(this)

		//Setup tab icon colors to show white when selected and transparent when not
		val colors: ColorStateList
		if (Build.VERSION.SDK_INT >= 23)
			colors = resources.getColorStateList(R.color.bottom_bar, theme)
		else
			colors = resources.getColorStateList(R.color.bottom_bar)
		for (i in 0..tabs.tabCount - 1) {
			val tab = tabs.getTabAt(i)
			var icon = tab?.icon

			if (icon != null) {
				icon = DrawableCompat.wrap(icon)
				DrawableCompat.setTintList(icon!!, colors)
			}
		}

		//Setup toolbar
		val toolbar = findViewById(R.id.toolbar) as Toolbar
		setSupportActionBar(toolbar)

		//Setup viewPager to use the SectionsPagerAdapter
		pagerAdapter = SectionsPagerAdapter(supportFragmentManager)
		viewPager = findViewById(R.id.container) as ViewPager
		viewPager!!.adapter = pagerAdapter

		//Setup listeners for onPageChange and onTabSelected
		viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
		tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
		tabs.addOnTabSelectedListener(this)

		supportActionBar?.title = getString(R.string.bar_title_dashboard)
	}

	inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
		override fun getItem(position: Int): Fragment {
			return when(position){
				0 -> DashboardFragment()
				1 -> WalletsFragment()
				2 -> PeopleFragment()
				3 -> SettingsFragment()
				else -> SettingsFragment()
			}
		}

		override fun getCount(): Int {
			return 4
		}
	}

	fun shouldRefresh(): Boolean{
		val ret = !refreshed.getOrDefault(currentTab, false)
		refreshed.put(currentTab, true)
		return ret
	}

	override fun onTabReselected(tab: TabLayout.Tab?) {}
	override fun onTabUnselected(tab: TabLayout.Tab?) {}
	override fun onTabSelected(tab: TabLayout.Tab?) {
		currentTab = tab!!.position
		supportActionBar?.title = getString(when(tab?.position){
			0 -> R.string.bar_title_dashboard
			1 -> R.string.bar_title_wallets
			2 -> R.string.bar_title_people
			3 -> R.string.bar_title_settings
			else -> R.string.app_name
		})
	}
}
