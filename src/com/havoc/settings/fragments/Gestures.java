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
import android.provider.Settings;
import android.content.ContentResolver;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.development.DevelopmentSettings;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.R;

public class Gestures extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "Gestures";
    private static final String USE_GESTURE_NAVIGATION = "use_bottom_gesture"; 

    private SwitchPreference mGestureNavigation; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ContentResolver resolver = getActivity().getContentResolver();
        addPreferencesFromResource(R.xml.havoc_settings_gestures);
        
        mGestureNavigation = (SwitchPreference) findPreference(USE_GESTURE_NAVIGATION); 
        mGestureNavigation.setChecked(Settings.System.getInt(resolver, 
               Settings.System.USE_BOTTOM_GESTURE_NAVIGATION, 0) == 1); 
        mGestureNavigation.setOnPreferenceChangeListener(this); 
 
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mGestureNavigation) { 
            boolean value = (Boolean) objValue; 
            Settings.System.putInt(getActivity().getContentResolver(), 
                    Settings.System.USE_BOTTOM_GESTURE_NAVIGATION, value ? 1 : 0); 
            return true; 
        } 

        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
