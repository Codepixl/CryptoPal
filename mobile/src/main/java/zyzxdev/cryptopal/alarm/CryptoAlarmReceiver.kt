package zyzxdev.cryptopal.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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

	override fun onReceive(context: Context?, intent: Intent?) {
		Log.v("CryptoPal","Received Alarm")

		newTransactions.clear() //Idk just to make sure I guess

		PeopleManager.init(context!!)
		WalletManager.init(context)
		toUpdate = WalletManager.wallets.size

		if(toUpdate == 0) return

		val notification = NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_qrcode)
				.setContentTitle("Updating Transactions")
				.setContentText("Hold on...")
				.setOngoing(true)
				.build()
		(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(0, notification)

		for(wallet in WalletManager.wallets)
			wallet.refreshTransactions(context, object: TaskCompletedCallback{
				override fun taskCompleted(data: Object) {
					(data as ArrayList<Transaction>).mapTo(newTransactions) { it }
					updatedOne(context)
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
						.setSmallIcon(R.drawable.ic_qrcode)
						.setContentTitle(PeopleManager.getNameForAddress(transaction.address))
						.setContentText(transaction.toString())
						.build()
				(ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(transaction.hash, 1, notification)

				CardManager.addCard(TransactionCard(transaction))
			}

			WalletManager.save()
			CardManager.save()
		}
	}
}