package com.eemanapp.fuoexaet.view.main.settings

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.eemanapp.fuoexaet.R

class SettingsContent : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener{
    override fun onPreferenceClick(preference: Preference?): Boolean {
        if (preference?.title == getString(R.string.push_notifications)){
            Toast.makeText(context, "Category clicked", Toast.LENGTH_LONG).show()
            //return super.onPreferenceTreeClick(preference)
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_screen, rootKey)
    }
}