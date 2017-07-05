package zyzxdev.cryptopal.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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

		//Register notification channel
		if(Build.VERSION.SDK_INT >= 26) {
			val mNotificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			val id = "cryptopal_channel_transactions"
			val name = ctx.getString(R.string.channel_transactions)
			val description = ctx.getString(R.string.channel_transactions_description)
			val importance = NotificationManager.IMPORTANCE_DEFAULT
			val mChannel = NotificationChannel(id, name, importance)
			mChannel.description = description
			mChannel.enableLights(true)
			mChannel.lightColor = ctx.getColor(R.color.colorPrimary)
			mChannel.enableVibration(true)
			mNotificationManager.createNotificationChannel(mChannel)
		}

		val notification = NotificationCompat.Builder(ctx)
				.setSmallIcon(R.drawable.ic_notification_cryptopal)
				.setContentTitle("Updating Transactions")
				.setContentText("Hold on...")
				.setOngoing(true)
				.build()
		(ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(0, notification)

		for(wallet in WalletManager.wallets)
			wallet.refreshTransactions(ctx, object: TaskCompletedCallback{
				override fun taskCompleted(data: Any) {
					(data as ArrayList<*>).mapTo(newTransactions) { it as Transaction }
					updatedOne(ctx)
				}
			}, false)
	}

	fun updatedOne(ctx: Context){
		toUpdate--
		if(toUpdate == 0){
			CardManager.init(ctx)

			(ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(0)
			for(transaction in newTransactions){
				Log.v("CryptoPal", "NEW TRANSACTION "+transaction)

				val notification = NotificationCompat.Builder(ctx)
						.setSmallIcon(R.drawable.ic_notification_cryptopal)
						.setContentTitle(PeopleManager.getNameForAddress(transaction.address))
						.setContentText(transaction.toString())
						.setChannelId("cryptopal_channel_transactions")
						.build()
				(ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(transaction.hash, 1, notification)

				CardManager.addCard(TransactionCard(transaction))
			}

			WalletManager.save()
			CardManager.save()
		}
	}
}