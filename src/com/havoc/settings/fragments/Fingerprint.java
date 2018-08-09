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
import android.content.ContentResolver;
import android.os.UserHandle;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.hardware.fingerprint.FingerprintManager;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.development.DevelopmentSettings;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.R;

public class Fingerprint extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "Fingerprint";
    private static final String FP_WAKE_AND_UNLOCK = "fp_wake_and_unlock"; 
    private static final String FP_ONLY_SCREEN_ON = "fp_only_screen_on";
    private static final String GESTURE_SWIPE_DOWN_FINGERPRINT_INPUT_SUMMARY = "gesture_swipe_down_fingerprint_input_summary";

    private SwitchPreference mFpWakeAndUnlock; 
    private SwitchPreference mFpOnlyScreenOn;
    private FingerprintManager mFingerprintManager;
    private Preference mFingerprintGesture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.havoc_settings_fingerprint);
        final PreferenceScreen prefSet = getPreferenceScreen(); 
        try {
            mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        } catch (Exception e) {
            //ignore
        }

        mFpWakeAndUnlock = (SwitchPreference) findPreference(FP_WAKE_AND_UNLOCK); 

         // Fingerprint only at screen on
        mFpOnlyScreenOn = (SwitchPreference) prefSet.findPreference(FP_ONLY_SCREEN_ON);

        mFpWakeAndUnlock.setChecked(Settings.System.getIntForUser(resolver, 
        Settings.System.FP_WAKE_AND_UNLOCK, 1, UserHandle.USER_CURRENT) == 1); 
        mFpWakeAndUnlock.setOnPreferenceChangeListener(this); 

        if (mFingerprintManager == null || !mFingerprintManager.isHardwareDetected()){
            mFpWakeAndUnlock.getParent().removePreference(mFpWakeAndUnlock);
            mFpOnlyScreenOn.getParent().removePreference(mFpOnlyScreenOn);
        }
		
        mFingerprintGesture = (Preference) prefSet.findPreference(GESTURE_SWIPE_DOWN_FINGERPRINT_INPUT_SUMMARY); 
        boolean mFingerprintGestureSupported = getResources().getBoolean( 
                com.android.internal.R.bool.config_supportSystemNavigationKeys); 
        if (!mFingerprintGestureSupported) 
            prefSet.removePreference(mFingerprintGesture);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mFpWakeAndUnlock) { 
            boolean value = (Boolean) objValue; 
            Settings.System.putIntForUser(resolver, 
                    Settings.System.FP_WAKE_AND_UNLOCK, value ? 1: 0, UserHandle.USER_CURRENT); 
            return true; 
        }

        return false;
    }


    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
