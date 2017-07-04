package zyzxdev.cryptopal.alarm

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.support.v7.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.activity.DeveloperOptionsActivity
import zyzxdev.cryptopal.broadcast.CryptoBroadcastReceiver

/**
 * Created by aaron on 7/1/2017.
 */
class CryptoAlarmManager{
	companion object {

		fun startAlarm(ctx: Context?) {
			val alarmManager = ctx?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
			alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 1000 * 60 * 30, getIntent(ctx))
			Log.v("CryptoPal", "Alarm Started")
		}

		fun cancelAlarm(ctx: Context?, printDebug: Boolean = true) {
			val alarmManager = ctx?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
			alarmManager.cancel(getIntent(ctx))
			if(printDebug)
				Log.v("CryptoPal", "Alarm Canceled")
		}

		private fun getIntent(ctx: Context?): PendingIntent {
			val intent = Intent(ctx, CryptoAlarmReceiver::class.java)
			return PendingIntent.getBroadcast(ctx, 0, intent, 0)
		}
	}
}