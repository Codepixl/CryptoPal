package zyzxdev.cryptopal.broadcast

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import zyzxdev.cryptopal.alarm.CryptoAlarmManager

/**
 * Created by aaron on 7/1/2017.
 */
class CryptoBroadcastReceiver: BroadcastReceiver(){
	override fun onReceive(context: Context?, intent: Intent?) {
		if(intent?.action == Intent.ACTION_BOOT_COMPLETED){
			CryptoAlarmManager.startAlarm(context)
		}
	}
}