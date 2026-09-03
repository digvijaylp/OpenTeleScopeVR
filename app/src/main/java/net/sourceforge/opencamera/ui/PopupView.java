package net.sourceforge.opencamera.ui;

import net.sourceforge.opencamera.MainActivity;
import net.sourceforge.opencamera.MyApplicationInterface;
import net.sourceforge.opencamera.MyDebug;
import net.sourceforge.opencamera.PreferenceKeys;
import net.sourceforge.opencamera.R;
import net.sourceforge.opencamera.cameracontroller.CameraController;
import net.sourceforge.opencamera.preview.Preview;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

/** This defines the UI for the "popup" button, that provides quick access to a
 *  range of options.
 */
public class PopupView extends LinearLayout {
    private static final String TAG = "PopupView";
    public static final float ALPHA_BUTTON_SELECTED = 1.0f;
    public static final float ALPHA_BUTTON = 0.54f;

    private static final float button_text_size_dip = 12.0f;
    private static final float title_text_size_dip = 17.0f;
    private static final float standard_text_size_dip = 16.0f;
    private static final float arrow_text_size_dip = 16.0f;
    private static final float arrow_button_w_dp = 60.0f;
    private static final float arrow_button_h_dp = 48.0f;
    private final int arrow_button_w;
    private final int arrow_button_h;

    private int total_width_dp;

    @SuppressWarnings("FieldCanBeLocal")
    private final DecimalFormat decimal_format_1dp_force0 = new DecimalFormat("0.0");

    public PopupView(Context context) {
        super(context);
        if( MyDebug.LOG )
            Log.d(TAG, "new PopupView: " + this);

        final long debug_time = System.nanoTime();
        this.setOrientation(LinearLayout.VERTICAL);

        final float scale = getResources().getDisplayMetrics().density;
        arrow_button_w = (int) (arrow_button_w_dp * scale + 0.5f);
        arrow_button_h = (int) (arrow_button_h_dp * scale + 0.5f);

        final MainActivity main_activity = (MainActivity)this.getContext();

        boolean small_screen = false;
        total_width_dp = 280;
        int max_width_dp = main_activity.getMainUI().getMaxHeightDp(false);
        if( total_width_dp > max_width_dp ) {
            total_width_dp = max_width_dp;
            small_screen = true;
        }

        final Preview preview = main_activity.getPreview();
        boolean is_camera_extension = main_activity.getApplicationInterface().isCameraExtensionPref();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(main_activity);

        //81dlp_gemini// Only show Color Effects in popup with permanent header
        if( preview.getCameraController() != null && !is_camera_extension ) {
            List<String> supported_color_effects = preview.getSupportedColorEffects();
            List<String> supported_color_effects_entries = null;
            if( supported_color_effects != null ) {
                supported_color_effects_entries = new ArrayList<>();
                for(String value : supported_color_effects) {
                    String entry = main_activity.getMainUI().getEntryForColorEffect(value);
                    supported_color_effects_entries.add(entry);
                }
            }
            addRadioOptionsToPopup(sharedPreferences, supported_color_effects_entries, supported_color_effects, getResources().getString(R.string.color_effect), PreferenceKeys.ColorEffectPreferenceKey, CameraController.COLOR_EFFECT_DEFAULT, null, "TEST_COLOR_EFFECT", new RadioOptionsListener() {
                @Override
                public void onClick(String selected_value) {
                    if( preview.getCameraController() != null ) {
                        preview.getCameraController().setColorEffect(selected_value);
                    }
                }
            });
        }
        //81dlp_gemini//

        if( MyDebug.LOG )
            Log.d(TAG, "Overall PopupView time: " + (System.nanoTime() - debug_time));
    }

    int getTotalWidth() {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (total_width_dp * scale + 0.5f);
    }

    static abstract class ButtonOptionsPopupListener {
        public abstract void onClick(String option);
    }

