package net.sourceforge.opencamera;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import android.util.Log;

/** Handles listening for GPS location (both coarse and fine).
 */
public class LocationSupplier {
    private static final String TAG = "LocationSupplier";

    private final Context context;
    private final LocationManager locationManager;
    private MyLocationListener [] locationListeners;
    private volatile boolean test_force_no_location;

    LocationSupplier(Context context) {
        this.context = context;
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static class LocationInfo {
        private boolean location_was_cached;

        public boolean LocationWasCached() {
            return location_was_cached;
        }
    }

    public Location getLocation() {
        return null;
    }

    public Location getLocation(LocationInfo locationInfo) {
        //81dlp_gemini// Location retrieval completely disabled
        if( locationInfo != null )
            locationInfo.location_was_cached = false;
        return null;
        //81dlp_gemini//
    }

    private class MyLocationListener implements LocationListener {
        volatile boolean test_has_received_location;

        Location getLocation() {
            return null;
        }

        public void onLocationChanged(@NonNull Location location) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(@NonNull String provider) {
        }

        public void onProviderDisabled(@NonNull String provider) {
        }
    }

    /** Best to only call this from MainActivity.initLocation().
     * @return Returns false if location permission not available for either coarse or fine.
     */
    boolean setupLocationListener() {
        //81dlp_gemini// Location listener disabled; return true so caller does not trigger permission requests
        if( MyDebug.LOG )
            Log.d(TAG, "setupLocationListener: completely disabled");
        return true;
        //81dlp_gemini//
    }

    void freeLocationListeners() {
        //81dlp_gemini// No listeners are registered
        if( MyDebug.LOG )
            Log.d(TAG, "freeLocationListeners: no-op");
        locationListeners = null;
        //81dlp_gemini//
    }

    // for testing:

    public boolean testHasReceivedLocation() {
        return false;
    }

    public void setForceNoLocation(boolean test_force_no_location) {
        this.test_force_no_location = test_force_no_location;
    }

    public boolean hasLocationListeners() {
        return false;
    }

    public boolean noLocationListeners() {
        return true;
    }

    public static String locationToDMS(double coord) {
        String sign = (coord < 0.0) ? "-" : "";
        coord = Math.abs(coord);
        int intPart = (int)coord;
        boolean is_zero = (intPart==0);
        String degrees = String.valueOf(intPart);
        double mod = coord - intPart;

        coord = mod * 60;
        intPart = (int)coord;
        is_zero = is_zero && (intPart==0);
        mod = coord - intPart;
        String minutes = String.valueOf(intPart);

        coord = mod * 60;
        intPart = (int)coord;
        is_zero = is_zero && (intPart==0);
        String seconds = String.valueOf(intPart);

        if( is_zero ) {
            sign = "";
        }

        return sign + degrees + "\u00b0" + minutes + "'" + seconds + "\"";
    }
}