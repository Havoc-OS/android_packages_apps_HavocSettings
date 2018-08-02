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
import android.content.res.Resources;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManagerGlobal;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.havoc.settings.preferences.SystemSettingSwitchPreference;

import com.android.internal.logging.nano.MetricsProto;

public class Gestures extends SettingsPreferenceFragment implements
         OnPreferenceChangeListener {

    private static final String USE_BOTTOM_GESTURE_NAVIGATION = "use_bottom_gesture_navigation";

    private SystemSettingSwitchPreference mUseBottomGestureNavigation;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.havoc_settings_gestures);
        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        // use bottom gestures
        mUseBottomGestureNavigation = (SystemSettingSwitchPreference) findPreference(USE_BOTTOM_GESTURE_NAVIGATION);
        mUseBottomGestureNavigation.setOnPreferenceChangeListener(this);
        int useBottomGestureNavigation = Settings.System.getInt(getContentResolver(),
                USE_BOTTOM_GESTURE_NAVIGATION, 0);
        mUseBottomGestureNavigation.setChecked(useBottomGestureNavigation != 0);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mUseBottomGestureNavigation) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
		USE_BOTTOM_GESTURE_NAVIGATION, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
