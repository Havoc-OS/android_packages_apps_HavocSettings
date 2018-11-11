/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.havoc.settings.display;

import android.content.Context;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.RemoteException;
import android.text.TextUtils;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.drawer.SettingsDrawerActivity;
import com.android.internal.statusbar.IStatusBarService;

import libcore.util.Objects;
import java.util.ArrayList;
import java.util.List;

import com.android.settings.R;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

public class DarkUIPreferenceController extends AbstractPreferenceController implements
        Preference.OnPreferenceChangeListener {

    private static final String SYSTEM_UI_THEME = "systemui_theme_style";
    private ListPreference mSystemUiThemeStyle;
    private IStatusBarService mStatusBarService;

    public DarkUIPreferenceController(Context context) {
        super(context);
    }

    @Override
    public String getPreferenceKey() {
        return SYSTEM_UI_THEME;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mSystemUiThemeStyle = (ListPreference) screen.findPreference(SYSTEM_UI_THEME);
        int systemuiThemeStyle = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.SYSTEM_UI_THEME, 0);
        int valueIndex = mSystemUiThemeStyle.findIndexOfValue(String.valueOf(systemuiThemeStyle));
        mSystemUiThemeStyle.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        mSystemUiThemeStyle.setSummary(mSystemUiThemeStyle.getEntry());
        mSystemUiThemeStyle.setOnPreferenceChangeListener(this);
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSystemUiThemeStyle) {
            String value = (String) newValue;
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SYSTEM_UI_THEME, Integer.valueOf(value));
            int valueIndex = mSystemUiThemeStyle.findIndexOfValue(value);
            mSystemUiThemeStyle.setSummary(mSystemUiThemeStyle.getEntries()[valueIndex]);

            IStatusBarService statusBarService = IStatusBarService.Stub.asInterface(ServiceManager.checkService(Context.STATUS_BAR_SERVICE));
            if (statusBarService != null) {
                try {
                    statusBarService.restartUI();
                } catch (RemoteException e) {
                    // do nothing.
                }
            }
        }
        return true;
    }
}
