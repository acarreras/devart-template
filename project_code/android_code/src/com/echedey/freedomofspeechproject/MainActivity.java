package com.echedey.freedomofspeechproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String ENCODING = "UTF-8";

	private static final String postURL = "http://api.twitter.com/1/statuses/update.json";
	private static final String getURL = "http://api.twitter.com/1/statuses/mentions.json"; 

	// Keys & SecretsF
	private static final String consumerKey = "Adk4F9TfoVodl3iLoIcvBg";
	private static final String consumerSecret = "bbVWJ5pQafVA9qXOqaAi8FVXqEzXTNaO3va3RQ15o";
	private static final String accessToken = "1336357784-AII7cXdnszfwkVZ1N5etBSwl4rFseJjwys6nhDg";
	private static final String accessTokenSecret = "zh7wwXeOAAGBgSTNECapGEU7HIHUEu8zEyf39XWoXw";

	// OAuth Header Keys
	private static final String oauth_consumer_key = "oauth_consumer_key";
	private static final String oauth_nonce = "oauth_nonce";
	private static final String oauth_signature = "oauth_signature";
	private static final String oauth_signature_method = "oauth_signature_method";
	private static final String oauth_timestamp = "oauth_timestamp";
	private static final String oauth_token = "oauth_token";
	private static final String oauth_version = "oauth_version";
	private static final String status = "status";

	private EditText tweetEditor;
	private Button postButton;
	private Button sendBTMentionButton;
	TextView tv0;
	TextView tv1;

	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	BluetoothSocket mBluetoothSocket;
	BluetoothDevice mBluetoothDevice;
	private static final int REQUEST_ENABLE_BT = 2;

	InputStream mmInStream = null;
	OutputStream mmOutStream = null;

	ArrayAdapter<String> mArrayAdapter;
	public String Mention;
	
	final Handler mHandler = new Handler();
	 
	//Create Runnable for posting results
	final Runnable mUpdateResults = new Runnable() {
	public void run() {
		updateResultsInUI();
	}
	};
	
	
	public void raiseBT() {

		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}
	
	

	public void connect_and_send_BT(String message) {

		mBluetoothDevice = mBluetoothAdapter.getRemoteDevice("03:1A:09:08:30:06");
		UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		Log.e("Bluetooth Socket", "Opening Bluetooth Socket");

		try {

			mBluetoothSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
			Log.e("Bluetooth Socket", "Bluetooth Socket opened!");
		} catch (IOException e) {
			Log.e("Bluetooth Socket","Bluetooth not available, or insufficient permissions");
			Log.e("Bluetooth Socket", "Bluetooth Exception when opening Socket:", e);
		}

		Log.e("Bluetooth Socket", "Cancelling Discovery");
		mBluetoothAdapter.cancelDiscovery();

		boolean tryconnect = false;

		try {
			mBluetoothSocket.connect();
			
			tryconnect = true;

		} catch (IOException e) {
			
			Log.e("Bluetooth Socket", "Bluetooth connection Exception:", e);

		}
		
		String cosica = "palabra";
		Log.e("Bluetooth Socket", "Trying to send value" + " " + cosica);		
		//byte[] toSend = cosica.getBytes();
		byte[] toSend = message.getBytes();
		boolean sent = false;

		
		try {			
			OutputStream mmOutStream = mBluetoothSocket.getOutputStream();
			mmOutStream.write(toSend);
			sent = true;
			
			
		} catch (IOException e) {
			
			Log.e("Bluetooth Socket", "Exception", e);
			Log.e("Bluetooth Socket", "Could not send char over bluetooth");

		}

		Log.e("Bluetooth Socket", "Value for sent is" + " " + sent);
		
		if (tryconnect == true) {
			
			Log.e("Bluetooth Socket", "Connect on boolean");
			
			
		}
		
		if (sent == true) {
			Log.e("Bluetooth Socket", "CHAR SENT!!");			
		}
		
		Log.e("Bluetooth Socket", "mmInStream is" + " " + mmInStream);
		Log.e("Bluetooth Socket", "mmOutStream is" + " " + mmOutStream);

	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
                
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TableLayout rl = (TableLayout) findViewById(R.id.RelativeLayout1);
		
		tv0 = (TextView) findViewById(R.id.textview0);
		tv1 = (TextView) findViewById(R.id.textview1);

		
		rl.setBackgroundColor(Color.BLACK);
		tv0.setBackgroundColor(Color.WHITE);
		tv1.setBackgroundColor(Color.WHITE);		
		 
		raiseBT();

		sss_get_home_timeline();
		
		tv0.setText("Last twitter mention:");


		postButton = (Button) findViewById(R.id.postButton);
		sendBTMentionButton = (Button) findViewById(R.id.mentionButton);

		tweetEditor = (EditText) findViewById(R.id.tweetEditor);

		tweetEditor.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (tweetEditor.getText().length() == 0) {
					postButton.setEnabled(false);
				} else {
					postButton.setEnabled(true);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void alertUser(String str) {
		new AlertDialog.Builder(this).setMessage(str)
				.setNeutralButton("OK", null).show();
	}
	
	
	private void updateResultsInUI() {
		 
	// Network activity completed.
	setProgressBarIndeterminateVisibility(false);
	tv1.setText(Mention);
	}
	
	
	private void sss_get_home_timeline() {
			
		new Thread() {
			public void run() {
			get_home_timeline();
			mHandler.post(mUpdateResults);
			}
			}.start();
	}
	

	private void get_home_timeline() {

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(getURL);
		String operation = "get";

		try {

			addAuthorizationHeader("nomessage", get, operation);

			HttpResponse response = client.execute(get);
			parseGetResponse(response);
		
		} catch (Exception e) {
			Log.e(MainActivity.class.getName(), "Exception unknown", e);

			Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private void parseGetResponse(HttpResponse response)
			throws IllegalStateException, IOException, JSONException,
			ParseException {
		StringBuilder sb = getResponseBody(response);
		if (response.getStatusLine().getStatusCode() == 200) {
			JSONArray jsArray = new JSONArray(sb.toString());
			if (jsArray.length() == 0) {
				Log.e("twitter","No results retrieving mentions");
				//Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show();
				return;
			}
			//Toast.makeText(this, jsArray.length() + " results(s)",Toast.LENGTH_SHORT).show();
			ArrayList<String> tweets = new ArrayList<String>();
			for (int i = 0; i < jsArray.length(); i++) {
				JSONObject jsObject = jsArray.getJSONObject(i);
				tweets.add(jsObject.getString("created_at") + ": "
						+ jsObject.getString("text"));
			}
			
			Mention = tweets.get(0).toString();
			
			//Toast.makeText(this, "Last mention" + " " + Mention,Toast.LENGTH_LONG).show();
			
			Log.e(MainActivity.class.getName(), "Mentions:" + " " + tweets.toString());
			
		Log.e(MainActivity.class.getName(), "Last mention:" + " " + Mention);
			
			

		}
		else {
			//Toast.makeText(this,"Response Code: "+ response.getStatusLine().getStatusCode() + "\nResponse: " + sb.toString(),Toast.LENGTH_SHORT).show();
			Log.e(MainActivity.class.getName(), "Response Code: "
					+ response.getStatusLine().getStatusCode() + "\nResponse: "
					+ sb.toString());
		}
	}

	
	public void posting(View button) {
		
		sss_post();
		
		
	}
	
	public void sss_post() {
		
		new Thread() {
			public void run() {
			post();
			mHandler.post(mUpdateResults);
			}
			}.start();
	}
	
	
	
	public void post() {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(postURL);
		String operation = "post";
		try {
			String message = tweetEditor.getText().toString();
			addAuthorizationHeader(message, post, operation);
			post.setEntity(new StringEntity(percentEncode(status) + "="
					+ percentEncode(message)));
			HttpResponse response = client.execute(post);
			parsePostResponse(response);
		} catch (Exception e) {
			Log.e(MainActivity.class.getName(), "Exception on post:", e);
			//alertUser("Error: " + e.getMessage());
			//Toast.makeText(this, "Tweet Successful!\nID: " + new JSONObject(sb.toString()).get("id"), Toast.LENGTH_SHORT).show();
			
		}
	}
	
	
	public void postMention(View button, String message) {

		connect_and_send_BT(message);
	}
	
	

	private void addAuthorizationHeader(String tweet, HttpRequestBase request,
			String operation) throws Exception {
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");

		String nonce = base64Encode((UUID.randomUUID().toString()
				.replaceAll("-", "").getBytes()));
		String signatureMethod = "HMAC-SHA1";
		String timeStamp = String.valueOf(new Date().getTime() / 1000);
		String version = "1.0";


		if (operation == "post") {

		 String parameterString = percentEncode(oauth_consumer_key) + "="
		 + percentEncode(consumerKey) + "&" + percentEncode(oauth_nonce)
		 + "=" + percentEncode(nonce) + "&"
		 + percentEncode(oauth_signature_method) + "="
		 + percentEncode(signatureMethod) + "&"
		 + percentEncode(oauth_timestamp) + "="
		 + percentEncode(timeStamp) + "&" + percentEncode(oauth_token)
		 + "=" + percentEncode(accessToken) + "&"
		 + percentEncode(oauth_version) + "=" + percentEncode(version)
		 + "&" + percentEncode(status) + "=" + percentEncode(tweet);
		 // Generate the SignatureBaseString
		 String signatureBaseString = "POST&" + percentEncode(postURL) + "&"
		 + percentEncode(parameterString);
		
		 // Generate the SigningKey
		 String signingKey = percentEncode(consumerSecret) + "&"
		 + percentEncode(accessTokenSecret);
		
		 // Generate HMAC-MD5 signature
		 String signature = generateHmacSHA1(signingKey, signatureBaseString);
		
		 // Build the HTTP Header
		 String oauthHeader = "OAuth " + percentEncode(oauth_consumer_key)
		 + "=\"" + percentEncode(consumerKey) + "\", "
		 + percentEncode(oauth_nonce) + "=\"" + percentEncode(nonce)
		 + "\", " + percentEncode(oauth_signature) + "=\""
		 + percentEncode(signature) + "\", "
		 + percentEncode(oauth_signature_method) + "=\""
		 + percentEncode(signatureMethod) + "\", "
		 + percentEncode(oauth_timestamp) + "=\""
		 + percentEncode(timeStamp) + "\", "
		 + percentEncode(oauth_token) + "=\""
		 + percentEncode(accessToken) + "\", "
		 + percentEncode(oauth_version) + "=\"" + percentEncode(version)
		 + "\"";
		
		 request.addHeader("Authorization", oauthHeader);

		} else if (operation == "get") {

		String parameterString = percentEncode(oauth_consumer_key) + "="
				+ percentEncode(consumerKey) + "&" + percentEncode(oauth_nonce)
				+ "=" + percentEncode(nonce) + "&"
				+ percentEncode(oauth_signature_method) + "="
				+ percentEncode(signatureMethod) + "&"
				+ percentEncode(oauth_timestamp) + "="
				+ percentEncode(timeStamp) + "&" + percentEncode(oauth_token)
				+ "=" + percentEncode(accessToken) + "&"
				+ percentEncode(oauth_version) + "=" + percentEncode(version);
		// + "&" + percentEncode(status) + "=" + percentEncode(tweet);
		// Generate the SignatureBaseString
		String signatureBaseString = "GET&" + percentEncode(getURL) + "&"
				+ percentEncode(parameterString);

		// Generate the SigningKey
		String signingKey = percentEncode(consumerSecret) + "&"
				+ percentEncode(accessTokenSecret);

		// Generate HMAC-MD5 signature
		String signature = generateHmacSHA1(signingKey, signatureBaseString);

		// Build the HTTP Header
		String oauthHeader = "OAuth " + percentEncode(oauth_consumer_key)
				+ "=\"" + percentEncode(consumerKey) + "\", "
				+ percentEncode(oauth_nonce) + "=\"" + percentEncode(nonce)
				+ "\", " + percentEncode(oauth_signature) + "=\""
				+ percentEncode(signature) + "\", "
				+ percentEncode(oauth_signature_method) + "=\""
				+ percentEncode(signatureMethod) + "\", "
				+ percentEncode(oauth_timestamp) + "=\""
				+ percentEncode(timeStamp) + "\", "
				+ percentEncode(oauth_token) + "=\""
				+ percentEncode(accessToken) + "\", "
				+ percentEncode(oauth_version) + "=\"" + percentEncode(version)
				// + "\", "
				//+ percentEncode("count") + "=\"" + percentEncode("1") + "\", "
				+ "\"";

		Log.e(MainActivity.class.getName(), "Generated message:" + oauthHeader);

		request.addHeader("Authorization", oauthHeader);

		 }

	}

	private String generateHmacSHA1(String key, String value) throws Exception {
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(ENCODING),
				"HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(keySpec);
		byte[] result = mac.doFinal(value.getBytes(ENCODING));
		return base64Encode(result);
	}

	private void parsePostResponse(HttpResponse response)
			throws IllegalStateException, IOException, JSONException {
		StringBuilder sb = getResponseBody(response);
		if (response.getStatusLine().getStatusCode() == 200) {
			//alertUser("Tweet Successful!\nID: "	+ new JSONObject(sb.toString()).get("id"));
			Toast.makeText(this, "Tweet Successful!\nID: " + new JSONObject(sb.toString()).get("id"), Toast.LENGTH_SHORT).show();
		}
		// Not OK
		else {
			Log.e(MainActivity.class.getName(), "Response Code: "
					+ response.getStatusLine().getStatusCode() + "\nResponse: "
					+ sb.toString());
			//alertUser("Error Code: " + response.getStatusLine().getStatusCode()	+ "\n" + new JSONObject(sb.toString()).getString("error"));
			Toast.makeText(this, "Error Code: " + response.getStatusLine().getStatusCode()	+ "\n" + new JSONObject(sb.toString()).getString("error"), Toast.LENGTH_SHORT).show();
		}
	}

	private static StringBuilder getResponseBody(HttpResponse response)
			throws IllegalStateException, IOException {
		InputStream is = response.getEntity().getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		return sb;
	}

	private String percentEncode(String s) throws UnsupportedEncodingException {
		// This could be done faster with more hand-crafted code.
		return URLEncoder.encode(s, ENCODING)
		// OAuth encodes some characters differently:
				.replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
	}

	private static String base64Encode(byte[] array) {
		return Base64.encodeToString(array, Base64.NO_WRAP);
	}

}