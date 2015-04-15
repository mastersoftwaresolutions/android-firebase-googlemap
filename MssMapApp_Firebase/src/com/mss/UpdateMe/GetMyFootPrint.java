/**
 * 
 */
package com.mss.UpdateMe;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * @author Sarbjot Singh acer
 * 
 */
public class GetMyFootPrint extends Activity {
	private TextView textView;
	ArrayList<Map<String, Object>> allData;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listdata);
		textView = (TextView) findViewById(R.id.data);
		allData = new ArrayList<Map<String, Object>>();
		Firebase ref = new Firebase("https://mssmapapp.firebaseio.com/sarbjot");
		ref.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				// textView.setText(snapshot.getValue().toString());
				System.out.println(snapshot.getValue());
			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {
				System.out.println("The read failed: "
						+ firebaseError.getMessage());
			}
		});

		// Retrieve new posts as they are added to Firebase
		ref.addChildEventListener(new ChildEventListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onChildAdded(DataSnapshot snapshot,
					String previousChildKey) {
				Map<String, Object> newPost = (Map<String, Object>) snapshot
						.getValue();
				allData.add(newPost);
				textView.setText(newPost.get("long") + " : "
						+ newPost.get("lat") + " , " + newPost.get("time"));
				System.out.println("Long: " + newPost.get("long"));
				System.out.println("lat: " + newPost.get("lat"));
				System.out.println("Time: " + newPost.get("time"));
			}

			// ... ChildEventListener also defines onChildChanged,
			// onChildRemoved,
			// onChildMoved and onCanceled, covered in later sections.

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
				// TODO Auto-generated method stub

			}
		});
	}
}
