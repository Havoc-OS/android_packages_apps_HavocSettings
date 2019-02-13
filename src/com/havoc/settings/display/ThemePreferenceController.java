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

import android.app.Fragment;
import android.content.Context;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;

import com.android.settings.core.PreferenceControllerMixin;

import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;

import com.havoc.settings.display.ThemePicker;

public class ThemePreferenceController extends AbstractPreferenceController implements
        PreferenceControllerMixin, LifecycleObserver, OnResume {

    private static final String THEME_PICKER = "theme_picker";

    private static final int MY_USER_ID = UserHandle.myUserId();

    private final Fragment mParent;
    private Preference mThemeStyle;

    public ThemePreferenceController(Context context, Lifecycle lifecycle, Fragment parent) {
        super(context);
        mParent = parent;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        mThemeStyle = (Preference) screen.findPreference(THEME_PICKER);
        mThemeStyle.setEnabled(true);
    }

    @Override
    public void onResume() {
        updateEnableState();
        updateSummary();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return THEME_PICKER;
    }

    public void updateEnableState() {
        if (mThemeStyle == null) {
            return;
        }

        mThemeStyle.setOnPreferenceClickListener(
            new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ThemePicker.show(mParent);
                    return true;
                }
            }
        );
    }

    public void updateSummary() {
        if (mThemeStyle != null) {
            mThemeStyle.setSummary(mContext.getString(
                    com.android.settings.R.string.theme_picker_summary));
        }
    }
}
