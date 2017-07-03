package zyzxdev.cryptopal.util

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.support.v4.view.PagerTabStrip
import android.view.SurfaceView
import android.view.View

class NoSwipeViewPager(ctx: Context, attrs: AttributeSet): ViewPager(ctx, attrs) {
	override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
		return false
	}
}