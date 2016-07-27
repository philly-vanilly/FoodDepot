package de.uni_hamburg.vsis.fooddepot.fooddepotclient.settings;

import android.preference.PreferenceFragment;
import android.os.Bundle;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends PreferenceFragment {

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}
