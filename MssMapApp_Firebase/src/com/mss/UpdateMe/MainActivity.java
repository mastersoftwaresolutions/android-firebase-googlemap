package com.mss.UpdateMe;

import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {

	// Google Map
	private GoogleMap googleMap;
	private double latitude;
	private double longitude;
	private EditText textName;
	private ImageButton btnsave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Firebase.setAndroidContext(this);
		textName = (EditText) findViewById(R.id.textName);
		btnsave = (ImageButton) findViewById(R.id.sendButton);
		btnsave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				textName.setVisibility(View.GONE);
				btnsave.setVisibility(View.GONE);
				utils.setPreference("Name", textName.getText().toString(),
						getApplicationContext());
				GPSTracker gps = new GPSTracker(MainActivity.this);
				if (gps.canGetLocation()) {
					latitude = gps.getLatitude();
					longitude = gps.getLongitude();
					MarkerOptions marker = new MarkerOptions().position(
							new LatLng(latitude, longitude)).title(
							"Current Location");
					googleMap.addMarker(marker);
					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(new LatLng(latitude, longitude)).zoom(10)
							.build();

					googleMap.animateCamera(CameraUpdateFactory
							.newCameraPosition(cameraPosition));
					Intent in = new Intent(getApplicationContext(),
							GetMyFootPrint.class);
					startActivity(in);
				} else {
					gps.showSettingsAlert();
				}
			}
		});

		// mFirebaseRef = new Firebase("https://mssmapapp.firebaseIO.com");
		// mFirebaseRef.child("message").setValue(
		// "Do you have data? You'll love Firebase.");
		try {
			getUserEmail();
			initilizeMap();
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			// googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			// googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			// googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			// googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

			// Showing / hiding your current location
			googleMap.setMyLocationEnabled(true);

			// Enable / Disable zooming controls
			googleMap.getUiSettings().setZoomControlsEnabled(false);

			// Enable / Disable my location button
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);

			// Enable / Disable Compass icon
			googleMap.getUiSettings().setCompassEnabled(true);

			// Enable / Disable Rotate gesture
			googleMap.getUiSettings().setRotateGesturesEnabled(true);

			// Enable / Disable zooming functionality
			googleMap.getUiSettings().setZoomGesturesEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	/**
	 * function to load map If map is not created it will create it for you
	 * */
	private void initilizeMap() {

		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to Connect to maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	/*
	 * creating random postion around a location for testing purpose only
	 */
	private double[] createRandLocation(double latitude, double longitude) {

		return new double[] { latitude + ((Math.random() - 0.5) / 500),
				longitude + ((Math.random() - 0.5) / 500),
				150 + ((Math.random() - 0.5) * 10) };
	}

	public void getUserEmail() {
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(this).getAccounts();
		for (Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) {
				String possibleEmail = account.name;
				Toast.makeText(this, possibleEmail, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
