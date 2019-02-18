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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R; 
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.support.preferences.SecureSettingSwitchPreference;
import com.havoc.support.preferences.CustomSeekBarPreference;
import com.havoc.support.preferences.SystemSettingSwitchPreference;
import com.havoc.support.preferences.SystemSettingMasterSwitchPreference;

public class Display extends SettingsPreferenceFragment implements 
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "Display";

    private static final String SMART_PIXELS_ENABLED = "smart_pixels_enable";
    private static final String STABILIZATION_ENABLE = "stabilization_enable";
    private static final String SYSUI_ROUNDED_FWVALS = "sysui_rounded_fwvals";
    private static final String SYSUI_ROUNDED_SIZE = "sysui_rounded_size";
    private static final String SYSUI_ROUNDED_CONTENT_PADDING = "sysui_rounded_content_padding";
    private static final String SYSUI_STATUS_BAR_PADDING = "sysui_status_bar_padding";
    private static final String PREF_KEY_CUTOUT = "cutout_settings";

    private SystemSettingMasterSwitchPreference mSmartPixelsEnabled;
    private SystemSettingMasterSwitchPreference mStabilizationEnabled;
    private SecureSettingSwitchPreference mRoundedFwvals;
    private CustomSeekBarPreference mCornerRadius;
    private CustomSeekBarPreference mContentPadding;
    private CustomSeekBarPreference mSBPadding;

    ContentResolver resolver; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_display);
        resolver = getActivity().getContentResolver();

        mSmartPixelsEnabled = (SystemSettingMasterSwitchPreference) findPreference(SMART_PIXELS_ENABLED);
        mSmartPixelsEnabled.setOnPreferenceChangeListener(this);
        int smartPixelsEnabled = Settings.System.getInt(getContentResolver(),
                SMART_PIXELS_ENABLED, 0);
        mSmartPixelsEnabled.setChecked(smartPixelsEnabled != 0);

        if (!getResources().getBoolean(com.android.internal.R.bool.config_enableSmartPixels)) {
            getPreferenceScreen().removePreference(mSmartPixelsEnabled);
        }

        mStabilizationEnabled = (SystemSettingMasterSwitchPreference) findPreference(STABILIZATION_ENABLE);
        mStabilizationEnabled.setOnPreferenceChangeListener(this);
        int stabilizationEnabled = Settings.System.getInt(getContentResolver(),
                STABILIZATION_ENABLE, 0);
        mStabilizationEnabled.setChecked(stabilizationEnabled != 0);

        Resources res = null;
        Context ctx = getContext();
        float density = Resources.getSystem().getDisplayMetrics().density;

        try {
            res = ctx.getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Rounded Corner Radius
        mCornerRadius = (CustomSeekBarPreference) findPreference(SYSUI_ROUNDED_SIZE);
        mCornerRadius.setOnPreferenceChangeListener(this);
        int resourceIdRadius = res.getIdentifier("com.android.systemui:dimen/rounded_corner_radius", null, null);
        int cornerRadius = Settings.Secure.getIntForUser(ctx.getContentResolver(), Settings.Secure.SYSUI_ROUNDED_SIZE,
                (int) (res.getDimension(resourceIdRadius) / density), UserHandle.USER_CURRENT);
        mCornerRadius.setValue(cornerRadius / 1);

        // Rounded Content Padding
        mContentPadding = (CustomSeekBarPreference) findPreference(SYSUI_ROUNDED_CONTENT_PADDING);
        mContentPadding.setOnPreferenceChangeListener(this);
        int resourceIdPadding = res.getIdentifier("com.android.systemui:dimen/rounded_corner_content_padding", null,
                null);
        int contentPadding = Settings.Secure.getIntForUser(ctx.getContentResolver(),
                Settings.Secure.SYSUI_ROUNDED_CONTENT_PADDING,
                (int) (res.getDimension(resourceIdPadding) / density), UserHandle.USER_CURRENT);
        mContentPadding.setValue(contentPadding / 1);

        // Status Bar Content Padding
        mSBPadding = (CustomSeekBarPreference) findPreference(SYSUI_STATUS_BAR_PADDING);
        int resourceIdSBPadding = res.getIdentifier("com.android.systemui:dimen/status_bar_extra_padding", null,
                null);
        int sbPadding = Settings.Secure.getIntForUser(ctx.getContentResolver(),
                Settings.Secure.SYSUI_STATUS_BAR_PADDING,
                (int) (res.getDimension(resourceIdSBPadding) / density), UserHandle.USER_CURRENT);
        mSBPadding.setValue(sbPadding);
        mSBPadding.setOnPreferenceChangeListener(this);

        // Rounded use Framework Values
        mRoundedFwvals = (SecureSettingSwitchPreference) findPreference(SYSUI_ROUNDED_FWVALS);
        mRoundedFwvals.setOnPreferenceChangeListener(this);

        Preference mCutoutPref = (Preference) findPreference(PREF_KEY_CUTOUT);
        if (!hasPhysicalDisplayCutout(getContext())) {
            getPreferenceScreen().removePreference(mCutoutPref);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mCornerRadius) {
            Settings.Secure.putIntForUser(getContext().getContentResolver(),Settings.Secure.SYSUI_ROUNDED_SIZE,
                    ((int) objValue) * 1, UserHandle.USER_CURRENT);
        } else if (preference == mContentPadding) {
            Settings.Secure.putIntForUser(getContext().getContentResolver(), Settings.Secure.SYSUI_ROUNDED_CONTENT_PADDING,
                    ((int) objValue) * 1, UserHandle.USER_CURRENT);
        } else if (preference == mSBPadding) {
            Settings.Secure.putIntForUser(getContext().getContentResolver(), Settings.Secure.SYSUI_STATUS_BAR_PADDING,
                    (int) objValue, UserHandle.USER_CURRENT);
        } else if (preference == mRoundedFwvals) {
            restoreCorners();
        } else if (preference == mSmartPixelsEnabled) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(),
		            SMART_PIXELS_ENABLED, value ? 1 : 0);
        } else if (preference == mStabilizationEnabled) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(),
		            STABILIZATION_ENABLE, value ? 1 : 0);
        }
        return true; 
    }

    private void restoreCorners() {
        Resources res = null;
        float density = Resources.getSystem().getDisplayMetrics().density;

        try {
            res = getContext().getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        int resourceIdRadius = res.getIdentifier("com.android.systemui:dimen/rounded_corner_radius", null, null);
        int resourceIdPadding = res.getIdentifier("com.android.systemui:dimen/rounded_corner_content_padding", null,
                null);
        int resourceIdSBPadding = res.getIdentifier("com.android.systemui:dimen/status_bar_extra_padding", null,
                null);
        mCornerRadius.setValue((int) (res.getDimension(resourceIdRadius) / density));
        mContentPadding.setValue((int) (res.getDimension(resourceIdPadding) / density));
        mSBPadding.setValue((int) (res.getDimension(resourceIdSBPadding) / density));
    }

    private static boolean hasPhysicalDisplayCutout(Context context) {
        return context.getResources().getBoolean(
                com.android.internal.R.bool.config_physicalDisplayCutout);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
