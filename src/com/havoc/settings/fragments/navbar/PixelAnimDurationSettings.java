/*Copyright (C) 2015 The ResurrectionRemix Project 
                2018 Havoc 
     Licensed under the Apache License, Version 2.0 (the "License"); 
     you may not use this file except in compliance with the License. 
     You may obtain a copy of the License at 
 
          http://www.apache.org/licenses/LICENSE-2.0 
 
     Unless required by applicable law or agreed to in writing, software 
     distributed under the License is distributed on an "AS IS" BASIS, 
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
     See the License for the specific language governing permissions and 
     limitations under the License. 
*/ 
package com.havoc.settings.fragments.navbar;
 
import android.app.Activity; 
import android.content.Context; 
import android.content.ContentResolver; 
import android.content.res.Resources; 
import android.provider.Settings; 
import android.os.Bundle; 
import android.os.UserHandle; 
import android.support.v14.preference.SwitchPreference; 
import android.support.v7.preference.Preference; 
import android.support.v7.preference.PreferenceCategory; 
import android.support.v7.preference.Preference.OnPreferenceChangeListener; 
import android.support.v7.preference.PreferenceScreen; 
import android.graphics.Color; 
import android.support.v7.preference.ListPreference; 

import com.android.internal.logging.nano.MetricsProto;  
import com.havoc.settings.preferences.SystemSettingSeekBarPreference;
import com.android.settings.R; 
import com.android.settings.SettingsPreferenceFragment; 

import net.margaritov.preference.colorpicker.ColorPickerPreference; 
 
