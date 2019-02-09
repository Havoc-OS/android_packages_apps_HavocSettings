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
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto; 
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.R;
import com.havoc.settings.Utils;
import com.havoc.support.preferences.GlobalSettingMasterSwitchPreference;
import com.havoc.support.preferences.SystemSettingMasterSwitchPreference;

public class Notifications extends SettingsPreferenceFragment 
        implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "Notifications";

    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";
    private static final String FLASHLIGHT_ON_CALL = "flashlight_on_call";
    private static final String FORCE_EXPANDED_NOTIFICATIONS = "force_expanded_notifications";
    private static final String BATTERY_LIGHT_ENABLED = "battery_light_enabled";
    private static final String HEADS_UP_NOTIFICATIONS_ENABLED = "heads_up_notifications_enabled";
    private static final String STATUS_BAR_SHOW_TICKER = "status_bar_show_ticker";

    private ListPreference mFlashlightOnCall;
    private SwitchPreference mForceExpanded;
    private SystemSettingMasterSwitchPreference mBatteryLightEnabled;
    private GlobalSettingMasterSwitchPreference mHeadsUpEnabled;
    private SystemSettingMasterSwitchPreference mShowTicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_notifications);
		
        PreferenceScreen prefScreen = getPreferenceScreen();

        PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!Utils.isVoiceCapable(getActivity())) {
            prefScreen.removePreference(incallVibCategory);
        }

        mFlashlightOnCall = (ListPreference) findPreference(FLASHLIGHT_ON_CALL);
        Preference FlashOnCall = findPreference("flashlight_on_call");
        int flashlightValue = Settings.System.getInt(getContentResolver(),
                Settings.System.FLASHLIGHT_ON_CALL, 0);
        mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
        mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
        mFlashlightOnCall.setOnPreferenceChangeListener(this);

        if (!Utils.deviceSupportsFlashLight(getActivity())) {
            prefScreen.removePreference(FlashOnCall);
        }

	    mForceExpanded = (SwitchPreference) findPreference(FORCE_EXPANDED_NOTIFICATIONS);
        mForceExpanded.setChecked((Settings.System.getInt(getContentResolver(),
		        Settings.System.FORCE_EXPANDED_NOTIFICATIONS, 0) == 1));

        mBatteryLightEnabled = (SystemSettingMasterSwitchPreference) findPreference(BATTERY_LIGHT_ENABLED);
        mBatteryLightEnabled.setOnPreferenceChangeListener(this);
        int batteryLightEnabled = Settings.System.getInt(getContentResolver(),
                BATTERY_LIGHT_ENABLED, 1);
        mBatteryLightEnabled.setChecked(batteryLightEnabled != 0);

        mHeadsUpEnabled = (GlobalSettingMasterSwitchPreference) findPreference(HEADS_UP_NOTIFICATIONS_ENABLED);
        mHeadsUpEnabled.setOnPreferenceChangeListener(this);
        int headsUpEnabled = Settings.Global.getInt(getContentResolver(),
                HEADS_UP_NOTIFICATIONS_ENABLED, 1);
        mHeadsUpEnabled.setChecked(headsUpEnabled != 0);

        mShowTicker = (SystemSettingMasterSwitchPreference) findPreference(STATUS_BAR_SHOW_TICKER);
        mShowTicker.setOnPreferenceChangeListener(this);
        int showTicker = Settings.System.getInt(getContentResolver(),
                STATUS_BAR_SHOW_TICKER, 0);
        mShowTicker.setChecked(showTicker != 0);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mFlashlightOnCall) {
            int flashlightValue = Integer.parseInt(((String) objValue).toString());
            Settings.System.putInt(getContentResolver(),
                  Settings.System.FLASHLIGHT_ON_CALL, flashlightValue);
            mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
            mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
            return true;
        } else if (preference == mForceExpanded) {
            boolean checked = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FORCE_EXPANDED_NOTIFICATIONS, checked ? 1:0);
            return true;
        } else if (preference == mBatteryLightEnabled) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(),
		            BATTERY_LIGHT_ENABLED, value ? 1 : 0);
            return true;
        } else if (preference == mHeadsUpEnabled) {
            boolean value = (Boolean) objValue;
            Settings.Global.putInt(getContentResolver(),
		            HEADS_UP_NOTIFICATIONS_ENABLED, value ? 1 : 0);
            return true;
        } else if (preference == mShowTicker) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(),
		            STATUS_BAR_SHOW_TICKER, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
