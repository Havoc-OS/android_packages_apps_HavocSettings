/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.havoc.settings.fragments;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarBattery extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";

    private ListPreference mStatusBarBatteryShowPercent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.statusbar_battery);

        ContentResolver resolver = getActivity().getContentResolver();

        mStatusBarBatteryShowPercent =
                (ListPreference) findPreference(SHOW_BATTERY_PERCENT);

        int batteryShowPercent = Settings.System.getInt(resolver,
                Settings.System.SHOW_BATTERY_PERCENT, 0);
        mStatusBarBatteryShowPercent.setValue(String.valueOf(batteryShowPercent));
        mStatusBarBatteryShowPercent.setSummary(mStatusBarBatteryShowPercent.getEntry());
        mStatusBarBatteryShowPercent.setOnPreferenceChangeListener(this);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.HAVOC_SETTINGS;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mStatusBarBatteryShowPercent) {
            int batteryShowPercent = Integer.valueOf((String) newValue);
            int index = mStatusBarBatteryShowPercent.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    resolver, Settings.System.SHOW_BATTERY_PERCENT, batteryShowPercent);
            mStatusBarBatteryShowPercent.setSummary(
                    mStatusBarBatteryShowPercent.getEntries()[index]);
            return true;
        }
        return false;
    }
}
