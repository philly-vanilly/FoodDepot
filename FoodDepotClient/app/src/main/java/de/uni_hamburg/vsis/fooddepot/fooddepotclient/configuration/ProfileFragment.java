package de.uni_hamburg.vsis.fooddepot.fooddepotclient.configuration;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.EditText;
import android.widget.Toast;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends PreferenceFragment {

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.profile_prefs);

        Preference pref_profile_pic = findPreference("pref_profile_pic");
        pref_profile_pic.setOnPreferenceClickListener (new Preference.OnPreferenceClickListener(){
            public boolean onPreferenceClick(Preference preference){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            int PICK_IMAGE = 1;
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            return true;
            }
        });

        String defaultValue = "";
        SharedPreferences profileSharedPreferences = getActivity().getSharedPreferences("food_depot_profile_shared_preferences", Activity.MODE_PRIVATE);
        EditTextPreference username = (EditTextPreference) findPreference("pref_key_username");
        username.setText(profileSharedPreferences.getString(getActivity().getString(R.string.saved_profile_username), defaultValue));
        EditTextPreference firstname = (EditTextPreference) findPreference("pref_key_firstname");
        firstname.setText(profileSharedPreferences.getString(getActivity().getString(R.string.saved_profile_firstname), defaultValue));
        EditTextPreference lastname = (EditTextPreference) findPreference("pref_key_lastname");
        lastname.setText(profileSharedPreferences.getString(getActivity().getString(R.string.saved_profile_lastname), defaultValue));
        EditTextPreference email = (EditTextPreference) findPreference("pref_key_email");
        email.setText(profileSharedPreferences.getString(getActivity().getString(R.string.saved_profile_email), defaultValue));
        EditTextPreference password = (EditTextPreference) findPreference("pref_key_password");
        password.setText(profileSharedPreferences.getString(getActivity().getString(R.string.saved_profile_password), defaultValue));

        username.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                return true;
            }
        });

//        Preference pref_title_username = findPreference("pref_key_username");
//        pref_title_username.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                Toast.makeText(getActivity().getBaseContext(), preference.getTitle(), Toast.LENGTH_LONG).show();
////                SharedPreferences customSharedPreference = getActivity().getSharedPreferences("profile_shared_preferences", Activity.MODE_PRIVATE);
////                SharedPreferences.Editor editor = customSharedPreference.edit();
////                editor.putString("myCustomPref", "The preference has been clicked");
////                editor.commit();
//                return true;
//            }
//        });
    }
}
