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
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.FontInfo;
import android.content.IFontService;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference; 
import android.widget.Toast;
import android.view.ViewConfiguration; 
import android.view.Menu; 
import android.view.MenuInflater; 
import android.view.MenuItem; 
import android.util.Log; 
import android.text.TextUtils; 
import android.content.DialogInterface; 
import android.app.AlertDialog; 

import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

import com.havoc.settings.preferences.CustomSeekBarPreference; 

import com.havoc.settings.fragments.ui.AnimationControls;
import com.havoc.settings.fragments.ui.DozeFragment;
import com.havoc.settings.fragments.ui.FontDialogPreference;
import com.havoc.settings.R;

import java.util.ArrayList;
import java.util.List;

import lineageos.providers.LineageSettings;

public class Animations extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_TOAST_ANIMATION = "toast_animation";
    private static final String KEY_LISTVIEW_ANIMATION = "listview_animation";
    private static final String KEY_LISTVIEW_INTERPOLATOR = "listview_interpolator";
/*
    private static final String POWER_MENU_ANIMATION = "power_menu_animation";
*/
    private static final String PREF_TILE_ANIM_STYLE = "qs_tile_animation_style";
    private static final String PREF_TILE_ANIM_DURATION = "qs_tile_animation_duration";
    private static final String PREF_TILE_ANIM_INTERPOLATOR = "qs_tile_animation_interpolator";
    private static final String SCROLLINGCACHE_PREF = "pref_scrollingcache";
    private static final String SCROLLINGCACHE_PERSIST_PROP = "persist.sys.scrollingcache";
    private static final String SCROLLINGCACHE_DEFAULT = "1";
    private static final String TAG = "ScrollAnimationInterfaceSettings"; 
 
    private static final String ANIMATION_FLING_VELOCITY = "animation_fling_velocity"; 
    private static final String ANIMATION_SCROLL_FRICTION = "animation_scroll_friction"; 
    private static final String ANIMATION_OVERSCROLL_DISTANCE = "animation_overscroll_distance"; 
    private static final String ANIMATION_OVERFLING_DISTANCE = "animation_overfling_distance"; 
    private static final float MULTIPLIER_SCROLL_FRICTION = 10000f; 
    private static final String ANIMATION_NO_SCROLL = "animation_no_scroll"; 
 
    private static final int MENU_RESET = Menu.FIRST; 
 
    private CustomSeekBarPreference mAnimationFling; 
    private CustomSeekBarPreference mAnimationScroll; 
    private CustomSeekBarPreference mAnimationOverScroll; 
    private CustomSeekBarPreference mAnimationOverFling; 
    private SwitchPreference mAnimNoScroll; 
    private ListPreference mToastAnimation;
    private ListPreference mListViewAnimation;
    private ListPreference mListViewInterpolator;
/*
    private ListPreference mPowerMenuAnimation;
*/
    private ListPreference mTileAnimationStyle;
    private ListPreference mTileAnimationDuration;
    private ListPreference mTileAnimationInterpolator;
    private ListPreference mScrollingCachePref;

    Toast mToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.havoc_settings_animations);
        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        mToastAnimation = (ListPreference) findPreference(KEY_TOAST_ANIMATION);
        int toastanimation = Settings.System.getIntForUser(resolver,
                Settings.System.TOAST_ANIMATION, 1,
                UserHandle.USER_CURRENT);
        mToastAnimation.setValue(String.valueOf(toastanimation));
        mToastAnimation.setSummary(mToastAnimation.getEntry());
        mToastAnimation.setOnPreferenceChangeListener(this);

        mListViewAnimation = (ListPreference) findPreference(KEY_LISTVIEW_ANIMATION);
        int listviewanimation = Settings.System.getIntForUser(resolver,
                Settings.System.LISTVIEW_ANIMATION, 0,
                UserHandle.USER_CURRENT);
        mListViewAnimation.setValue(String.valueOf(listviewanimation));
        mListViewAnimation.setSummary(mListViewAnimation.getEntry());
        mListViewAnimation.setOnPreferenceChangeListener(this);

        mListViewInterpolator = (ListPreference) findPreference(KEY_LISTVIEW_INTERPOLATOR);
        int listviewinterpolator = Settings.System.getIntForUser(resolver,
                Settings.System.LISTVIEW_INTERPOLATOR, 0,
                UserHandle.USER_CURRENT);
        mListViewInterpolator.setValue(String.valueOf(listviewinterpolator));
        mListViewInterpolator.setSummary(mListViewInterpolator.getEntry());
        mListViewInterpolator.setEnabled(listviewanimation > 0);
        mListViewInterpolator.setOnPreferenceChangeListener(this);

