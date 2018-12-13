/*
 * Copyright (C) 2018 Havoc-OS
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
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.statusbar.IStatusBarService;
import com.android.settingslib.core.AbstractPreferenceController;

import libcore.util.Objects;
import java.util.ArrayList;
import java.util.List;


public class UiStylePreferenceController extends AbstractPreferenceController implements
        Preference.OnPreferenceChangeListener {

    private static final String UI_STYLE = "ui_style";
    private ListPreference mUiStyle;
    private IStatusBarService mStatusBarService;

    public UiStylePreferenceController(Context context) {
        super(context);
    }

    @Override
    public String getPreferenceKey() {
        return UI_STYLE;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mUiStyle = (ListPreference) screen.findPreference(UI_STYLE);
        int uiStyle = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.UI_STYLE, 0);
        int valueIndex = mUiStyle.findIndexOfValue(String.valueOf(uiStyle));
        mUiStyle.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        mUiStyle.setSummary(mUiStyle.getEntry());
        mUiStyle.setOnPreferenceChangeListener(this);
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mUiStyle) {
            String value = (String) newValue;
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.UI_STYLE, Integer.valueOf(value));
            int valueIndex = mUiStyle.findIndexOfValue(value);
            mUiStyle.setSummary(mUiStyle.getEntries()[valueIndex]);
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
