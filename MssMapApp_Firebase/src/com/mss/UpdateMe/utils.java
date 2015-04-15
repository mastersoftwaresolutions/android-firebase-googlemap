/**
 * 
 */
package com.mss.UpdateMe;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Sarbjot Singh acer
 * 
 */
public class utils {
	/**
	 * Stores a particular value to the shared preference
	 * 
	 * @param key
	 * @param value
	 * @param context
	 */
	static public void setPreference(String key, String value, Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * Gets the value from shared preference for a particular key
	 * 
	 * @param key
	 * @param defaultValue
	 * @param context
	 * @return
	 */
	static public String getPreference(String key, String defaultValue,
			Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getString(key, defaultValue);
	}

}
