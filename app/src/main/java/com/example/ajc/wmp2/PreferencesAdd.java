package com.example.ajc.wmp2;

import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by ajc on 2016-06-29.
 */
public class PreferencesAdd extends PreferenceActivity
{
    PreferencesAdd (String name)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Category" + name, "1");
        editor.commit();
    }
}