/*
        mPowerMenuAnimation = (ListPreference) findPreference(POWER_MENU_ANIMATION);
        int powermenuanimation = Settings.System.getIntForUser(resolver,
                Settings.System.POWER_MENU_ANIMATION, 0,
                UserHandle.USER_CURRENT);
        mPowerMenuAnimation.setValue(String.valueOf(powermenuanimation));
        mPowerMenuAnimation.setSummary(mPowerMenuAnimation.getEntry());
        mPowerMenuAnimation.setOnPreferenceChangeListener(this);
*/

        mTileAnimationStyle = (ListPreference) findPreference(PREF_TILE_ANIM_STYLE);
        int tileAnimationStyle = Settings.System.getIntForUser(resolver,
                Settings.System.ANIM_TILE_STYLE, 0,
                UserHandle.USER_CURRENT);
        mTileAnimationStyle.setValue(String.valueOf(tileAnimationStyle));
        mTileAnimationStyle.setSummary(mTileAnimationStyle.getEntry());
        mTileAnimationStyle.setOnPreferenceChangeListener(this);

        mTileAnimationDuration = (ListPreference) findPreference(PREF_TILE_ANIM_DURATION);
        int tileAnimationDuration = Settings.System.getIntForUser(resolver,
                Settings.System.ANIM_TILE_DURATION, 2000,
                UserHandle.USER_CURRENT);
        mTileAnimationDuration.setValue(String.valueOf(tileAnimationDuration));
        mTileAnimationDuration.setSummary(mTileAnimationDuration.getEntry());
        mTileAnimationDuration.setEnabled(tileAnimationStyle > 0);
        mTileAnimationDuration.setOnPreferenceChangeListener(this);

        mTileAnimationInterpolator = (ListPreference) findPreference(PREF_TILE_ANIM_INTERPOLATOR);
        int tileAnimationInterpolator = Settings.System.getIntForUser(resolver,
                Settings.System.ANIM_TILE_INTERPOLATOR, 0,
                UserHandle.USER_CURRENT);
        mTileAnimationInterpolator.setValue(String.valueOf(tileAnimationInterpolator));
        mTileAnimationInterpolator.setSummary(mTileAnimationInterpolator.getEntry());
        mTileAnimationInterpolator.setEnabled(tileAnimationStyle > 0);
        mTileAnimationInterpolator.setOnPreferenceChangeListener(this);

        mScrollingCachePref = (ListPreference) findPreference(SCROLLINGCACHE_PREF);
        mScrollingCachePref.setValue(SystemProperties.get(SCROLLINGCACHE_PERSIST_PROP,
                SystemProperties.get(SCROLLINGCACHE_PERSIST_PROP, SCROLLINGCACHE_DEFAULT)));
        mScrollingCachePref.setSummary(mScrollingCachePref.getEntry());
        mScrollingCachePref.setOnPreferenceChangeListener(this);

        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }

        mAnimNoScroll = (SwitchPreference) prefScreen.findPreference(ANIMATION_NO_SCROLL); 
        mAnimNoScroll.setChecked(Settings.System.getInt(resolver, 
                Settings.System.ANIMATION_CONTROLS_NO_SCROLL, 0) == 1); 
        mAnimNoScroll.setOnPreferenceChangeListener(this); 
 
        float defaultScroll = Settings.System.getFloat(resolver, 
                Settings.System.CUSTOM_SCROLL_FRICTION, ViewConfiguration.DEFAULT_SCROLL_FRICTION); 
        mAnimationScroll = (CustomSeekBarPreference) prefScreen.findPreference(ANIMATION_SCROLL_FRICTION); 
        mAnimationScroll.setValue((int) (defaultScroll * MULTIPLIER_SCROLL_FRICTION)); 
        mAnimationScroll.setOnPreferenceChangeListener(this); 
 
        int defaultFling = Settings.System.getInt(resolver, 
                Settings.System.CUSTOM_FLING_VELOCITY, ViewConfiguration.DEFAULT_MAXIMUM_FLING_VELOCITY); 
        mAnimationFling = (CustomSeekBarPreference) prefScreen.findPreference(ANIMATION_FLING_VELOCITY); 
        mAnimationFling.setValue(defaultFling); 
        mAnimationFling.setOnPreferenceChangeListener(this); 
 
        int defaultOverScroll = Settings.System.getInt(resolver, 
                Settings.System.CUSTOM_OVERSCROLL_DISTANCE, ViewConfiguration.DEFAULT_OVERSCROLL_DISTANCE); 
        mAnimationOverScroll = (CustomSeekBarPreference) prefScreen.findPreference(ANIMATION_OVERSCROLL_DISTANCE); 
        mAnimationOverScroll.setValue(defaultOverScroll); 
        mAnimationOverScroll.setOnPreferenceChangeListener(this); 
 
        int defaultOverFling = Settings.System.getInt(resolver, 
                Settings.System.CUSTOM_OVERFLING_DISTANCE, ViewConfiguration.DEFAULT_OVERFLING_DISTANCE); 
        mAnimationOverFling = (CustomSeekBarPreference) prefScreen.findPreference(ANIMATION_OVERFLING_DISTANCE); 
        mAnimationOverFling.setValue(defaultOverFling); 
        mAnimationOverFling.setOnPreferenceChangeListener(this); 
 
        setHasOptionsMenu(true); 

    }

    @Override 
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) { 
        menu.add(0, MENU_RESET, 0, R.string.reset) 
                .setIcon(R.drawable.ic_settings_backup) // use the backup icon 
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS); 
    } 
 
    @Override 
    public boolean onOptionsItemSelected(MenuItem item) { 
        switch (item.getItemId()) { 
            case MENU_RESET: 
                resetToDefault(); 
                return true; 
            default: 
                return super.onContextItemSelected(item); 
        } 
    } 
 
    private void resetToDefault() { 
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity()); 
        alertDialog.setTitle(R.string.reset); 
        alertDialog.setMessage(R.string.animation_settings_reset_message); 
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { 
            public void onClick(DialogInterface dialog, int id) { 
                resetAllValues(); 
                resetAllSettings(); 
            } 
        }); 
        alertDialog.setNegativeButton(R.string.cancel, null); 
        alertDialog.create().show(); 
    } 
 
    private void resetAllValues() { 
        mAnimationFling.setValue(ViewConfiguration.DEFAULT_MAXIMUM_FLING_VELOCITY); 
        mAnimationScroll.setValue((int) (ViewConfiguration.DEFAULT_SCROLL_FRICTION * MULTIPLIER_SCROLL_FRICTION)); 
        mAnimationOverScroll.setValue(ViewConfiguration.DEFAULT_OVERSCROLL_DISTANCE); 
        mAnimationOverFling.setValue(ViewConfiguration.DEFAULT_OVERFLING_DISTANCE); 
        mAnimNoScroll.setChecked(false); 
    } 
 
    private void resetAllSettings() { 
        setProperVal(mAnimationFling, ViewConfiguration.DEFAULT_MAXIMUM_FLING_VELOCITY); 
        Settings.System.putFloat(getActivity().getContentResolver(), 
                   Settings.System.CUSTOM_SCROLL_FRICTION, ViewConfiguration.DEFAULT_SCROLL_FRICTION); 
        setProperVal(mAnimationOverScroll, ViewConfiguration.DEFAULT_OVERSCROLL_DISTANCE); 
        setProperVal(mAnimationOverFling, ViewConfiguration.DEFAULT_OVERFLING_DISTANCE); 
        setProperVal(mAnimNoScroll, 0); 
    } 

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mToastAnimation) {
            int value = Integer.parseInt((String) newValue);
            int index = mToastAnimation.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.TOAST_ANIMATION, value, UserHandle.USER_CURRENT);
            mToastAnimation.setSummary(mToastAnimation.getEntries()[index]);
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(getActivity(), R.string.toast_animation_test,
                    Toast.LENGTH_SHORT);
            mToast.show();
            return true;
        } else if (preference == mListViewAnimation) {
            int value = Integer.parseInt((String) newValue);
            int index = mListViewAnimation.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.LISTVIEW_ANIMATION, value, UserHandle.USER_CURRENT);
            mListViewAnimation.setSummary(mListViewAnimation.getEntries()[index]);
            mListViewInterpolator.setEnabled(value > 0);
            return true;
        } else if (preference == mListViewInterpolator) {
            int value = Integer.parseInt((String) newValue);
            int index = mListViewInterpolator.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.LISTVIEW_INTERPOLATOR, value, UserHandle.USER_CURRENT);
            mListViewInterpolator.setSummary(mListViewInterpolator.getEntries()[index]);
            return true;
