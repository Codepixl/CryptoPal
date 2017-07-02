package zyzxdev.cryptopal.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * Created by aaron on 7/2/2017.
 */
class CryptoAlarmReceiver: BroadcastReceiver(){
	override fun onReceive(context: Context?, intent: Intent?) {
		Log.v("CryptoPalAlarm", "Alarm Received")
		//And here we will refresh balances and such
	}
}