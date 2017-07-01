package zyzxdev.cryptopal.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aaron on 6/28/2017.
 */

public class DownloadTask extends AsyncTask<String, Integer, String>{

	private Context context;
	private PowerManager.WakeLock mWakeLock;
	private TaskCompletedCallback callback;

	public DownloadTask(Context context){
		this.context = context;
	}

	public DownloadTask setCallback(TaskCompletedCallback callback){
		this.callback = callback;
		return this;
	}

	@Override
	protected String doInBackground(String... sUrl){
		InputStream input = null;
		HttpURLConnection connection = null;
		StringBuilder stringBuilder = new StringBuilder();
		try{
			URL url = new URL(sUrl[0]);
			connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(false);
			connection.connect();

			// expect HTTP 200 OK, so we don't mistakenly save error report
			// instead of the file
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
				return "Server returned HTTP " + connection.getResponseCode()
						+ " " + connection.getResponseMessage();
			}

			// this will be useful to display download percentage
			// might be -1: server did not report the length
			int fileLength = connection.getContentLength();

			// download the file
			input = connection.getInputStream();

			byte data[] = new byte[4096];
			long total = 0;
			int count;
			while((count = input.read(data)) != -1){
				// allow canceling with back button
				if(isCancelled()){
					input.close();
					return null;
				}
				total += count;
				// publishing the progress....
				if(fileLength > 0) // only if total length is known
					publishProgress((int) (total * 100 / fileLength));
				stringBuilder.append(new String(data, 0, count, "UTF-8"));
			}
		}catch(Exception e){
			return e.toString();
		}finally{
			try{
				if(input != null)
					input.close();
			}catch(IOException ignored){
			}

			if(connection != null)
				connection.disconnect();
		}

		return stringBuilder.toString();
	}

	@Override
	protected void onPostExecute(String o){
		callback.taskCompleted(o);
	}
}