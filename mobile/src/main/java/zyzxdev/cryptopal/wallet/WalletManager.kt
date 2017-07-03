package zyzxdev.cryptopal.wallet

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset

/**
 * Created by aaron on 6/27/2017.
 */
class WalletManager {
	companion object{
		private var ctx: Context? = null
		var wallets = ArrayList<Wallet>()

		//Load wallets from saved JSON data
		fun init(ctx: Context): Boolean{
			Companion.ctx = ctx
			val walletFile = File(ctx.filesDir, "wallets.json")
			wallets = ArrayList<Wallet>()
			var inp: FileInputStream? = null
			if(walletFile.exists()){
				try {
					inp = FileInputStream(walletFile)
					val buf = ByteArray(inp.available())
					inp.read(buf)
					inp.close()
					val json = JSONObject(String(buf, Charset.forName("UTF-8")))
					if (json.has("wallets")) {
						val arr = json.getJSONArray("wallets")
						for (i in 0 until arr.length())
							wallets.add(Wallet(arr.getJSONObject(i)))
						return true
					}
					return false
				}catch(e: Exception){
					e.printStackTrace()
					try{
						inp?.close()
					}catch(e: IOException){}
					return false
				}
			}
			return true
		}

		//Save wallets to JSON
		fun save(): Boolean{
			if(ctx == null) return false
			var out: FileOutputStream? = null
			try{
				val walletFile = File(ctx?.filesDir, "wallets.json")
				out = FileOutputStream(walletFile)
				val arr: JSONArray = JSONArray()
				for(w in wallets)
					arr.put(w.toJSON())
				val j = JSONObject()
				j.put("wallets", arr)
				out.write(j.toString(4).toByteArray(Charset.forName("UTF-8")))
				out.close()
			}catch(e: Exception){
				e.printStackTrace()
				try{
					out?.close()
				}catch(e: IOException){
					e.printStackTrace()
				}
				return false
			}
			return true
		}
	}
}