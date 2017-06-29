package zyzxdev.cryptopal

import android.app.Dialog
import android.content.Context
import android.content.Intent.getIntent
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView

/**
 * Created by aaron on 6/28/2017.
 */
class QRDialog(ctx: Context, val stringData: String): Dialog(ctx) {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		setContentView(R.layout.dialog_qr)

		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
		window.setBackgroundDrawableResource(android.R.color.transparent)

		(findViewById(R.id.qrImage) as ImageView).setImageBitmap(Util.generateQRCode(stringData))
	}
}