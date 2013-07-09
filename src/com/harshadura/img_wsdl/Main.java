package com.harshadura.img_wsdl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Main extends Activity {

	ImageView image;
	String extStorageDirectory;
	Bitmap decodedByte;

	private int iterator = 0;
	Button btnStartProgress;
	ProgressDialog progressBar;
	private int progressBarStatus = 0;
	private Handler progressBarHandler = new Handler();

	private static String SOAP_ACTION = "http://tempuri.org/GetLinkImage";
	private static String NAMESPACE = "http://tempuri.org/";
	private static String METHOD_NAME = "GetLinkImage";
	private static String URL = "http://cypad.dyndns.org/cypadsqm_android/webservices/CypadSQMSync.asmx?WSDL";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button buttonDownload = (Button) findViewById(R.id.downloadImages);
		Button buttonDisplay = (Button) findViewById(R.id.displayImages);

		buttonDownload.setOnClickListener(buttonSaveOnClickListener);
		buttonDisplay.setOnClickListener(buttonDisplayClickListener);
		
		createDirIfNotExists("Cypad");		
		extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString() + "/Cypad/";
		Log.v("path", extStorageDirectory);
	}

	public int doSomeTasks() {

		String[] imageIDs = { "7F810028-769A-71E0-B5B9-CF3CEA14368B",
				"B0300559-24F0-DBD2-CB99-DA191BD8754B",
				"828B2509-55D7-78B3-D8D5-D36F778E43C8",
				"8888C784-EE05-4FBA-471E-9A84A2D112CD",
				"1E60D6F5-E8C0-0E01-5CD5-DA70D6656CFE",
				"09047862-5C2B-367A-2CC5-C22B33CB3BA9",
				"862E2940-5D89-95EA-420E-FD4F9F3D5954",
				"47939822-65F9-AF58-96F8-4A0F607896AE",
				"3AD0346C-F83C-21ED-A80D-41B667D7CB60",
				"5086D463-36C9-8AB6-20C0-F1290EB5FAAF" };

		while (iterator < imageIDs.length) {
			getEncodedImageFromService(imageIDs[iterator]);

			switch (iterator) {
			case 1:
				return 10;
			case 2:
				return 20;
			case 3:
				return 30;
			case 4:
				return 40;
			case 5:
				return 50;
			case 6:
				return 60;
			case 7:
				return 70;
			case 8:
				return 80;
			case 9:
				return 90;
			case 10:
				return 100;
			}
		}

		return 100;
	}

	public static boolean createDirIfNotExists(String path) {
		boolean ret = true;

		File file = new File(Environment.getExternalStorageDirectory(), path);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				Log.e("TravellerLog :: ", "Problem creating Image folder");
				ret = false;
			}
		}
		return ret;
	}

	Button.OnClickListener buttonDisplayClickListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			 Intent intent = new Intent(Main.this, ImageGallery.class);
			 startActivity(intent);
		}
	};

	Button.OnClickListener buttonSaveOnClickListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			progressBar = new ProgressDialog(v.getContext());
			progressBar.setCancelable(true);
			progressBar.setMessage("Retrieving Data...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.setMax(100);
			progressBar.show();
			progressBarStatus = 0;

			new Thread(new Runnable() {
				public void run() {
					while (progressBarStatus < 100) {
						progressBarStatus = doSomeTasks();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						progressBarHandler.post(new Runnable() {
							public void run() {
								progressBar.setProgress(progressBarStatus);
							}
						});
					}
					if (progressBarStatus >= 100) {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						iterator = 0;
						progressBar.dismiss();
					}
				}
			}).start();

		}
	};

	public void getEncodedImageFromService(String imageID) {
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		request.addProperty("ImageId", imageID);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		SoapObject result;
		result = (SoapObject) envelope.bodyIn;

		if (result != null) {
			String encodedImage = result.getProperty(0).toString();
			Log.v("TAG", encodedImage);
			byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
			decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,
					decodedString.length);
			SaveToSDCard();
		}
	}

	public void SaveToSDCard() {
		String imagename = "image" + (iterator + 1) + ".png";
		OutputStream outStream = null;
		File file = new File(extStorageDirectory, imagename);

		try {
			outStream = new FileOutputStream(file);
			decodedByte.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		iterator++;
	}
}
