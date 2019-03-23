/* 
* Copyright (C) 2016 RR 
*               2018 Havoc
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
import android.content.Context; 
import android.content.Intent; 
import android.content.res.Resources; 
import android.content.pm.PackageManager; 
import android.content.pm.ResolveInfo; 
import android.os.Bundle; 
import android.os.UserHandle; 
import android.support.v7.preference.ListPreference; 
import android.support.v14.preference.SwitchPreference; 
import android.support.v7.preference.Preference; 
import android.support.v7.preference.Preference.OnPreferenceChangeListener; 
import android.support.v7.preference.PreferenceScreen; 
import android.provider.Settings; 
import android.util.Log; 
 
import com.android.internal.logging.nano.MetricsProto.MetricsEvent; 
import com.android.settings.R; 
import com.android.settings.SettingsPreferenceFragment; 
import com.havoc.support.colorpicker.ColorPickerPreference; 
 
import java.util.List; 
import java.util.ArrayList; 
 
public class RecentsStyles extends SettingsPreferenceFragment  implements Preference.OnPreferenceChangeListener { 
 
    private static final String FAB_COLOR = "fab_button_color"; 
    private static final String MEMBAR_COLOR = "mem_bar_color";   
    private static final String MEM_TEXT_COLOR = "mem_text_color"; 
    private static final String CLEAR_BUTTON_COLOR = "clear_button_color"; 
    private static final String RECENTS_DATE_COLOR = "recents_date_color"; 
    private static final String RECENTS_CLOCK_COLOR = "recents_clock_color";    
    private static final String FAB_ANIM_STYLE = "fab_animation_style"; 
 
    static final int DEFAULT = 0xffffffff; 
    static final int DEFAULT_BG_ICON = 0xff4285f4;   
    static final int DEFAULT_BG_MEM_BAR = 0xff009688;   
    static final int DEFAULT_BG_FAB = 0xffffffff;   
 
    private ColorPickerPreference mMemTextColor; 
    private ColorPickerPreference mMemBarColor; 
    private ColorPickerPreference mClearButtonColor; 
    private ColorPickerPreference mfabColor; 
    private ColorPickerPreference mClockColor; 
    private ColorPickerPreference mDateColor; 
     
    private ListPreference mFabanimation;   

    @Override 
    public int getMetricsCategory() { 
        return MetricsEvent.HAVOC_SETTINGS; 
    } 
 
    @Override 
    public void onCreate(Bundle icicle) { 
        super.onCreate(icicle); 
        addPreferencesFromResource(R.xml.recent_styles); 
        ContentResolver resolver = getActivity().getContentResolver(); 
        PreferenceScreen prefSet = getPreferenceScreen(); 
   
        int intColor; 
        String hexColor; 
 
        mfabColor = (ColorPickerPreference) prefSet.findPreference(FAB_COLOR); 
        mfabColor.setOnPreferenceChangeListener(this); 
        intColor = Settings.System.getInt(getContentResolver(), 
                Settings.System.FAB_BUTTON_COLOR, DEFAULT_BG_FAB); 
        hexColor = String.format("#%08x", (0xffffffff & intColor)); 
        mfabColor.setSummary(hexColor); 
        mfabColor.setNewPreviewColor(intColor); 
 
        mFabanimation = (ListPreference) prefSet.findPreference(FAB_ANIM_STYLE); 
        mFabanimation.setValue(String.valueOf(Settings.System.getInt( 
                getContentResolver(), Settings.System.FAB_ANIMATION_STYLE, 0))); 
        mFabanimation.setSummary(mFabanimation.getEntry()); 
        mFabanimation.setOnPreferenceChangeListener(this); 
 
        mMemTextColor = (ColorPickerPreference) prefSet.findPreference(MEM_TEXT_COLOR); 
        mMemTextColor.setOnPreferenceChangeListener(this); 
        intColor = Settings.System.getInt(getContentResolver(), 
                Settings.System.MEM_TEXT_COLOR, DEFAULT); 
        hexColor = String.format("#%08x", (0xffffffff & intColor)); 
        mMemTextColor.setSummary(hexColor); 
        mMemTextColor.setNewPreviewColor(intColor); 
 
        mMemBarColor= (ColorPickerPreference) prefSet.findPreference(MEMBAR_COLOR); 
        mMemBarColor.setOnPreferenceChangeListener(this); 
        intColor = Settings.System.getInt(getContentResolver(), 
                Settings.System.MEM_BAR_COLOR, DEFAULT_BG_MEM_BAR); 
        hexColor = String.format("#%08x", (0xff009688 & intColor)); 
        mMemBarColor.setSummary(hexColor); 
        mMemBarColor.setNewPreviewColor(intColor); 
 
        mClearButtonColor= (ColorPickerPreference) prefSet.findPreference(CLEAR_BUTTON_COLOR); 
        mClearButtonColor.setOnPreferenceChangeListener(this); 
        intColor = Settings.System.getInt(getContentResolver(), 
                Settings.System.CLEAR_BUTTON_COLOR, getResources().getColor(R.color.floating_action_button_touch_tint)); 
        hexColor = String.format("#%08x", (0xff4285f4 & intColor)); 
        mClearButtonColor.setSummary(hexColor); 
        mClearButtonColor.setNewPreviewColor(intColor); 
 
        mClockColor= (ColorPickerPreference) prefSet.findPreference(RECENTS_CLOCK_COLOR); 
        mClockColor.setOnPreferenceChangeListener(this); 
        intColor = Settings.System.getInt(getContentResolver(), 
                Settings.System.RECENTS_CLOCK_COLOR, DEFAULT); 
        hexColor = String.format("#%08x", (0xffffffff & intColor)); 
        mClockColor.setSummary(hexColor); 
        mClockColor.setNewPreviewColor(intColor); 
 
        mDateColor= (ColorPickerPreference) prefSet.findPreference(RECENTS_DATE_COLOR); 
        mDateColor.setOnPreferenceChangeListener(this); 
        intColor = Settings.System.getInt(getContentResolver(), 
                Settings.System.RECENTS_DATE_COLOR, DEFAULT); 
        hexColor = String.format("#%08x", (0xffffffff & intColor)); 
        mDateColor.setSummary(hexColor); 
        mDateColor.setNewPreviewColor(intColor);      
    } 
 
    @Override 
    public boolean onPreferenceChange(Preference preference, Object newValue) { 
        ContentResolver resolver = getActivity().getContentResolver(); 
        if (preference == mfabColor) { 
            String hex = ColorPickerPreference.convertToARGB( 
                Integer.valueOf(String.valueOf(newValue))); 
            if (hex.equals("#ffffffff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex); 
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), 
                Settings.System.FAB_BUTTON_COLOR, intHex); 
            return true; 
        } else if (preference == mMemTextColor) { 
            String hex = ColorPickerPreference.convertToARGB( 
                    Integer.valueOf(String.valueOf(newValue))); 
            if (hex.equals("#ffffffff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex); 
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), 
                Settings.System.MEM_TEXT_COLOR, intHex); 
            return true; 
        } else if (preference == mMemBarColor) { 
            String hex = ColorPickerPreference.convertToARGB( 
                    Integer.valueOf(String.valueOf(newValue))); 
            if (hex.equals("#ff009688")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex); 
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), 
                    Settings.System.MEM_BAR_COLOR, intHex); 
            return true; 
        } else if (preference == mClearButtonColor) { 
            String hex = ColorPickerPreference.convertToARGB( 
                    Integer.valueOf(String.valueOf(newValue))); 
            if (hex.equals("#ff4285f4")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex); 
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), 
                    Settings.System.CLEAR_BUTTON_COLOR, intHex); 
            return true; 
        } else if (preference == mClockColor) { 
            String hex = ColorPickerPreference.convertToARGB( 
                    Integer.valueOf(String.valueOf(newValue))); 
            if (hex.equals("#ffffffff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex); 
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), 
                    Settings.System.RECENTS_CLOCK_COLOR, intHex); 
            return true; 
        } else if (preference == mDateColor) { 
            String hex = ColorPickerPreference.convertToARGB( 
                    Integer.valueOf(String.valueOf(newValue))); 
            if (hex.equals("#ffffffff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex); 
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), 
                    Settings.System.RECENTS_DATE_COLOR, intHex); 
            return true; 
        } else if (preference == mFabanimation) { 
            Settings.System.putInt(getContentResolver(), Settings.System.FAB_ANIMATION_STYLE, 
                    Integer.valueOf((String) newValue)); 
            mFabanimation.setValue(String.valueOf(newValue)); 
            mFabanimation.setSummary(mFabanimation.getEntry());   
        } 
        return false; 
    } 
} 