/*
        } else if (preference == mPowerMenuAnimation) {
            int value = Integer.parseInt((String) newValue);
            int index = mPowerMenuAnimation.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.POWER_MENU_ANIMATION, value, UserHandle.USER_CURRENT);
            mPowerMenuAnimation.setSummary(mPowerMenuAnimation.getEntries()[index]);
            return true;
*/
        } else if (preference == mTileAnimationStyle) {
            int value = Integer.valueOf((String) newValue);
            int index = mTileAnimationStyle.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver, Settings.System.ANIM_TILE_STYLE,
                    value, UserHandle.USER_CURRENT);
            mTileAnimationStyle.setSummary(mTileAnimationStyle.getEntries()[index]);
            mTileAnimationDuration.setEnabled(value > 0);
            mTileAnimationInterpolator.setEnabled(value > 0);
            return true;
       } else if (preference == mTileAnimationDuration) {
            int value = Integer.valueOf((String) newValue);
            int index = mTileAnimationDuration.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver, Settings.System.ANIM_TILE_DURATION,
                    value, UserHandle.USER_CURRENT);
            mTileAnimationDuration.setSummary(mTileAnimationDuration.getEntries()[index]);
            return true;
       } else if (preference == mTileAnimationInterpolator) {
            int value = Integer.valueOf((String) newValue);
            int index = mTileAnimationInterpolator.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver, Settings.System.ANIM_TILE_INTERPOLATOR,
                    value, UserHandle.USER_CURRENT);
            mTileAnimationInterpolator.setSummary(mTileAnimationInterpolator.getEntries()[index]);
            return true;
        } else if (preference == mScrollingCachePref) {
            String value = (String) newValue;
            int index = mScrollingCachePref.findIndexOfValue(value);
            SystemProperties.set(SCROLLINGCACHE_PERSIST_PROP, value);
            mScrollingCachePref.setSummary(mScrollingCachePref.getEntries()[index]);
            return true;
        } else if (preference == mAnimNoScroll) { 
                boolean value = (Boolean) newValue; 
                Settings.System.putInt(resolver, Settings.System.ANIMATION_CONTROLS_NO_SCROLL, value ? 1 : 0); 
            } else if (preference == mAnimationScroll) { 
                int val = ((Integer)newValue).intValue(); 
                Settings.System.putFloat(resolver, 
                       Settings.System.CUSTOM_SCROLL_FRICTION, 
                       ((float) (val / MULTIPLIER_SCROLL_FRICTION))); 
            } else if (preference == mAnimationFling) { 
                int val = ((Integer)newValue).intValue(); 
                Settings.System.putInt(resolver, 
                        Settings.System.CUSTOM_FLING_VELOCITY, 
                        val); 
            } else if (preference == mAnimationOverScroll) { 
                int val = ((Integer)newValue).intValue(); 
                Settings.System.putInt(resolver, 
                        Settings.System.CUSTOM_OVERSCROLL_DISTANCE, 
                        val); 
            } else if (preference == mAnimationOverFling) { 
                int val = ((Integer)newValue).intValue(); 
                Settings.System.putInt(resolver, 
                        Settings.System.CUSTOM_OVERFLING_DISTANCE, 
                        val); 
            }
        return false;
    }

    private void setProperVal(Preference preference, int val) { 
        String mString = ""; 
        if (preference == mAnimNoScroll) { 
            mString = Settings.System.ANIMATION_CONTROLS_NO_SCROLL; 
        } else if (preference == mAnimationFling) { 
            mString = Settings.System.CUSTOM_FLING_VELOCITY; 
        } else if (preference == mAnimationOverScroll) { 
            mString = Settings.System.CUSTOM_OVERSCROLL_DISTANCE; 
        } else if (preference == mAnimationOverFling) { 
            mString = Settings.System.CUSTOM_OVERFLING_DISTANCE; 
        } 
 
        Settings.System.putInt(getActivity().getContentResolver(), mString, val); 
    } 

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        Settings.Global.putInt(resolver,
                Settings.Global.SCREEN_OFF_ANIMATION, 0);
        Settings.System.putIntForUser(resolver,
                Settings.System.TOAST_ANIMATION, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.LISTVIEW_ANIMATION, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.LISTVIEW_INTERPOLATOR, 0, UserHandle.USER_CURRENT);
/*
        Settings.System.putIntForUser(resolver,
                Settings.System.POWER_MENU_ANIMATION, 0, UserHandle.USER_CURRENT);
*/
        Settings.System.putIntForUser(resolver,
                Settings.System.ANIM_TILE_STYLE, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.ANIM_TILE_DURATION, 2000, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.ANIM_TILE_INTERPOLATOR, 0, UserHandle.USER_CURRENT);
        SystemProperties.set(SCROLLINGCACHE_PERSIST_PROP, SCROLLINGCACHE_DEFAULT);
        AnimationControls.reset(mContext);
        DozeFragment.reset(mContext);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}