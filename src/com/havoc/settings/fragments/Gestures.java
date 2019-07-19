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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManagerGlobal;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.hwkeys.ActionUtils; 

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.havoc.support.preferences.CustomSeekBarPreference;
import com.havoc.support.preferences.SecureSettingMasterSwitchPreference;
import com.havoc.support.preferences.SystemSettingMasterSwitchPreference;

public class Gestures extends SettingsPreferenceFragment implements
         OnPreferenceChangeListener {

    private static final String USE_BOTTOM_GESTURE_NAVIGATION = "use_bottom_gesture_navigation";
    private static final String EDGE_GESTURES_ENABLED = "edge_gestures_enabled";
    private static final String PIE_STATE = "pie_state";
    private static final String ACTIVE_EDGE_CATEGORY = "active_edge_category";
    private static final String GESTURE_ANYWHERE_ENABLED = "gesture_anywhere_enabled";

    private SystemSettingMasterSwitchPreference mUseBottomGestureNavigation;
    private SecureSettingMasterSwitchPreference mEdgeGesturesEnabled; 
    private SecureSettingMasterSwitchPreference mPieGestureEnabled; 
    private SystemSettingMasterSwitchPreference mGestureAnywhereEnabled; 

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.havoc_settings_gestures);
        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mUseBottomGestureNavigation = (SystemSettingMasterSwitchPreference) findPreference(USE_BOTTOM_GESTURE_NAVIGATION);
        mUseBottomGestureNavigation.setOnPreferenceChangeListener(this);
        int useBottomGestureNavigation = Settings.System.getInt(getContentResolver(),
                USE_BOTTOM_GESTURE_NAVIGATION, 0);
        mUseBottomGestureNavigation.setChecked(useBottomGestureNavigation != 0);

        mEdgeGesturesEnabled = (SecureSettingMasterSwitchPreference) findPreference(EDGE_GESTURES_ENABLED);
        mEdgeGesturesEnabled.setChecked((Settings.Secure.getInt(resolver,
                Settings.Secure.EDGE_GESTURES_ENABLED, 0) == 1));
        mEdgeGesturesEnabled.setOnPreferenceChangeListener(this);

        mPieGestureEnabled = (SecureSettingMasterSwitchPreference) findPreference(PIE_STATE);
        mPieGestureEnabled.setOnPreferenceChangeListener(this);
        int usePieGestures = Settings.Secure.getInt(resolver, PIE_STATE, 0);
        mPieGestureEnabled.setChecked(usePieGestures != 0);

        mGestureAnywhereEnabled = (SystemSettingMasterSwitchPreference) findPreference(GESTURE_ANYWHERE_ENABLED);
        mGestureAnywhereEnabled.setOnPreferenceChangeListener(this);
        int gestureAnywhereEnabled = Settings.System.getInt(resolver, GESTURE_ANYWHERE_ENABLED, 0);
        mGestureAnywhereEnabled.setChecked(gestureAnywhereEnabled != 0);

        Preference ActiveEdge = findPreference(ACTIVE_EDGE_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_active_edge)) {
            getPreferenceScreen().removePreference(ActiveEdge);
        } else {
            if (!getContext().getPackageManager().hasSystemFeature(
                    "android.hardware.sensor.assist")) {
                getPreferenceScreen().removePreference(ActiveEdge);
            }
        }

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mUseBottomGestureNavigation) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
		            USE_BOTTOM_GESTURE_NAVIGATION, value ? 1 : 0);
            return true;
        } else if (preference == mEdgeGesturesEnabled) { 
            int enabled = ((boolean) newValue) ? 1 : 0; 
            Settings.Secure.putIntForUser(resolver,
                    Settings.Secure.EDGE_GESTURES_ENABLED, enabled, UserHandle.USER_CURRENT); 
            if (enabled == 1) { 
                Settings.Secure.putInt(resolver, 
                        Settings.Secure.NAVIGATION_BAR_VISIBLE, 
                        0); 
            } else { 
                if (ActionUtils.hasNavbarByDefault(getPrefContext())) { 
                    Settings.Secure.putInt(resolver, 
                            Settings.Secure.NAVIGATION_BAR_VISIBLE, 
                            1); 
                } 
            } 
            return true; 
        } else if (preference == mPieGestureEnabled) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putInt(resolver, PIE_STATE, value ? 1 : 0);
            return true;
        } else if (preference == mGestureAnywhereEnabled) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver, GESTURE_ANYWHERE_ENABLED, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
