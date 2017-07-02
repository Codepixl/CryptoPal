package zyzxdev.cryptopal.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import zyzxdev.cryptopal.activity.DeveloperOptionsActivity
import zyzxdev.cryptopal.broadcast.CryptoBroadcastReceiver

/**
 * Created by aaron on 7/1/2017.
 */
class CryptoAlarmManager{
	companion object {

		fun startAlarm(ctx: Context?) {
			cancelAlarm(ctx) //Make sure we don't set multiple
			val alarmManager = ctx?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
			alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 1000, getIntent(ctx))
		}

		fun cancelAlarm(ctx: Context?) {
			val alarmManager = ctx?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
			alarmManager.cancel(getIntent(ctx))
		}

		private fun getIntent(ctx: Context?): PendingIntent {
			val intent = Intent("zyzxdev.cryptopal.alarm")
			return PendingIntent.getBroadcast(ctx, 0, intent, 0)
		}
	}
}