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

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

import com.havoc.settings.R;

import java.util.ArrayList;
import java.util.List;

public class Animations extends SettingsPreferenceFragment  implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "Animations";
    private static final String SCREEN_OFF_ANIMATION = "screen_off_animation"; 
    private static final String KEY_TOAST_ANIMATION = "toast_animation";

    private ListPreference mScreenOffAnimation; 
    private ListPreference mToastAnimation;

    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getActivity().getContentResolver(); 

        addPreferencesFromResource(R.xml.havoc_settings_animations);

        mContext = getActivity().getApplicationContext();

        // Screen Off Animations 
        mScreenOffAnimation = (ListPreference) findPreference(SCREEN_OFF_ANIMATION); 
        int screenOffStyle = Settings.System.getInt(resolver, 
                 Settings.System.SCREEN_OFF_ANIMATION, 0); 
        mScreenOffAnimation.setValue(String.valueOf(screenOffStyle)); 
        mScreenOffAnimation.setSummary(mScreenOffAnimation.getEntry()); 
        mScreenOffAnimation.setOnPreferenceChangeListener(this); 

        mToastAnimation = (ListPreference) findPreference(KEY_TOAST_ANIMATION);
        mToastAnimation.setSummary(mToastAnimation.getEntry());
        int CurrentToastAnimation = Settings.System.getInt(getContentResolver(), Settings.System.TOAST_ANIMATION, 1);
        mToastAnimation.setValueIndex(CurrentToastAnimation); //set to index of default value
        mToastAnimation.setSummary(mToastAnimation.getEntries()[CurrentToastAnimation]);
        mToastAnimation.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver(); 
        if (preference == mScreenOffAnimation) { 
            Settings.System.putInt(getContentResolver(), 
                    Settings.System.SCREEN_OFF_ANIMATION, Integer.valueOf((String) newValue)); 
            int valueIndex = mScreenOffAnimation.findIndexOfValue((String) newValue); 
            mScreenOffAnimation.setSummary(mScreenOffAnimation.getEntries()[valueIndex]); 
            return true; 
        }  else if (preference == mToastAnimation) {
            int index = mToastAnimation.findIndexOfValue((String) objValue);
            Settings.System.putString(getContentResolver(), Settings.System.TOAST_ANIMATION, (String) objValue);
            mToastAnimation.setSummary(mToastAnimation.getEntries()[index]);
            Toast.makeText(mContext, "Toast Test", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false; 
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
