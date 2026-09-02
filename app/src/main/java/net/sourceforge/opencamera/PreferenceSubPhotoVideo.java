package net.sourceforge.opencamera;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;

public class PreferenceSubPhotoVideo extends PreferenceFragment {
    private static final String TAG = "PreferenceSubPhotoVideo";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_sub_photo_video);
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
}