/*
 * Copyright (C) 2018 Havoc-OS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.havoc.settings.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.content.ContentResolver; 
import android.content.res.Resources; 
import android.os.UserHandle; 
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.development.DevelopmentSettings;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils; 

import com.havoc.settings.R;

public class Recents extends SettingsPreferenceFragment implements 
Preference.OnPreferenceChangeListener {

    public static final String TAG = "Recents";
    private static final String SHOW_CLEAR_ALL_RECENTS = "show_clear_all_recents"; 
    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location"; 

    private SwitchPreference mRecentsClearAll; 
    private ListPreference mRecentsClearAllLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_recents);

        final ContentResolver resolver = getContentResolver(); 
        final PreferenceScreen prefSet = getPreferenceScreen(); 
        final Resources res = getResources(); 
 
        mRecentsClearAll = (SwitchPreference) prefSet.findPreference(SHOW_CLEAR_ALL_RECENTS); 
        mRecentsClearAll.setChecked(Settings.System.getIntForUser(resolver, 
            Settings.System.SHOW_CLEAR_ALL_RECENTS, 1, UserHandle.USER_CURRENT) == 1); 
        mRecentsClearAll.setOnPreferenceChangeListener(this); 
 
        mRecentsClearAllLocation = (ListPreference) prefSet.findPreference(RECENTS_CLEAR_ALL_LOCATION); 
        int location = Settings.System.getIntForUser(resolver, 
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3, UserHandle.USER_CURRENT); 
        mRecentsClearAllLocation.setValue(String.valueOf(location)); 
        mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntry()); 
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this); 
    }

    @Override   
    public boolean onPreferenceChange(Preference preference, Object newValue){ 
    ContentResolver resolver = getActivity().getContentResolver(); 
        if (preference == mRecentsClearAll) { 
            boolean show = (Boolean) newValue; 
            Settings.System.putIntForUser(getActivity().getContentResolver(), 
                    Settings.System.SHOW_CLEAR_ALL_RECENTS, show ? 1 : 0, UserHandle.USER_CURRENT); 
            return true; 
        } else if (preference == mRecentsClearAllLocation) { 
            int location = Integer.valueOf((String) newValue); 
            int index = mRecentsClearAllLocation.findIndexOfValue((String) newValue); 
            Settings.System.putIntForUser(getActivity().getContentResolver(), 
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, location, UserHandle.USER_CURRENT); 
            mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntries()[index]); 
            return true; 
        } 
        return false; 
    } 


    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
