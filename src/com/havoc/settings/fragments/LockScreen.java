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

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.Utils;
import com.havoc.support.preferences.CustomSeekBarPreference;
import com.havoc.support.preferences.SecureSettingMasterSwitchPreference;
import com.havoc.support.preferences.SystemSettingMasterSwitchPreference;

public class LockScreen extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";
    private static final String WEATHER_LS_CAT = "weather_lockscreen_key";
    private static final String LOCKSCREEN_VISUALIZER_ENABLED = "lockscreen_visualizer_enabled";
    private static final String LOCKSCREEN_CLOCK = "lockscreen_clock";
    private static final String LOCKSCREEN_INFO = "lockscreen_info";

    private FingerprintManager mFingerprintManager;
    private SwitchPreference mFingerprintVib;
    private SecureSettingMasterSwitchPreference mVisualizerEnabled;
    private SystemSettingMasterSwitchPreference mClockEnabled;
    private SystemSettingMasterSwitchPreference mInfoEnabled;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.havoc_settings_lockscreen);

        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        Resources resources = getResources();

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintVib = (SwitchPreference) findPreference(FINGERPRINT_VIB);
        if (mFingerprintManager == null){
            prefScreen.removePreference(mFingerprintVib);
        } else {
            mFingerprintVib.setChecked((Settings.System.getInt(getContentResolver(),
                    Settings.System.FINGERPRINT_SUCCESS_VIB, 1) == 1));
            mFingerprintVib.setOnPreferenceChangeListener(this);
        }

        final PreferenceCategory weatherCategory = (PreferenceCategory) prefScreen
                .findPreference(WEATHER_LS_CAT);

        if (!Utils.isPackageInstalled(getContext(), "com.android.providers.weather")) {
            prefScreen.removePreference(weatherCategory);
        }

        mVisualizerEnabled = (SecureSettingMasterSwitchPreference) findPreference(LOCKSCREEN_VISUALIZER_ENABLED);
        mVisualizerEnabled.setOnPreferenceChangeListener(this);
        int visualizerEnabled = Settings.Secure.getInt(resolver,
                LOCKSCREEN_VISUALIZER_ENABLED, 0);
        mVisualizerEnabled.setChecked(visualizerEnabled != 0);

        mClockEnabled = (SystemSettingMasterSwitchPreference) findPreference(LOCKSCREEN_CLOCK);
        mClockEnabled.setOnPreferenceChangeListener(this);
        int clockEnabled = Settings.System.getInt(resolver,
                LOCKSCREEN_CLOCK, 1);
        mClockEnabled.setChecked(clockEnabled != 0);

        mInfoEnabled = (SystemSettingMasterSwitchPreference) findPreference(LOCKSCREEN_INFO);
        mInfoEnabled.setOnPreferenceChangeListener(this);
        int infoEnabled = Settings.System.getInt(resolver,
                LOCKSCREEN_INFO, 1);
        mInfoEnabled.setChecked(infoEnabled != 0);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mFingerprintVib) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FINGERPRINT_SUCCESS_VIB, value ? 1 : 0);
            return true;
        } else if (preference == mVisualizerEnabled) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putInt(getContentResolver(),
		            LOCKSCREEN_VISUALIZER_ENABLED, value ? 1 : 0);
            return true;
        } else if (preference == mClockEnabled) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
		            LOCKSCREEN_CLOCK, value ? 1 : 0);
            return true;
        } else if (preference == mInfoEnabled) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
		            LOCKSCREEN_INFO, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
