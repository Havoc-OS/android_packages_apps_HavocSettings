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
import com.android.settings.widget.VideoPreference;

import com.havoc.support.preferences.CustomSeekBarPreference;

import java.util.ArrayList;
import java.util.List;

public class ActiveEdge extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_SQUEEZE_VIDEO = "squeeze_video";
    private static final String KEY_VIDEO_PAUSED = "key_video_paused";

    private boolean mVideoPaused;

    private CustomSeekBarPreference mActiveEdgeSensitivity;
    private ListPreference mActiveEdgeActions;
    private SwitchPreference mActiveEdgeWake;
    private VideoPreference mVideoPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.active_edge);

        final ContentResolver resolver = getActivity().getContentResolver();

        if (savedInstanceState != null) {
            mVideoPaused = savedInstanceState.getBoolean(KEY_VIDEO_PAUSED, false);
        }

        mVideoPreference = (VideoPreference) findPreference(KEY_SQUEEZE_VIDEO);

        int activeEdgeActions = Settings.Secure.getIntForUser(resolver,
                Settings.Secure.SQUEEZE_SELECTION, 0,
                UserHandle.USER_CURRENT);
        mActiveEdgeActions = (ListPreference) findPreference("squeeze_selection");
        mActiveEdgeActions.setValue(Integer.toString(activeEdgeActions));
        mActiveEdgeActions.setSummary(mActiveEdgeActions.getEntry());
        mActiveEdgeActions.setOnPreferenceChangeListener(this);

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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_VIDEO_PAUSED, mVideoPaused);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoPreference != null) {
            mVideoPaused = mVideoPreference.isVideoPaused();
            mVideoPreference.onViewInvisible();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideoPreference != null) {
            mVideoPreference.onViewVisible(mVideoPaused);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mActiveEdgeActions) {
            int value = Integer.valueOf((String) newValue);
            Settings.Secure.putIntForUser(getContentResolver(),
                    Settings.Secure.SQUEEZE_SELECTION, value,
                    UserHandle.USER_CURRENT);
            int index = mActiveEdgeActions.findIndexOfValue((String) newValue);
            mActiveEdgeActions.setSummary(
                    mActiveEdgeActions.getEntries()[index]);
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
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
