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

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;

import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.utils.du.DUActionUtils;

import com.havoc.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import lineageos.providers.LineageSettings; 

public class LockScreen extends SettingsPreferenceFragment
    implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "LockScreen";

    private static final String KEY_LOCKSCREEN_CLOCK_SELECTION = "lockscreen_clock_selection";
    private static final String KEY_LOCKSCREEN_DATE_SELECTION = "lockscreen_date_selection";

    private ListPreference mLockscreenClockSelection;
    private ListPreference mLockscreenDateSelection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_lockscreen);

        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        mLockscreenClockSelection = (ListPreference) findPreference(KEY_LOCKSCREEN_CLOCK_SELECTION);
        int clockSelection = Settings.System.getIntForUser(resolver,
                Settings.System.LOCKSCREEN_CLOCK_SELECTION, 0, UserHandle.USER_CURRENT);
        mLockscreenClockSelection.setValue(String.valueOf(clockSelection));
        mLockscreenClockSelection.setSummary(mLockscreenClockSelection.getEntry());
        mLockscreenClockSelection.setOnPreferenceChangeListener(this);

        mLockscreenDateSelection = (ListPreference) findPreference(KEY_LOCKSCREEN_DATE_SELECTION);
        int dateSelection = Settings.System.getIntForUser(resolver,
                Settings.System.LOCKSCREEN_DATE_SELECTION, 0, UserHandle.USER_CURRENT);
        mLockscreenDateSelection.setValue(String.valueOf(dateSelection));
        mLockscreenDateSelection.setSummary(mLockscreenDateSelection.getEntry());
        mLockscreenDateSelection.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mLockscreenClockSelection) {
            int clockSelection = Integer.valueOf((String) newValue);
            int index = mLockscreenClockSelection.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.LOCKSCREEN_CLOCK_SELECTION, clockSelection, UserHandle.USER_CURRENT);
            mLockscreenClockSelection.setSummary(mLockscreenClockSelection.getEntries()[index]);
            return true;
        } else if (preference == mLockscreenDateSelection) {
            int dateSelection = Integer.valueOf((String) newValue);
            int index = mLockscreenDateSelection.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.LOCKSCREEN_DATE_SELECTION, dateSelection, UserHandle.USER_CURRENT);
            mLockscreenDateSelection.setSummary(mLockscreenDateSelection.getEntries()[index]);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