public class PixelAnimDurationSettings extends SettingsPreferenceFragment implements 
        Preference.OnPreferenceChangeListener { 
    private static final String PIXEL_X = "opa_anim_duration_x"; 
    private static final String PIXEL_Y = "opa_anim_duration_y"; 
    private static final String PIXEL_COLLAPSE = "collapse_anim_duration_ry"; 
    private static final String PIXEL_BG = "collapse_anim_duration_bg"; 
    private static final String PIXEL_RETRACT = "retract_anim_duration"; 
    private static final String PIXEL_DIAMOND = "diamond_anim_duration"; 
    private static final String PIXEL_DOTS = "dots_anim_duration"; 
    private static final String PIXEL_HOME = "home_resize_anim_duration"; 
 
    private static final String TOP_COLOR = "dot_top_color"; 
    private static final String BOTTOM_COLOR = "dot_bottom_color"; 
    private static final String LEFT_COLOR = "dot_left_color"; 
    private static final String RIGHT_COLOR = "dot_right_color"; 
    private static final String COLOR_CAT = "pixel_anim_color"; 
    private static final String COLOR_STYLE = "dot_color_switch"; 
 
    private SystemSettingSeekBarPreference mPixelx; 
    private SystemSettingSeekBarPreference mPixely; 
    private SystemSettingSeekBarPreference mCollapse; 
    private SystemSettingSeekBarPreference mBg; 
    private SystemSettingSeekBarPreference mRetract; 
    private SystemSettingSeekBarPreference mDiamond; 
    private SystemSettingSeekBarPreference mDots; 
    private SystemSettingSeekBarPreference mHome; 
    private ListPreference mColorStyle; 
    private ColorPickerPreference mTopColor; 
    private ColorPickerPreference mBottomColor; 
    private ColorPickerPreference mLeftColor; 
    private ColorPickerPreference mRightColor; 
    private PreferenceCategory mColorCat; 
    protected Context mContext; 
    protected ContentResolver mContentRes; 
 
    @Override 
    public int getMetricsCategory() { 
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS; 
    } 
 
    @Override 
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        addPreferencesFromResource(R.xml.pixel_anim_duration); 
 
    mContext = getActivity().getApplicationContext(); 
 
        mContentRes = getActivity().getContentResolver(); 
      final Resources res = getResources(); 
    int defaultValue; 
 
        mPixelx = 
                (SystemSettingSeekBarPreference) findPreference(PIXEL_X); 
        int xanim = Settings.System.getIntForUser(getContentResolver(), 
                Settings.System.OPA_ANIM_DURATION_X, 133, UserHandle.USER_CURRENT); 
        mPixelx.setValue(xanim / 1); 
        mPixelx.setOnPreferenceChangeListener(this); 
 
        mPixely = 
                (SystemSettingSeekBarPreference) findPreference(PIXEL_Y); 
        int yanim = Settings.System.getIntForUser(getContentResolver(), 
                Settings.System.OPA_ANIM_DURATION_Y, 255, UserHandle.USER_CURRENT); 
        mPixely.setValue(yanim / 1); 
        mPixely.setOnPreferenceChangeListener(this); 
 
        mCollapse = 
                (SystemSettingSeekBarPreference) findPreference(PIXEL_COLLAPSE); 
        int xcol = Settings.System.getIntForUser(getContentResolver(), 
                Settings.System.COLLAPSE_ANIMATION_DURATION_RY, 83, UserHandle.USER_CURRENT); 
        mCollapse.setValue(xcol / 1); 
        mCollapse.setOnPreferenceChangeListener(this); 
 
        mBg = 
               (SystemSettingSeekBarPreference) findPreference(PIXEL_BG); 
        int bg = Settings.System.getIntForUser(getContentResolver(), 
                Settings.System.COLLAPSE_ANIMATION_DURATION_BG, 100, UserHandle.USER_CURRENT); 
        mBg.setValue(yanim / 1); 
        mBg.setOnPreferenceChangeListener(this); 
 
        mRetract = 
                (SystemSettingSeekBarPreference) findPreference(PIXEL_RETRACT); 
        int ret = Settings.System.getIntForUser(getContentResolver(), 
                Settings.System.RETRACT_ANIMATION_DURATION, 300, UserHandle.USER_CURRENT); 
        mRetract.setValue(ret/ 1); 
        mRetract.setOnPreferenceChangeListener(this); 
 
        mDiamond = 
                (SystemSettingSeekBarPreference) findPreference(PIXEL_DIAMOND); 
        int diam = Settings.System.getIntForUser(getContentResolver(), 
                Settings.System.DIAMOND_ANIMATION_DURATION, 200, UserHandle.USER_CURRENT); 
        mDiamond.setValue(diam / 1); 
        mDiamond.setOnPreferenceChangeListener(this); 
 
        mDots = 
                (SystemSettingSeekBarPreference) findPreference(PIXEL_DOTS); 
        int dots = Settings.System.getIntForUser(getContentResolver(), 
                Settings.System.DOTS_RESIZE_DURATION, 200, UserHandle.USER_CURRENT); 
        mDots.setValue(dots / 1); 
        mDots.setOnPreferenceChangeListener(this); 
 
        mHome = 
                (SystemSettingSeekBarPreference) findPreference(PIXEL_HOME); 
        int home = Settings.System.getIntForUser(getContentResolver(), 
                Settings.System.HOME_RESIZE_DURATION, 255, UserHandle.USER_CURRENT); 
        mHome.setValue(home / 1); 
        mHome.setOnPreferenceChangeListener(this); 

        mColorCat = (PreferenceCategory) findPreference(COLOR_CAT); 
 
        mTopColor = 
                (ColorPickerPreference) findPreference(TOP_COLOR); 
        mTopColor.setOnPreferenceChangeListener(this); 
        int top = Settings.System.getInt(mContentRes, 
                 Settings.System.DOT_TOP_COLOR, Color.RED); 
        String topHexColor = String.format("#%08x", (0x00ffffff & top)); 
        mTopColor.setSummary(topHexColor); 
        mTopColor.setNewPreviewColor(top); 
 
        mBottomColor = 
                (ColorPickerPreference) findPreference(BOTTOM_COLOR); 
        mBottomColor.setOnPreferenceChangeListener(this); 
        int bottom = Settings.System.getInt(mContentRes, 
                 Settings.System.DOT_BOTTOM_COLOR, Color.YELLOW); 
        String bottomHexColor = String.format("#%08x", (0x00ffffff & bottom)); 
        mBottomColor.setSummary(bottomHexColor); 
        mBottomColor.setNewPreviewColor(bottom); 
 
        mRightColor = 
                (ColorPickerPreference) findPreference(RIGHT_COLOR); 
        mRightColor.setOnPreferenceChangeListener(this); 
        int right = Settings.System.getInt(mContentRes, 
                 Settings.System.DOT_RIGHT_COLOR, Color.GREEN); 
        String rightHexColor = String.format("#%08x", (0x00ffffff & right)); 
        mRightColor.setSummary(rightHexColor); 
        mRightColor.setNewPreviewColor(right); 
 
        mLeftColor = 
                (ColorPickerPreference) findPreference(LEFT_COLOR); 
        mLeftColor.setOnPreferenceChangeListener(this); 
        int left = Settings.System.getInt(mContentRes, 
                 Settings.System.DOT_LEFT_COLOR, Color.RED); 
        String leftHexColor = String.format("#%08x", (0x00ffffff & left)); 
        mLeftColor.setSummary(leftHexColor); 
        mLeftColor.setNewPreviewColor(left); 
 
        mColorStyle = 
                (ListPreference) findPreference(COLOR_STYLE); 
        int style = Settings.System.getIntForUser(mContentRes, 
                 Settings.System.DOT_COLOR_SWITCH, 0, 
                 UserHandle.USER_CURRENT); 
        mColorStyle.setValue(String.valueOf(style)); 
        mColorStyle.setSummary(mColorStyle.getEntry()); 
        mColorStyle.setOnPreferenceChangeListener(this); 
        UpdateSettings(style); 
 
    } 
 
    public boolean onPreferenceChange(Preference preference, Object objValue) { 
    int intValue; 
        int index; 
    ContentResolver resolver = getActivity().getContentResolver(); 
    final Resources res = getResources(); 
        if (preference == mPixelx) { 
            int val = (Integer) objValue; 
            Settings.System.putIntForUser(getContentResolver(), 
                    Settings.System.OPA_ANIM_DURATION_X, val * 1, UserHandle.USER_CURRENT); 
            return true; 
        } else if (preference == mPixely) { 
            int val = (Integer) objValue; 
            Settings.System.putIntForUser(getContentResolver(), 
                    Settings.System.OPA_ANIM_DURATION_Y, val * 1, UserHandle.USER_CURRENT); 
            return true; 
        } else if (preference == mCollapse) { 
            int val = (Integer) objValue; 
            Settings.System.putIntForUser(getContentResolver(), 
                    Settings.System.COLLAPSE_ANIMATION_DURATION_RY, val * 1, UserHandle.USER_CURRENT); 
            return true; 
        } else if (preference == mBg) { 
            int val = (Integer) objValue; 
            Settings.System.putIntForUser(getContentResolver(), 
                    Settings.System.COLLAPSE_ANIMATION_DURATION_BG, val * 1, UserHandle.USER_CURRENT); 
            return true; 
        } else if (preference == mRetract) { 
            int val = (Integer) objValue; 
            Settings.System.putIntForUser(getContentResolver(), 
                    Settings.System.RETRACT_ANIMATION_DURATION, val * 1, UserHandle.USER_CURRENT); 
            return true; 
        } else if (preference == mDiamond) { 
            int val = (Integer) objValue; 
            Settings.System.putIntForUser(getContentResolver(), 
                    Settings.System.DIAMOND_ANIMATION_DURATION, val * 1, UserHandle.USER_CURRENT); 
            return true; 
        } else if (preference == mDots) { 
            int val = (Integer) objValue; 
            Settings.System.putIntForUser(getContentResolver(), 
                    Settings.System.DOTS_RESIZE_DURATION, val * 1, UserHandle.USER_CURRENT); 
            return true; 
        } else if (preference == mHome) { 
            int val = (Integer) objValue; 
            Settings.System.putIntForUser(getContentResolver(), 
                    Settings.System.HOME_RESIZE_DURATION, val * 1, UserHandle.USER_CURRENT); 
            return true; 
        } else if (preference == mTopColor) { 
            String hex = ColorPickerPreference.convertToARGB( 
                    Integer.valueOf(String.valueOf(objValue))); 
            preference.setSummary(hex); 
            int intHex = ColorPickerPreference.convertToColorInt(hex); 
            Settings.System.putInt(resolver, 
                    Settings.System.DOT_TOP_COLOR, intHex); 
            return true; 
        } else if (preference == mBottomColor) { 
            String hex = ColorPickerPreference.convertToARGB( 
                    Integer.valueOf(String.valueOf(objValue))); 
            preference.setSummary(hex); 
            int intHex = ColorPickerPreference.convertToColorInt(hex); 
            Settings.System.putInt(resolver, 
                    Settings.System.DOT_BOTTOM_COLOR, intHex); 
            return true; 
        } else if (preference == mRightColor) { 
            String hex = ColorPickerPreference.convertToARGB( 
                    Integer.valueOf(String.valueOf(objValue))); 
            preference.setSummary(hex); 
            int intHex = ColorPickerPreference.convertToColorInt(hex); 
            Settings.System.putInt(resolver, 
                    Settings.System.DOT_RIGHT_COLOR, intHex); 
            return true; 
        } else if (preference == mLeftColor) { 
            String hex = ColorPickerPreference.convertToARGB( 
                    Integer.valueOf(String.valueOf(objValue))); 
            preference.setSummary(hex); 
            int intHex = ColorPickerPreference.convertToColorInt(hex); 
            Settings.System.putInt(resolver, 
                    Settings.System.DOT_LEFT_COLOR, intHex); 
            return true; 
        } else if (preference == mColorStyle) { 
            intValue = Integer.parseInt((String) objValue); 
            index = mColorStyle.findIndexOfValue((String) objValue); 
            Settings.System.putIntForUser(resolver, Settings.System. 
                    DOT_COLOR_SWITCH, intValue, UserHandle.USER_CURRENT); 
            mColorStyle.setSummary(mColorStyle.getEntries()[index]); 
            UpdateSettings(intValue); 
            return true; 
         }  
        return false; 
    } 

    public void UpdateSettings(int style) { 
        if (style == 0 || style == 2 || style == 3) { 
            mColorCat.setEnabled(false); 
        } else { 
            mColorCat.setEnabled(true); 
        } 
   } 
} 