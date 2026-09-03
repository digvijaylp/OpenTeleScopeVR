package net.sourceforge.opencamera;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.util.Log;

/** Android 6+ permission handling:
 */
public class PermissionHandler {
    private static final String TAG = "PermissionHandler";

    private final MainActivity main_activity;

    final private static int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    final private static int MY_PERMISSIONS_REQUEST_STORAGE = 1;
    final private static int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 2;
    final private static int MY_PERMISSIONS_REQUEST_LOCATION = 3;

    private boolean camera_denied; // whether the user requested to deny a camera permission
    private long camera_denied_time_ms; // if denied, the time when this occurred
    private boolean storage_denied; // whether the user requested to deny a camera permission
    private long storage_denied_time_ms; // if denied, the time when this occurred
    private boolean audio_denied; // whether the user requested to deny a camera permission
    private long audio_denied_time_ms; // if denied, the time when this occurred
    // private boolean location_denied;
    // private long location_denied_time_ms;

    final private static long deny_delay_ms = 1000;

    PermissionHandler(MainActivity main_activity) {
        this.main_activity = main_activity;
    }

    /** Show a "rationale" to the user for needing a particular permission, then request that permission again
     *  once they close the dialog.
     */
    private void showRequestPermissionRationale(final int permission_code) {
        if( MyDebug.LOG )
            Log.d(TAG, "showRequestPermissionRational: " + permission_code);

        boolean ok = true;
        String [] permissions = null;
        int message_id = 0;
        switch (permission_code) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (MyDebug.LOG)
                    Log.d(TAG, "display rationale for camera permission");
                permissions = new String[]{Manifest.permission.CAMERA};
                message_id = R.string.permission_rationale_camera;
                break;
            case MY_PERMISSIONS_REQUEST_STORAGE:
                if (MyDebug.LOG)
                    Log.d(TAG, "display rationale for storage permission");
                permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                message_id = R.string.permission_rationale_storage;
                break;
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO:
                if (MyDebug.LOG)
                    Log.d(TAG, "display rationale for record audio permission");
                permissions = new String[]{Manifest.permission.RECORD_AUDIO};
                message_id = R.string.permission_rationale_record_audio;
                break;
            //81dlp_gemini// Location rationale removed
            /*
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (MyDebug.LOG)
                    Log.d(TAG, "display rationale for location permission");
                permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                message_id = R.string.permission_rationale_location;
                break;
            */
            default:
                if (MyDebug.LOG)
                    Log.e(TAG, "showRequestPermissionRational unknown permission_code: " + permission_code);
                ok = false;
                break;
        }

        if( ok ) {
            final String [] permissions_f = permissions;
            new AlertDialog.Builder(main_activity)
            .setTitle(R.string.permission_rationale_title)
            .setMessage(message_id)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.ok, null)
            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    if( MyDebug.LOG )
                        Log.d(TAG, "requesting permission...");
                    ActivityCompat.requestPermissions(main_activity, permissions_f, permission_code);
                }
            }).show();
        }
    }

    void requestCameraPermission() {
        if( MyDebug.LOG )
            Log.d(TAG, "requestCameraPermission");
        if( camera_denied && System.currentTimeMillis() < camera_denied_time_ms + deny_delay_ms ) {
            if( MyDebug.LOG )
                Log.d(TAG, "too soon since user last denied permission");
            return;
        }

        if( ActivityCompat.shouldShowRequestPermissionRationale(main_activity, Manifest.permission.CAMERA) ) {
            showRequestPermissionRationale(MY_PERMISSIONS_REQUEST_CAMERA);
        }
        else {
            if( MyDebug.LOG )
                Log.d(TAG, "requesting camera permission...");
            ActivityCompat.requestPermissions(main_activity, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    void requestStoragePermission() {
        if( MyDebug.LOG )
            Log.d(TAG, "requestStoragePermission");
        if( MainActivity.useScopedStorage() ) {
            if( MyDebug.LOG )
                Log.e(TAG, "shouldn't be requesting permissions for scoped storage!");
            return;
        }
        else if( storage_denied && System.currentTimeMillis() < storage_denied_time_ms + deny_delay_ms ) {
            if( MyDebug.LOG )
                Log.d(TAG, "too soon since user last denied permission");
            return;
        }

        if( ActivityCompat.shouldShowRequestPermissionRationale(main_activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
            showRequestPermissionRationale(MY_PERMISSIONS_REQUEST_STORAGE);
        }
        else {
            if( MyDebug.LOG )
                Log.d(TAG, "requesting storage permission...");
            ActivityCompat.requestPermissions(main_activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
        }
    }

    void requestRecordAudioPermission() {
        if( MyDebug.LOG )
            Log.d(TAG, "requestRecordAudioPermission");
        if( audio_denied && System.currentTimeMillis() < audio_denied_time_ms + deny_delay_ms ) {
            if( MyDebug.LOG )
                Log.d(TAG, "too soon since user last denied permission");
            return;
        }

        if( ActivityCompat.shouldShowRequestPermissionRationale(main_activity, Manifest.permission.RECORD_AUDIO) ) {
            showRequestPermissionRationale(MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
        else {
            if( MyDebug.LOG )
                Log.d(TAG, "requesting record audio permission...");
            ActivityCompat.requestPermissions(main_activity, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
    }

    void requestLocationPermission() {
        //81dlp_gemini// Completely disabled: never request location permissions
        if( MyDebug.LOG )
            Log.d(TAG, "requestLocationPermission: disabled");
        /*
        if( location_denied && System.currentTimeMillis() < location_denied_time_ms + deny_delay_ms ) {
            if( MyDebug.LOG )
                Log.d(TAG, "too soon since user last denied permission");
            return;
        }

        if( ActivityCompat.shouldShowRequestPermissionRationale(main_activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(main_activity, Manifest.permission.ACCESS_COARSE_LOCATION) ) {
            showRequestPermissionRationale(MY_PERMISSIONS_REQUEST_LOCATION);
        }
        else {
            if( MyDebug.LOG )
                Log.d(TAG, "requesting location permissions...");
            ActivityCompat.requestPermissions(main_activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
        */
        //81dlp_gemini//
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        if( MyDebug.LOG )
            Log.d(TAG, "onRequestPermissionsResult: requestCode " + requestCode);

        switch( requestCode ) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
            {
                if( grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    if( MyDebug.LOG )
                        Log.d(TAG, "camera permission granted");
                    main_activity.getPreview().retryOpenCamera();
                }
                else {
                    if( MyDebug.LOG )
                        Log.d(TAG, "camera permission denied");
                    camera_denied = true;
                    camera_denied_time_ms = System.currentTimeMillis();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_STORAGE:
            {
                if( grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    if( MyDebug.LOG )
                        Log.d(TAG, "storage permission granted");
                    main_activity.getPreview().retryOpenCamera();
                }
                else {
                    if( MyDebug.LOG )
                        Log.d(TAG, "storage permission denied");
                    storage_denied = true;
                    storage_denied_time_ms = System.currentTimeMillis();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO:
            {
                if( grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    if( MyDebug.LOG )
                        Log.d(TAG, "record audio permission granted");
                }
                else {
                    if( MyDebug.LOG )
                        Log.d(TAG, "record audio permission denied");
                    audio_denied = true;
                    audio_denied_time_ms = System.currentTimeMillis();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                //81dlp_gemini// Location callback ignored completely
                return;
            }
            default:
            {
                if( MyDebug.LOG )
                    Log.e(TAG, "unknown requestCode " + requestCode);
            }
        }
    }
}