/*
 * Copyright (C) 2019 The Dirty Unicorns Project
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
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.havoc.support.preferences.CustomSeekBarPreference;

import java.util.ArrayList;
import java.util.List;

public class ActiveEdge extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_SHORT_SQUEEZE_APP_SELECTION = "short_squeeze_app_selection";
    private static final String KEY_LONG_SQUEEZE_APP_SELECTION = "long_squeeze_app_selection";

    private int shortSqueezeActions;
    private int longSqueezeActions;

    private CustomSeekBarPreference mActiveEdgeSensitivity;
    private ListPreference mShortSqueezeActions;
    private ListPreference mLongSqueezeActions;
    private Preference mShortSqueezeAppSelection;
    private Preference mLongSqueezeAppSelection;
    private SwitchPreference mActiveEdgeWake;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.active_edge);

        final ContentResolver resolver = getActivity().getContentResolver();

        shortSqueezeActions = Settings.Secure.getIntForUser(resolver,
                Settings.Secure.SHORT_SQUEEZE_SELECTION, 0,
                UserHandle.USER_CURRENT);
        mShortSqueezeActions = (ListPreference) findPreference("short_squeeze_selection");
        mShortSqueezeActions.setValue(Integer.toString(shortSqueezeActions));
        mShortSqueezeActions.setSummary(mShortSqueezeActions.getEntry());
        mShortSqueezeActions.setOnPreferenceChangeListener(this);

        longSqueezeActions = Settings.Secure.getIntForUser(resolver,
                Settings.Secure.LONG_SQUEEZE_SELECTION, 0,
                UserHandle.USER_CURRENT);
        mLongSqueezeActions = (ListPreference) findPreference("long_squeeze_selection");
        mLongSqueezeActions.setValue(Integer.toString(longSqueezeActions));
        mLongSqueezeActions.setSummary(mLongSqueezeActions.getEntry());
        mLongSqueezeActions.setOnPreferenceChangeListener(this);

        int sensitivity = Settings.Secure.getIntForUser(resolver,
                Settings.Secure.ASSIST_GESTURE_SENSITIVITY, 2, UserHandle.USER_CURRENT);
        mActiveEdgeSensitivity = (CustomSeekBarPreference) findPreference("gesture_assist_sensitivity");
        mActiveEdgeSensitivity.setValue(sensitivity);
        mActiveEdgeSensitivity.setOnPreferenceChangeListener(this);

        mActiveEdgeWake = (SwitchPreference) findPreference("gesture_assist_wake");
        mActiveEdgeWake.setChecked((Settings.Secure.getIntForUser(resolver,
                Settings.Secure.ASSIST_GESTURE_WAKE_ENABLED, 1,
                UserHandle.USER_CURRENT) == 1));
        mActiveEdgeWake.setOnPreferenceChangeListener(this);

        mShortSqueezeAppSelection = (Preference) findPreference(KEY_SHORT_SQUEEZE_APP_SELECTION);
        mLongSqueezeAppSelection = (Preference) findPreference(KEY_LONG_SQUEEZE_APP_SELECTION);

        customAppCheck();
        mShortSqueezeAppSelection.setEnabled(mShortSqueezeActions.getEntryValues()
                [shortSqueezeActions].equals("11"));
        mLongSqueezeAppSelection.setEnabled(mLongSqueezeActions.getEntryValues()
                [longSqueezeActions].equals("11"));
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mShortSqueezeActions) {
            int shortSqueezeActions = Integer.valueOf((String) newValue);
            Settings.Secure.putIntForUser(getContentResolver(),
                    Settings.Secure.SHORT_SQUEEZE_SELECTION, shortSqueezeActions,
                    UserHandle.USER_CURRENT);
            int index = mShortSqueezeActions.findIndexOfValue((String) newValue);
            mShortSqueezeActions.setSummary(
                    mShortSqueezeActions.getEntries()[index]);
            customAppCheck();
            mShortSqueezeAppSelection.setEnabled(mShortSqueezeActions.getEntryValues()
                    [shortSqueezeActions].equals("11"));
            return true;
        } else if (preference == mLongSqueezeActions) {
            int longSqueezeActions = Integer.valueOf((String) newValue);
            Settings.Secure.putIntForUser(getContentResolver(),
                    Settings.Secure.LONG_SQUEEZE_SELECTION, longSqueezeActions,
                    UserHandle.USER_CURRENT);
            int index = mLongSqueezeActions.findIndexOfValue((String) newValue);
            mLongSqueezeActions.setSummary(
                    mLongSqueezeActions.getEntries()[index]);
            customAppCheck();
            mLongSqueezeAppSelection.setEnabled(mLongSqueezeActions.getEntryValues()
                    [longSqueezeActions].equals("11"));
            return true;
        } else if (preference == mActiveEdgeSensitivity) {
            int val = (Integer) newValue;
            Settings.Secure.putIntForUser(getContentResolver(),
                    Settings.Secure.ASSIST_GESTURE_SENSITIVITY, val,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mActiveEdgeWake) {
            Settings.Secure.putIntForUser(getContentResolver(),
                    Settings.Secure.ASSIST_GESTURE_WAKE_ENABLED,
                    (Boolean) newValue ? 1 : 0,
                    UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Ensure preferences sensible to change get updated
        actionPreferenceReload();
        customAppCheck();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Ensure preferences sensible to change gets updated
        actionPreferenceReload();
        customAppCheck();
    }

    private void customAppCheck() {
        mShortSqueezeAppSelection.setSummary(Settings.Secure.getStringForUser(getActivity().getContentResolver(),
                String.valueOf(Settings.Secure.SHORT_SQUEEZE_CUSTOM_APP_FR_NAME), UserHandle.USER_CURRENT));
        mLongSqueezeAppSelection.setSummary(Settings.Secure.getStringForUser(getActivity().getContentResolver(),
                String.valueOf(Settings.Secure.LONG_SQUEEZE_CUSTOM_APP_FR_NAME), UserHandle.USER_CURRENT));
    }

    /* Helper for reloading both short and long gesture as they might change on
       package uninstallation */
    private void actionPreferenceReload() {
        int shortSqueezeActions = Settings.Secure.getIntForUser(getContentResolver(),
                Settings.Secure.SHORT_SQUEEZE_SELECTION, 0,
                UserHandle.USER_CURRENT);

        int longSqueezeActions = Settings.Secure.getIntForUser(getContentResolver(),
                Settings.Secure.LONG_SQUEEZE_SELECTION, 0,
                UserHandle.USER_CURRENT);

        // Reload the action preferences
        mShortSqueezeActions.setValue(Integer.toString(shortSqueezeActions));
        mShortSqueezeActions.setSummary(mShortSqueezeActions.getEntry());

        mLongSqueezeActions.setValue(Integer.toString(longSqueezeActions));
        mLongSqueezeActions.setSummary(mLongSqueezeActions.getEntry());

        // Also ensure that the application chooser gets disabled when needed
        mShortSqueezeAppSelection.setEnabled(mShortSqueezeActions.getEntryValues()
        [shortSqueezeActions].equals("11"));
        mLongSqueezeAppSelection.setEnabled(mLongSqueezeActions.getEntryValues()
        [longSqueezeActions].equals("11"));
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
