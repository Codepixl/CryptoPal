package zyzxdev.cryptopal.util

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils

/**
 * Created by aaron on 6/29/2017.
 */
class Animations{
	companion object{
		var fade_in: Animation? = null
		var fade_out: Animation? = null
		private var inited = false

		fun init(ctx: Context){
			if(inited) return
			inited = true

			fade_in = AnimationUtils.loadAnimation(ctx, android.R.anim.fade_in)
			fade_out = AnimationUtils.loadAnimation(ctx, android.R.anim.fade_out)
		}
	}
}