    private void addCheckBox(Context context, float scale, CharSequence text, boolean checked, CompoundButton.OnCheckedChangeListener listener) {
        @SuppressLint("InflateParams")
        final View switch_view = LayoutInflater.from(context).inflate(R.layout.popupview_switch, null);
        final SwitchCompat checkBox = switch_view.findViewById(R.id.popupview_switch);
        checkBox.setText(text);
        {
            checkBox.setGravity(Gravity.RIGHT);
            LayoutParams params = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
            );
            final int right_padding = (int) (20 * scale + 0.5f);
            params.setMargins(0, 0, right_padding, 0);
            checkBox.setLayoutParams(params);
        }
        if( checked )
            checkBox.setChecked(checked);
        checkBox.setOnCheckedChangeListener(listener);
        this.addView(checkBox);
    }

    private void addButtonOptionsToPopup(List<String> supported_options, int icons_id, int values_id, String prefix_string, String current_value, int max_buttons_per_row, String test_key, final ButtonOptionsPopupListener listener) {
        MainActivity main_activity = (MainActivity)this.getContext();
        createButtonOptions(this, this.getContext(), total_width_dp, main_activity.getMainUI().getTestUIButtonsMap(), supported_options, icons_id, values_id, prefix_string, true, current_value, max_buttons_per_row, test_key, listener);
    }

    public static String getButtonOptionString(boolean include_prefix, String prefix_string, String supported_option) {
        return (include_prefix ? (prefix_string + "\n") : "") + supported_option;
    }

    static List<View> createButtonOptions(ViewGroup parent, Context context, int total_width_dp, Map<String, View> test_ui_buttons, List<String> supported_options, int icons_id, int values_id, String prefix_string, boolean include_prefix, String current_value, int max_buttons_per_row, String test_key, final ButtonOptionsPopupListener listener) {
        final List<View> buttons = new ArrayList<>();
        if( supported_options != null ) {
            LinearLayout ll2 = new LinearLayout(context);
            ll2.setOrientation(LinearLayout.HORIZONTAL);
            try(TypedArray icons = icons_id != -1 ? context.getResources().obtainTypedArray(icons_id) : null) {
                String [] values = values_id != -1 ? context.getResources().getStringArray(values_id) : null;

                final float scale = context.getResources().getDisplayMetrics().density;
                final float scale_font = context.getResources().getDisplayMetrics().scaledDensity;
                int actual_max_per_row = supported_options.size();
                if( max_buttons_per_row > 0 )
                    actual_max_per_row = Math.min(actual_max_per_row, max_buttons_per_row);
                int button_width_dp = total_width_dp/actual_max_per_row;
                boolean use_scrollview = false;
                final int min_button_width_dp = 48;
                if( button_width_dp < min_button_width_dp && max_buttons_per_row == 0 ) {
                    button_width_dp = min_button_width_dp;
                    use_scrollview = true;
                }
                int button_width = (int)(button_width_dp * scale + 0.5f);

                View.OnClickListener on_click_listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String supported_option = (String)v.getTag();
                        listener.onClick(supported_option);
                    }
                };
                View current_view = null;

                for(int button_indx=0;button_indx<supported_options.size();button_indx++) {
                    final String supported_option = supported_options.get(button_indx);

                    if( max_buttons_per_row > 0 && button_indx > 0 && button_indx % max_buttons_per_row == 0 ) {
                        parent.addView(ll2);
                        ll2 = new LinearLayout(context);
                        ll2.setOrientation(LinearLayout.HORIZONTAL);

                        int n_remaining = supported_options.size() - button_indx;
                        if( n_remaining <= max_buttons_per_row ) {
                            button_width_dp = total_width_dp/n_remaining;
                            button_width = (int)(button_width_dp * scale + 0.5f);
                        }
                    }

                    int resource = -1;
                    if( icons != null && values != null ) {
                        int index = -1;
                        for(int i=0;i<values.length && index==-1;i++) {
                            if( values[i].equals(supported_option) )
                                index = i;
                        }
                        if( index != -1 ) {
                            resource = icons.getResourceId(index, 0);
                        }
                    }

                    String button_string;
                    if( prefix_string.isEmpty() ) {
                        button_string = supported_option;
                    }
                    else if( prefix_string.equalsIgnoreCase("ISO") && supported_option.length() >= 4 && supported_option.substring(0, 4).equalsIgnoreCase("ISO_") ) {
                        button_string = getButtonOptionString(include_prefix, prefix_string, supported_option.substring(4));
                    }
                    else if( prefix_string.equalsIgnoreCase("ISO") && supported_option.length() >= 3 && supported_option.substring(0, 3).equalsIgnoreCase("ISO") ) {
                        button_string = getButtonOptionString(include_prefix, prefix_string, supported_option.substring(3));
                    }
                    else {
                        button_string = getButtonOptionString(include_prefix, prefix_string, supported_option);
                    }
                    View view;
                    if( resource != -1 ) {
                        ImageButton image_button = new ImageButton(context);
                        view = image_button;
                        buttons.add(view);
                        ll2.addView(view);

                        final MainActivity main_activity = (MainActivity)context;
                        Bitmap bm = main_activity.getPreloadedBitmap(resource);
                        if( bm != null )
                            image_button.setImageBitmap(bm);
                        image_button.setScaleType(ScaleType.FIT_CENTER);
                        image_button.setBackgroundColor(Color.TRANSPARENT);
                        final int padding = (int) (10 * scale + 0.5f);
                        view.setPadding(padding, padding, padding, padding);
                    }
                    else {
                        @SuppressLint("InflateParams")
                        final View button_view = LayoutInflater.from(context).inflate(R.layout.popupview_button, null);
                        final Button button = button_view.findViewById(R.id.button);

                        button.setBackgroundColor(Color.TRANSPARENT);
                        view = button;
                        buttons.add(view);
                        ll2.addView(view);

                        button.setText(button_string);
                        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, button_text_size_dip);
                        button.setTextColor(Color.WHITE);
                        final int padding = (int) (0 * scale + 0.5f);
                        view.setPadding(padding, padding, padding, padding);
                    }

                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.width = button_width;
                    params.height = (int) (55 * ((resource != -1) ? scale : scale_font) + 0.5f);
                    view.setLayoutParams(params);

                    view.setContentDescription(button_string);
                    if( supported_option.equals(current_value) ) {
                        setButtonSelected(view, true);
                        current_view = view;
                    }
                    else {
                        setButtonSelected(view, false);
                    }
                    view.setTag(supported_option);
                    view.setOnClickListener(on_click_listener);
                    if( test_ui_buttons != null )
                        test_ui_buttons.put(test_key + "_" + supported_option, view);
                }
                if( use_scrollview ) {
                    final int total_width = (int) (total_width_dp * scale + 0.5f);
                    final HorizontalScrollView scroll = new HorizontalScrollView(context);
                    scroll.addView(ll2);
                    {
                        ViewGroup.LayoutParams params = new LayoutParams(
                                total_width,
                                LayoutParams.WRAP_CONTENT);
                        scroll.setLayoutParams(params);
                    }
                    parent.addView(scroll);
                    if( current_view != null ) {
                        final View final_current_view = current_view;
                        final int final_button_width = button_width;
                        parent.getViewTreeObserver().addOnGlobalLayoutListener(
                                new OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        int jump_x = final_current_view.getLeft() - (total_width-final_button_width)/2;
                                        jump_x = Math.min(jump_x, total_width-1);
                                        if( jump_x > 0 ) {
                                            scroll.scrollTo(jump_x, 0);
                                        }
                                    }
                                }
                        );
                    }
                }
                else {
                    parent.addView(ll2);
                }
            }
        }
        return buttons;
    }

    static void setButtonSelected(View view, boolean selected) {
        view.setAlpha(selected ? ALPHA_BUTTON_SELECTED : ALPHA_BUTTON);
    }

    private void addTitleToPopup(final String title) {
        @SuppressLint("InflateParams")
        final View view = LayoutInflater.from(this.getContext()).inflate(R.layout.popupview_textview, null);
        final TextView text_view = view.findViewById(R.id.text_view);

        text_view.setText(title + ":");
        text_view.setTextSize(TypedValue.COMPLEX_UNIT_SP, title_text_size_dip);
        text_view.setTypeface(null, Typeface.BOLD);
        this.addView(text_view);
    }

    private abstract static class RadioOptionsListener {
        protected abstract void onClick(String selected_value);
    }

    private void addRadioOptionsToPopup(final SharedPreferences sharedPreferences, final List<String> supported_options_entries, final List<String> supported_options_values, final String title, final String preference_key, final String default_value, final String current_option_value, final String test_key, final RadioOptionsListener listener) {
        if( supported_options_entries != null ) {
            final MainActivity main_activity = (MainActivity)this.getContext();

            //81dlp_gemini// Permanent title header; options rendered directly with no collapse listener
            addTitleToPopup(title);

            final RadioGroup rg = new RadioGroup(this.getContext());
            rg.setOrientation(RadioGroup.VERTICAL);
            rg.setVisibility(View.VISIBLE);
            main_activity.getMainUI().getTestUIButtonsMap().put(test_key, rg);

            addRadioOptionsToGroup(rg, sharedPreferences, supported_options_entries, supported_options_values, title, preference_key, default_value, current_option_value, test_key, listener);

            this.addView(rg);
            //81dlp_gemini//
        }
    }

    private void addRadioOptionsToGroup(final RadioGroup rg, SharedPreferences sharedPreferences, List<String> supported_options_entries, List<String> supported_options_values, final String title, final String preference_key, final String default_value, String current_option_value, final String test_key, final RadioOptionsListener listener) {
        if( preference_key != null )
            current_option_value = sharedPreferences.getString(preference_key, default_value);
        final MainActivity main_activity = (MainActivity)this.getContext();
        int count = 0;
        for(int i=0;i<supported_options_entries.size();i++) {
            final String supported_option_entry = supported_options_entries.get(i);
            final String supported_option_value = supported_options_values.get(i);

            @SuppressLint("InflateParams")
            final View view = LayoutInflater.from(this.getContext()).inflate(R.layout.popupview_radiobutton, null);
            final RadioButton button = view.findViewById(R.id.popupview_radiobutton);

            button.setId(count);
            button.setText(supported_option_entry);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, standard_text_size_dip);
            button.setTextColor(Color.WHITE);
            rg.addView(button);

            if( supported_option_value.equals(current_option_value) ) {
                rg.check(count);
            }
            count++;

            button.setContentDescription(supported_option_entry);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( preference_key != null ) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(main_activity);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(preference_key, supported_option_value);
                        editor.apply();
                    }

                    if( listener != null ) {
                        listener.onClick(supported_option_value);
                    }
                    else {
                        main_activity.updateForSettings(true, title + ": " + supported_option_entry);
                        main_activity.closePopup();
                    }
                }
            });
            main_activity.getMainUI().getTestUIButtonsMap().put(test_key + "_" + supported_option_value, button);
        }
    }
}