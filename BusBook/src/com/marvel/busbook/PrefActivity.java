package com.marvel.busbook;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.preference.*;
import android.preference.Preference.OnPreferenceChangeListener;
import android.widget.*;

public class PrefActivity extends PreferenceActivity {

	Button btnOK;
	Button btnCancel;
	ListView lstViewDataFiles;
	TextView txtDataLocation;
	CheckBox chkSortResult;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.pref);
        addPreferencesFromResource(R.xml.preferences);
        
        final ListPreference lstViewDataFiles = (ListPreference) findPreference("lstViewDataFiles");
        String files[] = new File(Environment.getExternalStorageDirectory().getPath()+"/busbook/").list();        
        lstViewDataFiles.setEntryValues(files);
        lstViewDataFiles.setEntries(files);
        
        lstViewDataFiles.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = lstViewDataFiles.findIndexOfValue(newValue.toString());
                if (index != -1) {

                	String filename = (String) lstViewDataFiles.getEntries()[index];
    				BusInfoData info = BusInfoData.getInstance();
    				Global global = Global.getInstance();
    				global.buslinefile = filename;
    				info.ensureLoaded(getResources(), true);
    				Toast.makeText(getBaseContext(), "The location has been updated", Toast.LENGTH_LONG).show();	
                }
                
                return true;
            }
        });

		final CheckBoxPreference chkSortResult = (CheckBoxPreference) findPreference("chkSortResult");		
        chkSortResult.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
				Global.getInstance().sortResult = (Boolean)newValue;                
                return true;
            }
        });
        
    }

}
