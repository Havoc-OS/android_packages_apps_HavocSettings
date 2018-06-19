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

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.R;

import android.app.Activity; 
import android.content.ContentResolver; 
import android.content.res.Resources; 
import android.database.ContentObserver; 
import android.os.Bundle; 
import android.os.Handler; 
import android.os.UserHandle; 
import android.support.v7.preference.ListPreference; 
import android.support.v14.preference.SwitchPreference; 
import android.support.v7.preference.Preference; 
import android.support.v7.preference.Preference.OnPreferenceChangeListener; 
import android.support.v7.preference.PreferenceScreen; 
import android.provider.Settings; 
 
import net.margaritov.preference.colorpicker.ColorPickerPreference; 
import com.havoc.settings.preferences.CustomSeekBarPreference; 

public class Panels extends SettingsPreferenceFragment implements 
Preference.OnPreferenceChangeListener {

    public static final String TAG = "Panels";
    private static final String PREF_TRANSPARENT_VOLUME_DIALOG = "transparent_volume_dialog"; 
    private static final String PREF_VOLUME_DIALOG_STROKE = "volume_dialog_stroke"; 
    private static final String PREF_VOLUME_DIALOG_STROKE_COLOR = "volume_dialog_stroke_color"; 
    private static final String PREF_VOLUME_DIALOG_STROKE_THICKNESS = "volume_dialog_stroke_thickness"; 
    private static final String PREF_VOLUME_DIALOG_CORNER_RADIUS = "volume_dialog_corner_radius"; 
    private static final String PREF_VOLUME_DIALOG_STROKE_DASH_WIDTH = "volume_dialog_dash_width"; 
    private static final String PREF_VOLUME_DIALOG_STROKE_DASH_GAP = "volume_dialog_dash_gap"; 
 
    private ListPreference mVolumeDialogStroke; 
    private Preference mVolumeDialogStrokeColor; 
    private Preference mVolumeDialogStrokeThickness; 
    private Preference mVolumeDialogDashWidth; 
    private Preference mVolumeDialogDashGap; 
 
    static final int DEFAULT_VOLUME_DIALOG_STROKE_COLOR = 0xFF80CBC4; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_panels);

        mVolumeDialogStroke = 
        (ListPreference) findPreference(Settings.System.VOLUME_DIALOG_STROKE); 
        mVolumeDialogStroke.setOnPreferenceChangeListener(this); 
        mVolumeDialogStrokeColor = findPreference(Settings.System.VOLUME_DIALOG_STROKE_COLOR); 
        mVolumeDialogStrokeThickness = findPreference(Settings.System.VOLUME_DIALOG_STROKE_THICKNESS); 
        mVolumeDialogDashWidth = findPreference(Settings.System.VOLUME_DIALOG_STROKE_DASH_WIDTH); 
        mVolumeDialogDashGap = findPreference(Settings.System.VOLUME_DIALOG_STROKE_DASH_GAP); 
        updateVolumeDialogDependencies(mVolumeDialogStroke.getValue()); 
    }

    @Override 
    public boolean onPreferenceChange(Preference preference, Object newValue) { 
        if (preference == mVolumeDialogStroke) { 
            updateVolumeDialogDependencies((String) newValue); 
            return true; 
        } else { 
            return false; 
        } 
    } 

    private void updateVolumeDialogDependencies(String volumeDialogStroke) { 
        if (volumeDialogStroke.equals("0")) { 
            mVolumeDialogStrokeColor.setEnabled(false); 
            mVolumeDialogStrokeThickness.setEnabled(false); 
            mVolumeDialogDashWidth.setEnabled(false); 
            mVolumeDialogDashGap.setEnabled(false); 
        } else if (volumeDialogStroke.equals("1")) { 
            mVolumeDialogStrokeColor.setEnabled(false); 
            mVolumeDialogStrokeThickness.setEnabled(true); 
            mVolumeDialogDashWidth.setEnabled(true); 
            mVolumeDialogDashGap.setEnabled(true); 
        } else { 
            mVolumeDialogStrokeColor.setEnabled(true); 
            mVolumeDialogStrokeThickness.setEnabled(true); 
            mVolumeDialogDashWidth.setEnabled(true); 
            mVolumeDialogDashGap.setEnabled(true); 
        } 
    } 

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
