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
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.app.DialogFragment; 
import android.content.DialogInterface; 
import android.app.AlertDialog; 
import android.app.Dialog; 

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.development.DevelopmentSettings;
import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.SettingsPreferenceFragment;

import com.havoc.settings.R;

public class Sounds extends SettingsPreferenceFragment implements
Preference.OnPreferenceChangeListener {


    public static final String TAG = "Sound";

    private static final String HEADSET_CONNECT_PLAYER = "headset_connect_player"; 
    private SwitchPreference mSafeHeadsetVolume; 
    private SwitchPreference mCameraSounds; 
 
    private static final String KEY_CAMERA_SOUNDS = "camera_sounds"; 
    private static final String PROP_CAMERA_SOUND = "persist.sys.camera-sound"; 
    private static final String KEY_SAFE_HEADSET_VOLUME = "safe_headset_volume"; 
    private static final int DLG_SAFE_HEADSET_VOLUME = 0; 
    private static final int DLG_CAMERA_SOUND = 1; 

    private ListPreference mNoisyNotification;
    private ListPreference mLaunchPlayerHeadsetConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.havoc_settings_sounds);

        ContentResolver resolver = getActivity().getContentResolver();

        mNoisyNotification = (ListPreference) findPreference("notification_sound_vib_screen_on");
        int mode = Settings.System.getIntForUser(resolver,
                Settings.System.NOTIFICATION_SOUND_VIB_SCREEN_ON,
                1, UserHandle.USER_CURRENT);
        mNoisyNotification.setValue(String.valueOf(mode));
        mNoisyNotification.setSummary(mNoisyNotification.getEntry());
        mNoisyNotification.setOnPreferenceChangeListener(this);

        mLaunchPlayerHeadsetConnection = (ListPreference) findPreference(HEADSET_CONNECT_PLAYER); 
        int mLaunchPlayerHeadsetConnectionValue = Settings.System.getIntForUser(resolver, 
                Settings.System.HEADSET_CONNECT_PLAYER, 0, UserHandle.USER_CURRENT); 
        mLaunchPlayerHeadsetConnection.setValue(Integer.toString(mLaunchPlayerHeadsetConnectionValue)); 
        mLaunchPlayerHeadsetConnection.setSummary(mLaunchPlayerHeadsetConnection.getEntry()); 
        mLaunchPlayerHeadsetConnection.setOnPreferenceChangeListener(this); 

        
        mCameraSounds = (SwitchPreference) findPreference(KEY_CAMERA_SOUNDS); 
        mCameraSounds.setChecked(SystemProperties.getBoolean(PROP_CAMERA_SOUND, true)); 
        mCameraSounds.setOnPreferenceChangeListener(this); 
 
        mSafeHeadsetVolume = (SwitchPreference) findPreference(KEY_SAFE_HEADSET_VOLUME); 
        mSafeHeadsetVolume.setChecked(Settings.System.getInt(getActivity().getContentResolver(), 
                Settings.System.SAFE_HEADSET_VOLUME, 1) != 0); 
        mSafeHeadsetVolume.setOnPreferenceChangeListener(this); 
    } 

    private void showDialogInner(int id) { 
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id); 
        newFragment.setTargetFragment(this, 0); 
        newFragment.show(getFragmentManager(), "dialog " + id); 
    } 

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey(); 
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNoisyNotification) {
            int value = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.NOTIFICATION_SOUND_VIB_SCREEN_ON, value, UserHandle.USER_CURRENT);
            int index = mNoisyNotification.findIndexOfValue((String) newValue);
            mNoisyNotification.setSummary(
                    mNoisyNotification.getEntries()[index]);
            return true;
        } else if (preference == mLaunchPlayerHeadsetConnection) { 
            int mLaunchPlayerHeadsetConnectionValue = Integer.valueOf((String) newValue); 
            int index = mLaunchPlayerHeadsetConnection.findIndexOfValue((String) newValue); 
            mLaunchPlayerHeadsetConnection.setSummary( 
                    mLaunchPlayerHeadsetConnection.getEntries()[index]); 
            Settings.System.putIntForUser(resolver, Settings.System.HEADSET_CONNECT_PLAYER, 
                    mLaunchPlayerHeadsetConnectionValue, UserHandle.USER_CURRENT); 
            return true; 
        }  else  if (KEY_CAMERA_SOUNDS.equals(key)) { 
                if ((Boolean) newValue) { 
                    SystemProperties.set(PROP_CAMERA_SOUND, "1"); 
                } else { 
                    showDialogInner(DLG_CAMERA_SOUND); 
                } 
                return true; 
             } else if (KEY_SAFE_HEADSET_VOLUME.equals(key)) { 
                 if ((Boolean) newValue) { 
                     Settings.System.putInt(getActivity().getContentResolver(), 
                             Settings.System.SAFE_HEADSET_VOLUME, 1); 
                 } else { 
                     showDialogInner(DLG_SAFE_HEADSET_VOLUME); 
                 } 
                 return true; 
        }
        return false;
    }

    public static class MyAlertDialogFragment extends DialogFragment { 
     
        public static MyAlertDialogFragment newInstance(int id) { 
            MyAlertDialogFragment frag = new MyAlertDialogFragment(); 
            Bundle args = new Bundle(); 
            args.putInt("id", id); 
            frag.setArguments(args); 
            return frag; 
        } 

        Sounds getOwner() { 
            return (Sounds) getTargetFragment(); 
        } 
 
 
        @Override 
        public Dialog onCreateDialog(Bundle savedInstanceState) { 
            int id = getArguments().getInt("id"); 
            switch (id) { 
                case DLG_SAFE_HEADSET_VOLUME: 
                    return new AlertDialog.Builder(getActivity()) 
                    .setTitle(R.string.attention) 
                    .setMessage(R.string.safe_headset_volume_warning_dialog_text) 
                    .setPositiveButton(R.string.ok, 
                        new DialogInterface.OnClickListener() { 
                        public void onClick(DialogInterface dialog, int which) { 
                            Settings.System.putInt(getOwner().getActivity().getContentResolver(), 
                                    Settings.System.SAFE_HEADSET_VOLUME, 0); 
 
                            } 
                    }) 
                    .setNegativeButton(R.string.cancel, 
                        new DialogInterface.OnClickListener() { 
                        public void onClick(DialogInterface dialog, int which) { 
                            dialog.cancel(); 
                        } 
                    }) 
                    .create(); 
                case DLG_CAMERA_SOUND: 
                    return new AlertDialog.Builder(getActivity()) 
                    .setTitle(R.string.attention) 
                    .setMessage(R.string.camera_sound_warning_dialog_text) 
                    .setPositiveButton(R.string.ok, 
                        new DialogInterface.OnClickListener() { 
                        public void onClick(DialogInterface dialog, int which) { 
                            SystemProperties.set(PROP_CAMERA_SOUND, "0"); 
                        } 
                    }) 
                    .setNegativeButton(R.string.cancel, 
                        new DialogInterface.OnClickListener() { 
                        public void onClick(DialogInterface dialog, int which) { 
                            dialog.cancel(); 
                        } 
                    }) 
                    .create(); 
            } 
            throw new IllegalArgumentException("unknown id " + id); 
        } 
 
        @Override 
        public void onCancel(DialogInterface dialog) { 
            int id = getArguments().getInt("id"); 
            switch (id) { 
                case DLG_SAFE_HEADSET_VOLUME: 
                    getOwner().mSafeHeadsetVolume.setChecked(true); 
                    break; 
                case DLG_CAMERA_SOUND: 
                    getOwner().mCameraSounds.setChecked(true); 
                    break; 
            } 
        } 
    } 

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        Settings.System.putIntForUser(resolver,
                Settings.System.NOTIFICATION_SOUND_VIB_SCREEN_ON, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.SCREENSHOT_SOUND, 1, UserHandle.USER_CURRENT);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
