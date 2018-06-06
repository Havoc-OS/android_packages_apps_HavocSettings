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

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.IWindowManager;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.WindowManagerGlobal;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.utils.du.DUActionUtils;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.R;
import com.havoc.settings.utils.DeviceUtils;
import com.havoc.settings.utils.TelephonyUtils;

import org.lineageos.internal.util.ScreenType;

import static org.lineageos.internal.util.DeviceKeysConstants.*;

import java.util.List;

import lineageos.providers.LineageSettings;

public class Volume extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "Volume";
	
    private static final String KEY_VOLUME_KEY_CURSOR_CONTROL = "volume_key_cursor_control";
    private static final String KEY_SWAP_VOLUME_BUTTONS = "swap_volume_buttons";
    private static final String KEY_VOLUME_MUSIC_CONTROLS = "volbtn_music_controls";
    private static final String KEY_VOLUME_CONTROL_RING_STREAM = "volume_keys_control_ring_stream";

    private ListPreference mVolumeKeyCursorControl;
    private SwitchPreference mVolumeWakeScreen;
    private SwitchPreference mVolumeMusicControls;
    private SwitchPreference mSwapVolumeButtons;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_volume);

        final Resources res = getResources();
        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();


            int cursorControlAction = Settings.System.getIntForUser(resolver,
                    Settings.System.VOLUME_KEY_CURSOR_CONTROL, 0, UserHandle.USER_CURRENT);
            mVolumeKeyCursorControl = initList(KEY_VOLUME_KEY_CURSOR_CONTROL,
                    cursorControlAction);

            int swapVolumeKeys = LineageSettings.System.getIntForUser(getContentResolver(),
                    LineageSettings.System.SWAP_VOLUME_KEYS_ON_ROTATION, 0, UserHandle.USER_CURRENT);
            mSwapVolumeButtons = (SwitchPreference)
                    prefScreen.findPreference(KEY_SWAP_VOLUME_BUTTONS);
            if (mSwapVolumeButtons != null) {
                mSwapVolumeButtons.setChecked(swapVolumeKeys > 0);
            }

        mVolumeWakeScreen = (SwitchPreference) findPreference(LineageSettings.System.VOLUME_WAKE_SCREEN);
        mVolumeMusicControls = (SwitchPreference) findPreference(KEY_VOLUME_MUSIC_CONTROLS);

        if (mVolumeWakeScreen != null) {
            if (mVolumeMusicControls != null) {
                mVolumeMusicControls.setDependency(LineageSettings.System.VOLUME_WAKE_SCREEN);
                mVolumeWakeScreen.setDisableDependentsState(true);
            }
        }
    }

    private ListPreference initList(String key, Action value) {
        return initList(key, value.ordinal());
    }

    private ListPreference initList(String key, int value) {
        ListPreference list = (ListPreference) getPreferenceScreen().findPreference(key);
        if (list == null) return null;
        list.setValue(Integer.toString(value));
        list.setSummary(list.getEntry());
        list.setOnPreferenceChangeListener(this);
        return list;
    }

    private void handleListChange(ListPreference pref, Object newValue, String setting) {
        String value = (String) newValue;
        int index = pref.findIndexOfValue(value);
        pref.setSummary(pref.getEntries()[index]);
        LineageSettings.System.putIntForUser(getContentResolver(), setting, Integer.valueOf(value), UserHandle.USER_CURRENT);
    }

    private void handleSystemListChange(ListPreference pref, Object newValue, String setting) {
        String value = (String) newValue;
        int index = pref.findIndexOfValue(value);
        pref.setSummary(pref.getEntries()[index]);
        Settings.System.putIntForUser(getContentResolver(), setting, Integer.valueOf(value), UserHandle.USER_CURRENT);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mVolumeKeyCursorControl) {
            handleSystemListChange(mVolumeKeyCursorControl, newValue,
                    Settings.System.VOLUME_KEY_CURSOR_CONTROL);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mSwapVolumeButtons) {
            int value = mSwapVolumeButtons.isChecked()
                    ? (ScreenType.isTablet(getActivity()) ? 2 : 1) : 0;
            if (value == 2) {
                Display defaultDisplay = getActivity().getWindowManager().getDefaultDisplay();

                DisplayInfo displayInfo = new DisplayInfo();
                defaultDisplay.getDisplayInfo(displayInfo);

                // Not all tablets are landscape
                if (displayInfo.getNaturalWidth() < displayInfo.getNaturalHeight()) {
                    value = 1;
                }
            }
            LineageSettings.System.putIntForUser(getActivity().getContentResolver(),
                    LineageSettings.System.SWAP_VOLUME_KEYS_ON_ROTATION, value, UserHandle.USER_CURRENT);
		}
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}