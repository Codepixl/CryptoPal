package zyzxdev.cryptopal.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v7.app.NotificationCompat
import zyzxdev.cryptopal.R

/**
 * Created by aaron on 7/8/2017.
 */
object CryptoNotificationManager {
	enum class Channel{
		TRANSACTIONS, SILENT;

		fun getName(): String{
			return when(this){
				TRANSACTIONS -> "cryptopal_channel_transactions"
				SILENT -> "cryptopal_channel_silent"
			}
		}
	}

	private fun registerNotificationChannels(ctx: Context) {
		if (Build.VERSION.SDK_INT >= 26){
			createChannel(ctx,
					Channel.TRANSACTIONS,
					ctx.getString(R.string.channel_transactions),
					ctx.getString(R.string.channel_transactions_description),
					NotificationManager.IMPORTANCE_DEFAULT)
			{ channel ->
				channel.enableVibration(true)
				channel.setShowBadge(true)
			}

			createChannel(ctx,
					Channel.SILENT,
					ctx.getString(R.string.channel_silent),
					ctx.getString(R.string.channel_silent_description),
					NotificationManager.IMPORTANCE_LOW)
		}
	}

	private fun createChannel(ctx: Context, channel: Channel, name: String, description: String, importance: Int, beforeRegister: ((NotificationChannel) -> Unit)? = null): NotificationChannel?{
		if(Build.VERSION.SDK_INT >= 26) {
			val tid = channel.getName()
			val tChannel = NotificationChannel(tid, name, importance)
			tChannel.description = description
			tChannel.name = name
			beforeRegister?.invoke(tChannel)
			val mgr = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			mgr.createNotificationChannel(tChannel)
			return tChannel
		}else
			return null
	}

	fun sendNotification(ctx: Context, notification: Notification, id: Int = 0, tag:String = ""){
		(ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(tag, id, notification)
	}

	fun buildNotification(ctx: Context, channel: Channel): android.support.v4.app.NotificationCompat.Builder{
		registerNotificationChannels(ctx)
		return NotificationCompat.Builder(ctx)
				.setSmallIcon(R.drawable.ic_notification_cryptopal)
				.setChannelId(channel.getName())
	}
}