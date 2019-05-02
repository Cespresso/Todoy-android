package cespresso.gmail.com.todoy.ui.main


import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import cespresso.gmail.com.todoy.R


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preference, rootKey)
    }
}
