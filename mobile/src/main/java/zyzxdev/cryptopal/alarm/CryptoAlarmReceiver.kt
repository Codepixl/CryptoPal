package zyzxdev.cryptopal.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.preference.PreferenceManager
import android.support.v7.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.activity.MainTabbedActivity
import zyzxdev.cryptopal.fragment.dashboard.card.CardManager
import zyzxdev.cryptopal.fragment.dashboard.card.TransactionCard
import zyzxdev.cryptopal.people.PeopleManager
import zyzxdev.cryptopal.util.CryptoNotificationManager
import zyzxdev.cryptopal.util.TaskCompletedCallback
import zyzxdev.cryptopal.wallet.Transaction
import zyzxdev.cryptopal.wallet.WalletManager

/**
 * Created by aaron on 7/2/2017.
 */
class CryptoAlarmReceiver: BroadcastReceiver(){
	var toUpdate = 0
	val newTransactions = ArrayList<Transaction>()

	override fun onReceive(ctx: Context?, intent: Intent?) {
		Log.v("CryptoPal","Received Alarm")

		newTransactions.clear() //Idk just to make sure I guess

		PeopleManager.init(ctx!!)
		WalletManager.init(ctx)
		toUpdate = WalletManager.wallets.size

		if(toUpdate == 0) return

		val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)

		for(wallet in WalletManager.wallets)
			wallet.refreshTransactions(ctx, object: TaskCompletedCallback{
				override fun taskCompleted(data: Any?) {
					if(data != null)
						(data as ArrayList<*>).mapTo(newTransactions) { it as Transaction }
					updatedOne(ctx)
				}
			}, false)
	}

	fun updatedOne(ctx: Context){
		toUpdate--
		if(toUpdate == 0){
			CardManager.init(ctx)

			for(transaction in newTransactions){
				Log.v("CryptoPal", "NEW TRANSACTION "+transaction)

				val notification = CryptoNotificationManager.buildNotification(ctx, CryptoNotificationManager.Channel.TRANSACTIONS)
						.setContentTitle(PeopleManager.getNameForAddress(transaction.address))
						.setContentText(transaction.toString())
						.build()
				CryptoNotificationManager.sendNotification(ctx, notification, tag = transaction.hash, id = 1)

				CardManager.addCard(TransactionCard(transaction))
			}

			WalletManager.save()
			CardManager.save()
		}
	}
}