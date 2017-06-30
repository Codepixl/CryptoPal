package zyzxdev.cryptopal.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import zyzxdev.cryptopal.R
import java.util.*


/**
 * Created by aaron on 6/28/2017.
 */
class Util{
	companion object {
		@Throws(WriterException::class)
		fun generateQRCode(codeText: String): Bitmap {
			val hintMap = Hashtable<EncodeHintType, ErrorCorrectionLevel>()
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H) // H = 30% damage

			val qrCodeWriter = QRCodeWriter()

			val size = 256

			val bitMatrix = qrCodeWriter.encode(codeText, BarcodeFormat.QR_CODE, size, size, hintMap)
			val width = bitMatrix.width
			val bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565)
			for (x in 0..width - 1) {
				for (y in 0..width - 1) {
					bmp.setPixel(y, x, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
				}
			}
			return bmp
		}

		private val SECOND_MILLIS = 1000
		private val MINUTE_MILLIS = 60 * SECOND_MILLIS
		private val HOUR_MILLIS = 60 * MINUTE_MILLIS
		private val DAY_MILLIS = 24 * HOUR_MILLIS


		fun getTimeAgo(time: Long, ctx: Context): String? {
			var time = time
			if (time < 1000000000000L) {
				// if timestamp given in seconds, convert to millis
				time *= 1000
			}

			val now = System.currentTimeMillis()

			val diff = now - time
			if(time < 0){
				return ctx.getString(R.string.never)
			}else if(diff < 0){
				return ctx.getString(R.string.in_the_future)
			}else if (diff < MINUTE_MILLIS) {
				return ctx.getString(R.string.just_now)
			} else if (diff < 2 * MINUTE_MILLIS) {
				return ctx.getString(R.string.a_minute_ago)
			} else if (diff < 50 * MINUTE_MILLIS) {
				return  ctx.getString(R.string.x_minutes_ago, diff / MINUTE_MILLIS)
			} else if (diff < 90 * MINUTE_MILLIS) {
				return ctx.getString(R.string.an_hour_ago)
			} else if (diff < 24 * HOUR_MILLIS) {
				return ctx.getString(R.string.x_hours_ago, diff / HOUR_MILLIS)
			} else if (diff < 48 * HOUR_MILLIS) {
				return ctx.getString(R.string.yesterday)
			} else {
				return ctx.getString(R.string.x_days_ago, diff / DAY_MILLIS)
			}
		}
	}
}