package com.mss.UpdateMe;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends Activity {
	private GoogleMap googleMap;
	private double latitude;
	private double longitude;
	private EditText textName;
	private ImageButton btnsave;
	private ArrayList<Map<String, Object>> allData;
	private GMapV2Direction md;
	protected ArrayList<LatLng> points;
	private ImageButton locateFriend;
	private ArrayList<String> allUsers = null;
	private ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Firebase.setAndroidContext(this);
		inItUi();
		allData = new ArrayList<Map<String, Object>>();
		getAllUsersList();

		btnsave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				progress.show();
				getMyPrevousData();
				showPathOnMap();
				textName.setVisibility(View.GONE);
				btnsave.setVisibility(View.GONE);
				locateFriend.setVisibility(View.VISIBLE);
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
					// Intent in = new Intent(getApplicationContext(),
					// GetMyFootPrint.class);
					// startActivity(in);
				} else {
					gps.showSettingsAlert();
				}
			}
		});
		try {
			initilizeMap();
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setZoomControlsEnabled(false);
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);
			googleMap.getUiSettings().setCompassEnabled(true);
			googleMap.getUiSettings().setRotateGesturesEnabled(true);
			googleMap.getUiSettings().setZoomGesturesEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author acer
	 */
	private void inItUi() {
		progress = new ProgressDialog(this);
		progress.setMessage("Loading location data ");
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setIndeterminate(true);
		locateFriend = (ImageButton) findViewById(R.id.btn_locate_friends);
		textName = (EditText) findViewById(R.id.textName);
		btnsave = (ImageButton) findViewById(R.id.sendButton);
		if (!utils.getPreference("Name", "anonymous", this).equals("anonymous")) {
			progress.show();
			getMyPrevousData();
			showPathOnMap();
			locateFriend.setVisibility(View.VISIBLE);
			textName.setVisibility(View.GONE);
			btnsave.setVisibility(View.GONE);
		}
		locateFriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showUserListDialog();
			}
		});
	}

	/**
	 * 
	 * @author acer
	 */
	private void getAllUsersList() {
		allUsers = new ArrayList<String>();
		Firebase ref = new Firebase("https://mssmapapp.firebaseio.com");
		ref.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				for (DataSnapshot child : snapshot.getChildren()) {
					allUsers.add(child.getKey());
					System.out.println("child Name : " + child.getKey());
				}
			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {
				System.out.println("The read failed: "
						+ firebaseError.getMessage());
			}
		});
	}

	/**
	 * 
	 * @author acer
	 */
	public void showPathOnMap() {
		md = new GMapV2Direction();
		Thread thread = new Thread(new Runnable() {
			// private org.w3c.dom.Document doc;

			@Override
			public void run() {
				try {
					Thread.sleep(5000l);
					points = new ArrayList<LatLng>();
					for (int i = 0; i < allData.size(); i++) {
						Map<String, Object> firstLoc = allData.get(i);
						String lng = (String) firstLoc.get("long");
						String lat = (String) firstLoc.get("lat");
						System.out.println("lat and long in string  ; "
								+ Float.parseFloat(lng) + " : "
								+ Float.parseFloat(lat));
						LatLng position = new LatLng(Float.parseFloat(lng),
								Float.parseFloat(lat));
						points.add(position);
					}
					// doc = md.getDocument(points.get(0), points.get(1),
					// GMapV2Direction.MODE_WALKING);
				} catch (Exception e) {
				}
				runOnUiThread(new Runnable() {
					public void run() {
						ArrayList<LatLng> directionPoint = points;
						PolylineOptions rectLine = new PolylineOptions().width(
								6).color(Color.RED);
						for (int i = 0; i < directionPoint.size(); i++) {
							MarkerOptions marker = new MarkerOptions()
									.position(directionPoint.get(i))
									.title((String) allData.get(i).get("time"));
							googleMap.addMarker(marker);
							rectLine.add(directionPoint.get(i));
							CameraPosition cameraPosition = new CameraPosition.Builder()
									.target(directionPoint.get(i)).zoom(16)
									.build();
							googleMap.animateCamera(CameraUpdateFactory
									.newCameraPosition(cameraPosition));

						}
						Polyline polylin = googleMap.addPolyline(rectLine);
						progress.dismiss();

					}
				});

			}
		});
		thread.start();

	}

	/**
	 * 
	 * @author acer
	 */
	private void getMyPrevousData() {
		Firebase ref = new Firebase("https://mssmapapp.firebaseio.com/"
				+ utils.getPreference("Name", "anonymous", this));
		ref.addChildEventListener(new ChildEventListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onChildAdded(DataSnapshot snapshot,
					String previousChildKey) {
				Map<String, Object> newPost = (Map<String, Object>) snapshot
						.getValue();
				allData.add(newPost);
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
			}

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
			}

		});

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

	private void showUserListDialog() {
		if (!(allUsers == null)) {
			AlertDialog.Builder builderSingle = new AlertDialog.Builder(
					MainActivity.this);
			builderSingle.setIcon(R.drawable.ic_launcher);
			builderSingle.setTitle("Select One Friend");
			final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
					MainActivity.this,
					android.R.layout.select_dialog_singlechoice);
			for (int i = 0; i < allUsers.size(); i++) {
				arrayAdapter.add(allUsers.get(i));
			}
			builderSingle.setNegativeButton("cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			builderSingle.setAdapter(arrayAdapter,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							progress.show();
							String strName = arrayAdapter.getItem(which);
							getFriendTrack(strName);
							showPathOnMap();
							AlertDialog.Builder builderInner = new AlertDialog.Builder(
									MainActivity.this);
							builderInner.setMessage(strName);
							builderInner.setTitle(strName
									+ " Track is showning on Map");
							builderInner.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									});
							builderInner.show();
						}
					});
			builderSingle.show();
		} else {
			Toast.makeText(MainActivity.this, "Please Wait for a Secand ",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 
	 * @author acer
	 * @param strName
	 */
	protected void getFriendTrack(String strName) {
		allData = null;
		allData = new ArrayList<Map<String, Object>>();
		Firebase ref = new Firebase("https://mssmapapp.firebaseio.com/"
				+ strName);
		Query queryRef = ref.orderByChild("time");
		queryRef.addChildEventListener(new ChildEventListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onChildAdded(DataSnapshot snapshot,
					String previousChildKey) {
				Map<String, Object> newPost = (Map<String, Object>) snapshot
						.getValue();
				allData.add(newPost);
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
			}

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
			}
		});
	}
}
