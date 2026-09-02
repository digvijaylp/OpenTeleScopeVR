package net.sourceforge.opencamera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashSet;

public class PreferenceSubAppInfo extends PreferenceFragment {
    private static final String TAG = "PreferenceSubAppInfo";
    private final HashSet<AlertDialog> dialogs = new HashSet<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_sub_app_info);

        final Bundle bundle = getArguments();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Privacy Policy
        Preference privacyPref = findPreference("preference_privacy_policy");
        if( privacyPref != null ) {
            privacyPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference pref) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    if( mainActivity != null ) {
                        mainActivity.launchOnlinePrivacyPolicy();
                    }
                    return false;
                }
            });
        }

        // About Dialog
        Preference aboutPref = findPreference("preference_about");
        if( aboutPref != null ) {
            aboutPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference pref) {
                    showAboutDialog(bundle, sharedPreferences);
                    return false;
                }
            });
        }

        // Online Help / Issues link
        Preference helpPref = findPreference("preference_online_help");
        if( helpPref != null ) {
            helpPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference pref) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    if( mainActivity != null ) {
                        mainActivity.launchOnlineHelp();
                    }
                    return false;
                }
            });
        }
    }

    private void showAboutDialog(Bundle bundle, SharedPreferences sharedPreferences) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.preference_about);
        final StringBuilder about_string = new StringBuilder();

        String version = "UNKNOWN_VERSION";
        int version_code = -1;
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version = pInfo.versionName;
            version_code = pInfo.versionCode;
        }
        catch(NameNotFoundException e) {
            MyDebug.logStackTrace(TAG, "NameNotFoundException getting version number", e);
        }

        about_string.append("OpenTeleScopeVR v").append(version);
        about_string.append("\nCode: ").append(version_code);
        about_string.append("\nPackage: ").append(getActivity().getPackageName());
        about_string.append("\nAndroid API: ").append(Build.VERSION.SDK_INT);
        about_string.append("\nManufacturer: ").append(Build.MANUFACTURER);
        about_string.append("\nModel: ").append(Build.MODEL);

        if( bundle != null ) {
            about_string.append("\nCurrent camera ID: ").append(bundle.getInt("cameraId"));
            about_string.append("\nNo. of cameras: ").append(bundle.getInt("nCameras"));
            about_string.append("\nCamera API: ").append(bundle.getString("camera_api"));
            about_string.append("\nPreview resolution: ").append(bundle.getInt("preview_width")).append("x").append(bundle.getInt("preview_height"));
            about_string.append("\nPhoto resolution: ").append(bundle.getInt("resolution_width")).append("x").append(bundle.getInt("resolution_height"));
        }

        SpannableString span = new SpannableString(about_string);
        @SuppressLint("InflateParams")
        final View dialog_view = LayoutInflater.from(getActivity()).inflate(R.layout.alertdialog_textview, null);
        final TextView textView = dialog_view.findViewById(R.id.text_view);
        textView.setText(span);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);

        final float scale = getActivity().getResources().getDisplayMetrics().density;
        ScrollView scrollView = new ScrollView(getActivity());
        scrollView.addView(textView);
        textView.setPadding((int)(5*scale+0.5f), (int)(5*scale+0.5f), (int)(5*scale+0.5f), (int)(5*scale+0.5f));
        scrollView.setPadding((int)(14*scale+0.5f), (int)(2*scale+0.5f), (int)(10*scale+0.5f), (int)(12*scale+0.5f));
        alertDialog.setView(scrollView);

        alertDialog.setPositiveButton(android.R.string.ok, null);
        alertDialog.setNegativeButton(R.string.about_copy_to_clipboard, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("OpenTeleScopeVR About", about_string);
                clipboard.setPrimaryClip(clip);
            }
        });

        final AlertDialog alert = alertDialog.create();
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                dialogs.remove(alert);
            }
        });
        alert.show();
        dialogs.add(alert);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyPreferenceFragment.setBackground(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if( getArguments() != null && getArguments().getBoolean("edge_to_edge_mode") ) {
            MyPreferenceFragment.handleEdgeToEdge(view);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for(AlertDialog dialog : dialogs) {
            dialog.dismiss();
        }
    }
}